package com.mosaic.io.journal;

import com.mosaic.io.CheckSumException;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.time.Duration;
import com.softwaremosaic.junit.JUnitMosaic;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.internal.runners.model.MultipleFailureException;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicLong;

import static java.util.Arrays.asList;


@RunWith(JUnitMosaicRunner.class)
public class Journal2Test extends Tests {

    // Size each journal file to be able to store exactly 20 transactions before overflowing
    private static final long TRANSACTION_COUNT_PERDATAFILE = 20;
    private static final long JOURNAL_FILE_SIZE             = Journal2.FILEHEADER_SIZE + Journal2.FILEFOOTER_SIZE +
        + TRANSACTION_COUNT_PERDATAFILE*(Transaction2.SIZE_BYTES+Journal2.PER_MSGHEADER_SIZE);


    private Journal2       journal     = new Journal2( dataDir, "junitJournal", JOURNAL_FILE_SIZE );

    private JournalReader2 reader      = journal.createReader();
    private JournalWriter2 writer      = journal.createWriter();
    private Transaction2   transaction = new Transaction2();


    public Journal2Test() {
        system.registerServicesAfter( reader, writer );
    }

// EMPTY JOURNAL

    @Test
    public void givenNoJournal_createReader_expectNoMessages() {
        assertFalse( reader.readNextInto(transaction) );
    }

    @Test
    public void givenNoJournal_createReaderAndSeekForward_expectSeekToFail() {
        assertFalse( reader.seekTo(5) );
    }


// MESSAGES THAT FIT IN SINGLE DATA FILE

    @Test( threadCheck=true )
    public void givenEmptyJournal_addMessage_expectReaderToReceiveIt() {
        writeMessage( 11, 12, 13 );

        assertNextMessageIs( 0, 11, 12, 13 );
        assertFalse( reader.readNextInto(transaction) );
    }

    @Test
    public void givenEmptyJournal_addMessages_expectReaderToReceiveThem() {
        writeMessage( 11, 12, 13 );
        writeMessage( 21, 22, 23 );

        assertNextMessageIs( 0, 11, 12, 13 );
        assertNextMessageIs( 1, 21, 22, 23 );
        assertFalse( reader.readNextInto(transaction) );
    }

    @Test
    public void givenEmptyJournalAndMultipleReaders_addMessages_expectReadersToReceiveThem() {
        JournalReader2 reader1 = createAndRegisterReader();
        JournalReader2 reader2 = createAndRegisterReader();

        writeMessage( 11, 12, 13 );
        writeMessage( 21, 22, 23 );


        for ( JournalReader2 r : asList(reader1, reader2, reader) ) {
            assertNextMessageIs( r, 0, 11, 12, 13 );
            assertNextMessageIs( r, 1, 21, 22, 23 );

            assertFalse( r.readNextInto(transaction) );
        }
    }

    @Test
    public void givenQueueWithMessages_startReader_expectReaderToReceiveMessages() {
        writeMessage( 11, 12, 13 );
        writeMessage( 21, 22, 23 );

        JournalReader2 reader1 = createAndRegisterReader();

        for ( JournalReader2 r : asList(reader1,reader) ) {
            assertNextMessageIs( r, 0, 11, 12, 13 );
            assertNextMessageIs( r, 1, 21, 22, 23 );

            assertFalse( r.readNextInto( transaction ) );
        }
    }

    @Test
    public void givenQueueWithMessages_startReaderPartWayThrough_expectReaderToReceiveMessages() {
        writeMessage( 11, 12, 13 );
        writeMessage( 21, 22, 23 );
        writeMessage( 31, 32, 33 );
        writeMessage( 41, 42, 43 );


        assertTrue( reader.seekTo(2) );
        assertNextMessageIs( 2, 31, 32, 33 );
        assertNextMessageIs( 3, 41, 42, 43 );

        assertFalse( reader.readNextInto( transaction ) );
    }


// ROLL OVER DATA FILE

    @Test
    public void addEnoughMessagesToExactlyMatchSingleDataFileSize_expectNoExtraDataFiles() {
        for ( long seq=0; seq<TRANSACTION_COUNT_PERDATAFILE; seq++ ) {
            writeMessage( seq );
        }

        for ( long seq=0; seq<TRANSACTION_COUNT_PERDATAFILE; seq++ ) {
            assertNextMessageIs( seq );
        }

        assertFalse( reader.readNextInto(transaction) );

        assertEquals( "expected only one data file to be created", 1, dataDir.files().size() );
    }

    @Test
    public void addEnoughMessagesToOverFlowFirstDataFile_expectSecondFileToBeCreated() {
        for ( long seq=0; seq<TRANSACTION_COUNT_PERDATAFILE; seq++ ) {
            writeMessage( seq );
        }

        writeMessage( TRANSACTION_COUNT_PERDATAFILE );

        for ( long seq=0; seq<TRANSACTION_COUNT_PERDATAFILE; seq++ ) {
            assertNextMessageIs( seq );
        }

        assertNextMessageIs( TRANSACTION_COUNT_PERDATAFILE );

        assertFalse( reader.readNextInto( transaction ) );
        assertEquals( "expected two data files to be created", 2, dataDir.files().size() );
    }

    @Test
    public void addEnoughMessagesToOverFlowTwoDataFiles_expectThirdFileToBeCreated() {
        long numMessages = TRANSACTION_COUNT_PERDATAFILE*2 + 1;

        for ( long seq=0; seq<numMessages; seq++ ) {
            writeMessage( seq );
        }

        for ( long seq=0; seq<numMessages; seq++ ) {
            assertNextMessageIs( seq );
        }

        assertFalse( reader.readNextInto( transaction ) );
        assertEquals( "expected three data files to be created", 3, dataDir.files().size() );
    }


// CONCURRENT

    @Test
    public void concurrentlyWriteToAndReadFromJournal_expectTheSameBehaviourAsTheSingleThreadedTest() throws MultipleFailureException {
        long numFullFiles = 20;
        long numMessages  = TRANSACTION_COUNT_PERDATAFILE*numFullFiles + 1;

        AtomicLong writeCount = new AtomicLong();
        AtomicLong readCount  = new AtomicLong();


        Runnable job1 = () -> {
            for ( long seq=0; seq<numMessages; seq++ ) {
                writeMessage( seq );

                writeCount.incrementAndGet();


                Backdoor.sleep( Duration.millis( 1 ) );  // spreads the writes out
            }
        };

        Runnable job2 = () -> {
            Transaction2 t = new Transaction2();

            for ( long seq=0; seq<numMessages; seq++ ) {
                JUnitMosaic.spinUntilTrue( () -> reader.readNextInto(t) );

                assertEquals( expectedFrom(seq), t.getFrom() );
                assertEquals( expectedTo(seq), t.getTo() );
                assertEquals( expectedAmount(seq), t.getAmount(), 1e-6 );

                readCount.incrementAndGet();


                // spreads the reads out, so writes occur apx 2*faster than reads
                // this creates a reasonable interleaving of writes and reads
                Backdoor.sleep( Duration.millis(2) );
            }
        };

        JUnitMosaic.runConcurrentlyAndWaitFor(
            "Journal2Test.concurrentlyWriteToAndReadFromJournal_expectTheSameBehaviourAsTheSingleThreadedTest",
            job1,
            job2
        );


        assertEquals( numMessages, readCount.get() );
        assertEquals( numMessages, writeCount.get() );

        assertFalse( reader.readNextInto( transaction ) );
        assertEquals( "expected three data files to be created", numFullFiles+1, dataDir.files().size() );
    }


// RESTARTS

    @Test
    public void givenEmptyJournal_addMessagesRestartWriterAddMoreMessages_expectReaderToReceiveAllMessages() {
        long numMessages = 10;

        for ( long seq=0; seq<4; seq++ ) {
            writeMessage( seq );
        }

        writer.stop();
        writer.start();

        for ( long seq=4; seq<numMessages; seq++ ) {
            writeMessage( seq );
        }


        for ( long seq=0; seq<numMessages; seq++ ) {
            assertNextMessageIs( seq );
        }

        assertFalse( reader.readNextInto( transaction ) );
        assertEquals( "expected one data file to be created", 1, dataDir.files().size() );
    }

    @Test
    public void givenEmptyJournal_addMessagesToOverflowFirstDataFileRestartWriterAddMoreMessages_expectReaderToReceiveAllMessages() {
        long numMessages = TRANSACTION_COUNT_PERDATAFILE*2;

        for ( long seq=0; seq<TRANSACTION_COUNT_PERDATAFILE+1; seq++ ) {
            writeMessage( seq );
        }

        assertEquals( "expected two data files to be created", 2, dataDir.files().size() );

        writer.stop();
        writer.start();

        for ( long seq=TRANSACTION_COUNT_PERDATAFILE+1; seq<numMessages; seq++ ) {
            writeMessage( seq );
        }


        for ( long seq=0; seq<numMessages; seq++ ) {
            assertNextMessageIs( seq );
        }

        assertFalse( reader.readNextInto(transaction) );
        assertEquals( "$expected two data files to be created", 2, dataDir.files().size() );
    }


// CHECKSUM FAILURES

    @Test
    @SuppressWarnings("EmptyCatchBlock")
    public void givenNonEmptyJournal_corruptAByteInAPayloadAndStartReader_expectReaderToErrorOnCorruptedPayload() {
        writeMessage( 11, 12, 13 );
        writeMessage( 21, 22, 23 );
        writeMessage( 31, 32, 33 );

        corruptMessage(1);

        assertNextMessageIs( 0, 11, 12, 13 );

        try {
            assertNextMessageIs( 1, 21, 22, 23 );
            fail( "expected checksum failure" );
        } catch ( CheckSumException ex ) {
            // expected
        }
    }

    /**
     * Corrupt a single byte within the payload of the specified message.
     */
    private void corruptMessage( long targetMessageSeq ) {
        FileX dataFile = dataDir.getFile("junitJournal0.data");
        long  fileSize = dataFile.sizeInBytes();

        dataFile.processFile2( contents -> {
            long pos               = JournalDataFile2.FILEHEADER_SIZE;
            long currentMessageSeq = 0;

            while ( currentMessageSeq != targetMessageSeq ) {
                int payloadSize = contents.readInt( pos + JournalDataFile2.PER_MSGHEADER_PAYLOADSIZE_INDEX, fileSize );

                pos += JournalDataFile2.PER_MSGHEADER_SIZE + payloadSize;
                currentMessageSeq += 1;
            }

            contents.writeByte( pos+JournalDataFile2.PER_MSGHEADER_SIZE+3, fileSize, (byte) 7 );

            return null;
        }, FileModeEnum.READ_WRITE );
    }


// MISSING DATA FILES

    @Test
    public void givenMultipleDataFiles_removeFirstAndReadAllWillStartFromTheSecondFile_expectError() {
        long numMessages = TRANSACTION_COUNT_PERDATAFILE*3;
        writeMessages(numMessages);

        deleteDataFile(0);


        reader.start();

        for ( long i=TRANSACTION_COUNT_PERDATAFILE; i<numMessages; i++ ) {
            assertNextMessageIs( i );
        }
    }

    @Test
    public void givenMultipleDataFiles_removeFirstFileAndThenReadFromAMessagePartWayThroughSecondDataFile_expectSuccess() {
        long numMessages = TRANSACTION_COUNT_PERDATAFILE * 3;
        writeMessages( numMessages );

        deleteDataFile(0);


        long startFrom = TRANSACTION_COUNT_PERDATAFILE + TRANSACTION_COUNT_PERDATAFILE / 2;

        reader.start();
        assertTrue( reader.seekTo(startFrom) );

        for ( long seq=startFrom; seq<numMessages; seq++ ) {
            assertNextMessageIs( seq );
        }

        assertFalse( reader.readNextInto(transaction) );
    }

    @Test
    public void givenMultipleDataFiles_removeMiddleDataFileAndTryToReadAll_expectItToReadUpToTheEndOfTheFirstBlockAndThenWait() {
        writeMessages( TRANSACTION_COUNT_PERDATAFILE*3 );

        deleteDataFile(1);


        JournalReader2 r = createAndRegisterReader();

        for ( long seq=0; seq<TRANSACTION_COUNT_PERDATAFILE; seq++ ) {
            assertNextMessageIs( r, seq );
        }

        assertFalse( r.readNextInto(transaction) );
    }


// SEEK  (specifically jumping between files)

    @Test
    public void givenThreeFiles_seekForwardWithinFirstFile() {
        writeMessages( TRANSACTION_COUNT_PERDATAFILE*3 );

        readAndAssertMessagesFrom(10, TRANSACTION_COUNT_PERDATAFILE*3);
    }

    @Test
    public void givenThreeFiles_seekForwardToNextFile() {
        writeMessages( TRANSACTION_COUNT_PERDATAFILE*3 );

        readAndAssertMessagesFrom(TRANSACTION_COUNT_PERDATAFILE+1, TRANSACTION_COUNT_PERDATAFILE*3);
    }

    @Test
    public void givenThreeFiles_seekForwardThenBackWithinSameFile() {
        writeMessages( TRANSACTION_COUNT_PERDATAFILE * 3 );

        readAndAssertMessagesFrom(10, TRANSACTION_COUNT_PERDATAFILE*3);
        readAndAssertMessagesFrom(2, TRANSACTION_COUNT_PERDATAFILE*3);
    }

    @Test
    public void givenThreeFiles_seekForwardThenBackToFirstFile() {
        writeMessages( TRANSACTION_COUNT_PERDATAFILE * 3 );

        readAndAssertMessagesFrom(TRANSACTION_COUNT_PERDATAFILE+10, TRANSACTION_COUNT_PERDATAFILE*3);
        readAndAssertMessagesFrom(3, TRANSACTION_COUNT_PERDATAFILE*3);
    }

    @Test
    public void givenThreeFiles_seekToBeforeFirstFile_expectException() {
        writeMessages( TRANSACTION_COUNT_PERDATAFILE * 3 );

        try {
            reader.seekTo( -1 );
            fail( "expected exception JournalNotFoundException" );
        } catch ( JournalNotFoundException2 ex ) {
            assertEquals( "Unable to find msg seq '-1' under '"+toFullDir("/data/junitJournal")+"'; has the data file been removed?", ex.getMessage() );
        }
    }

    @Test
    public void givenThreeFiles_removeFirstFile_seekToFirstFile_expectException() {
        writeMessages( TRANSACTION_COUNT_PERDATAFILE * 3 );

        deleteDataFile(0);


        try {
            reader.seekTo( 10 );
            fail( "expected exception JournalNotFoundException" );
        } catch ( JournalNotFoundException2 ex ) {
            assertEquals( "Unable to find msg seq '10' under '"+toFullDir("/data/junitJournal")+"'; has the data file been removed?", ex.getMessage() );
        }
    }

    @Test
    public void givenThreeFiles_seekBeyondLastMessage_expectFalse() {
        writeMessages( TRANSACTION_COUNT_PERDATAFILE * 3 );

        assertFalse( reader.seekTo( TRANSACTION_COUNT_PERDATAFILE * 3 + 10 ) );
    }


    private void writeMessages( long numMessages ) {
        for ( long seq=0; seq<numMessages; seq++ ) {
            writeMessage( seq );
        }
    }

    private void readAndAssertMessagesFrom( long fromSeq, long toSeqExc ) {
        reader.seekTo( fromSeq );

        for ( long seq=fromSeq; seq<toSeqExc; seq++ ) {
            assertNextMessageIs( seq );
        }

        assertFalse( reader.readNextInto(transaction) );
    }


    private JournalReader2 createAndRegisterReader() {
        JournalReader2 j = journal.createReader();

        system.registerServicesAfter(j);

        return j;
    }

    private void writeMessage( long msgSeq ) {
        writeMessage( expectedFrom(msgSeq), expectedTo(msgSeq), expectedAmount(msgSeq) );
    }

    private void writeMessage( long expectedFrom, long expectedTo, long expectedAmount ) {
        writer.allocateTo( transaction );

        transaction.setFrom( expectedFrom );
        transaction.setTo( expectedTo );
        transaction.setAmount( expectedAmount );

        writer.completeMessage();
    }

    private void assertNextMessageIs( long msgSeq ) {
        assertNextMessageIs( reader, msgSeq );
    }

    private void assertNextMessageIs( JournalReader2 r, long msgSeq ) {
        assertNextMessageIs( r, msgSeq, expectedFrom(msgSeq), expectedTo(msgSeq), expectedAmount(msgSeq) );
    }

    private long expectedAmount( long msgSeq ) {
        return msgSeq*10 + 3;
    }

    private long expectedTo( long msgSeq ) {
        return msgSeq*10 + 2;
    }

    private long expectedFrom( long msgSeq ) {
        return msgSeq*10 + 1;
    }

    private void assertNextMessageIs( long expectedMessageSeq, long expectedFrom, long expectedTo, long expectedAmount ) {
        assertNextMessageIs( reader, expectedMessageSeq, expectedFrom, expectedTo, expectedAmount );
    }

    private void assertNextMessageIs( JournalReader2 r, long expectedMessageSeq, long expectedFrom, long expectedTo, long expectedAmount ) {
        boolean wasSuccessfullyRead = r.readNextInto( transaction );
        assertTrue( "reached end of data file early: " + (expectedFrom/10), wasSuccessfullyRead );

        assertEquals( "messageSeq", expectedMessageSeq, transaction.getMessageSeq() );
        assertEquals( "from "+expectedMessageSeq,       expectedFrom,       transaction.getFrom() );
        assertEquals( "to "+expectedMessageSeq,         expectedTo,         transaction.getTo() );
        assertEquals( "amount "+expectedMessageSeq,     expectedAmount,     transaction.getAmount(), 1e-6 );
    }

    private void deleteDataFile( long fileSeq ) {
        writer.stop();
        reader.stop();

        FileX dataFile0 = dataDir.getFile( "junitJournal"+fileSeq+".data" );
        dataFile0.delete();
    }

    private String toFullDir( String relDir ) {
        String d = system.fileSystem.getCurrentWorkingDirectory().getFullPath();

        if ( d.equals("/") ) {
            return relDir;
        } else {
            return d + relDir;
        }
    }

}