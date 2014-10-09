package com.mosaic.collections.queue.journal;

import com.mosaic.io.CheckSumException;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.StartStoppable;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.time.Duration;
import com.softwaremosaic.junit.JUnitMosaic;
import com.softwaremosaic.junit.annotations.Test;
import org.junit.After;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;


@SuppressWarnings("UnusedDeclaration")
public abstract class BaseJournalWriterTestCases {

    // Size each journal file to be able to store exactly 20 transactions before overflowing
    private static final long TRANSACTION_COUNT_PERDATAFILE = 20;
    private static final long JOURNAL_FILE_SIZE             = JournalDataFile.FILEHEADER_SIZE
        + 20*(Transaction.RECORD_SIZE+JournalDataFile.PERMSGHEADER_SIZE)   // space for the messages
        + JournalDataFile.PERMSGHEADER_PAYLOADSIZE_SIZE;                   // the file is truncated with -1
              // 10 + 20 * (24+8) = 10 + 20*32 = 10 + 640 = 650

    private List<StartStoppable> resources = new LinkedList<>();

    private SystemX     system  = createSystem();
    private DirectoryX  dataDir = system.fileSystem.getCurrentWorkingDirectory().getOrCreateDirectory( "data" );


    private JournalWriter journal = registerResource( new JournalWriter(dataDir, "junitJournal", JOURNAL_FILE_SIZE ) );
    private JournalReader reader  = createAndRegisterReader();


    private Transaction transaction = new Transaction();

    private long initialAllocCount;


    protected abstract SystemX createSystem();


    @Before
    public void setUp() {
        initialAllocCount = Backdoor.getActiveAllocCounter();
    }

    @After
    public void tearDown() {
        resources.forEach( StartStoppable::stop );

        system.fileSystem.getRoot().deleteAll();

        JUnitMosaic.spinUntilTrue( () -> Backdoor.getActiveAllocCounter() == initialAllocCount );
        JUnitMosaic.spinUntilTrue( () -> system.fileSystem.getNumberOfOpenFiles() == 0 );
    }


// EMPTY JOURNAL

    @Test
    public void givenNoJournal_createReader_expectException() {
        JournalReader r = new JournalReader(dataDir, "unknownJournal");

        try {
            r.start();
            fail( "expected FileNotFoundException" );
        } catch ( JournalNotFoundException ex ) {
            assertEquals( toFullDir("/data/unknownJournal0.data"), ex.getMessage() );
        }
    }

    @Test( threadCheck=true )
    public void givenNewJournal_expectReaderToHaltWaitingForFirstMessage() {
        assertFalse( reader.readNextInto(transaction) );
    }


// MESSAGES THAT FIT IN SINGLE DATA FILE

    @Test( threadCheck=true )
    public void givenEmptyJournal_addMessage_expectReaderToReceiveIt() {
        writeMessage( 11, 12, 13 );

        assertNextMessageIs( 11, 12, 13 );
        assertFalse( reader.readNextInto(transaction) );
    }

    @Test
    public void givenEmptyJournal_addMessages_expectReaderToReceiveThem() {
        writeMessage( 11, 12, 13 );
        writeMessage( 21, 22, 23 );

        assertNextMessageIs( 11, 12, 13 );
        assertNextMessageIs( 21, 22, 23 );
        assertFalse( reader.readNextInto(transaction) );
    }

    @Test
    public void givenEmptyJournalAndMultipleReaders_addMessages_expectReadersToReceiveThem() {
        JournalReader reader1 = createAndRegisterReader();
        JournalReader reader2 = createAndRegisterReader();

        writeMessage( 11, 12, 13 );
        writeMessage( 21, 22, 23 );


        for ( JournalReader r : asList(reader1, reader2, reader) ) {
            assertNextMessageIs( r, 11, 12, 13 );
            assertNextMessageIs( r, 21, 22, 23 );

            assertFalse( r.readNextInto(transaction) );
        }
    }

    @Test
    public void givenQueueWithMessages_startReader_expectReaderToReceiveMessages() {
        writeMessage( 11, 12, 13 );
        writeMessage( 21, 22, 23 );

        JournalReader reader1 = createAndRegisterReader();

        for ( JournalReader r : asList(reader1,reader) ) {
            assertNextMessageIs( r, 11, 12, 13 );
            assertNextMessageIs( r, 21, 22, 23 );

            assertFalse( r.readNextInto( transaction ) );
        }
    }

    @Test
    public void givenQueueWithMessages_startReaderPartWayThrough_expectReaderToReceiveMessages() {
        writeMessage( 11, 12, 13 );
        writeMessage( 21, 22, 23 );
        writeMessage( 31, 32, 33 );
        writeMessage( 41, 42, 43 );


        reader.seekTo( 2 );
        assertNextMessageIs( 31, 32, 33 );
        assertNextMessageIs( 41, 42, 43 );

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
        for ( long seq=0; seq<TRANSACTION_COUNT_PERDATAFILE+1; seq++ ) {
            writeMessage( seq );
        }

        for ( long seq=0; seq<TRANSACTION_COUNT_PERDATAFILE+1; seq++ ) {
            assertNextMessageIs( seq );
        }

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
    public void concurrentlyWriteToAndReadFromJournal_expectTheSameBehaviourAsTheSingleThreadedTest() {
        long numMessages = TRANSACTION_COUNT_PERDATAFILE*2 + 1;

        AtomicLong writeCount = new AtomicLong();
        AtomicLong readCount  = new AtomicLong();

        new Thread() {
            public void run() {
                for ( long seq=0; seq<numMessages; seq++ ) {
                    writeMessage( seq );

                    writeCount.incrementAndGet();


                    Backdoor.sleep( Duration.millis(1) );  // spreads the writes out
                }
            }
        }.start();

        new Thread() {
            public void run() {
                Transaction t = new Transaction();

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
            }
        }.start();


        JUnitMosaic.spinUntilTrue( () -> readCount.get() == numMessages );

        assertFalse( reader.readNextInto( transaction ) );
        assertEquals( "expected three data files to be created", 3, dataDir.files().size() );
    }


// RESTARTS

    @Test
    public void givenEmptyJournal_addMessagesRestartWriterAddMoreMessages_expectReaderToReceiveAllMessages() {
        long numMessages = 10; //TRANSACTION_COUNT_PERDATAFILE*2 + 1;

        for ( long seq=0; seq<4; seq++ ) {
            writeMessage( seq );
        }

        journal.stop();
        journal.start();

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

        journal.stop();
        journal.start();

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

        assertNextMessageIs( 11, 12, 13 );

        try {
            assertNextMessageIs( 21, 22, 23 );
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

        dataFile.processFile( contents -> {
            long pos               = JournalDataFile.FILEHEADER_SIZE;
            long currentMessageSeq = 0;

            while ( currentMessageSeq != targetMessageSeq ) {
                int payloadSize = contents.readInt( pos + JournalDataFile.PERMSGHEADER_PAYLOADSIZE_INDEX, fileSize );

                pos += JournalDataFile.PERMSGHEADER_SIZE + payloadSize;
                currentMessageSeq += 1;
            }

            contents.writeByte( pos+JournalDataFile.PERMSGHEADER_SIZE+3, fileSize, (byte) 7 );

           return null;
        }, FileModeEnum.READ_WRITE );
    }


// MISSING DATA FILES

    @Test
    public void givenMultipleDataFiles_removeFirstAndReadAll_expectError() {
        long numMessages = TRANSACTION_COUNT_PERDATAFILE*3;

        for ( long seq=0; seq<numMessages; seq++ ) {
            writeMessage( seq );
        }

        deleteDataFile(0);


        try {
            reader.start();

            fail( "expected JournalNotFoundException" );
        } catch ( JournalNotFoundException ex ) {
            assertEquals( "Unable to find msg seq '0' under '"+toFullDir("/data/junitJournal")+"'; has the data file been removed?", ex.getMessage() );
        }
    }

    @Test
    public void givenMultipleDataFiles_removeFirstFileAndThenReadFromAMessagePartWayThroughSecondDataFile_expectSuccess() {
        long numMessages = TRANSACTION_COUNT_PERDATAFILE * 3;
        writeMessages( numMessages );

        deleteDataFile(0);


        long startFrom = TRANSACTION_COUNT_PERDATAFILE + TRANSACTION_COUNT_PERDATAFILE / 2;
        JournalReader r = createAndRegisterReader( startFrom );

        for ( long seq=startFrom; seq<numMessages; seq++ ) {
            assertNextMessageIs( r, seq );
        }

        assertFalse( r.readNextInto(transaction) );
    }

    @Test
    public void givenMultipleDataFiles_removeMiddleDataFileAndTryToReadAll_expectError() {
        writeMessages( TRANSACTION_COUNT_PERDATAFILE*3 );

        deleteDataFile(1);


        JournalReader r = createAndRegisterReader();

        for ( long seq=0; seq<TRANSACTION_COUNT_PERDATAFILE; seq++ ) {
            assertNextMessageIs( r, seq );
        }

        try {
            r.readNextInto( transaction );
            fail( "JournalNotFoundException" );
        } catch ( JournalNotFoundException ex ) {
            assertEquals( toFullDir("/data/junitJournal1.data"), ex.getMessage() );
        }
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
        } catch ( JournalNotFoundException ex ) {
            assertEquals( "Unable to find msg seq '-1' under '"+toFullDir("/data/junitJournal")+"'; has the data file been removed?", ex.getMessage() );
        }
    }

    @Test
    public void givenThreeFiles_removeFirstFile_seekToFirstFile_expectException() {
        writeMessages( TRANSACTION_COUNT_PERDATAFILE * 3 );

        deleteDataFile(0);


        reader = createAndRegisterReader( TRANSACTION_COUNT_PERDATAFILE*2 );

        try {
            reader.seekTo( 10 );
            fail( "expected exception JournalNotFoundException" );
        } catch ( JournalNotFoundException ex ) {
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




    private JournalReader createAndRegisterReader() {
        return createAndRegisterReader(0);
    }

    private JournalReader createAndRegisterReader( long startFrom ) {
        return registerResource( new JournalReader(dataDir, "junitJournal", startFrom) );
    }

    private <T extends StartStoppable> T registerResource( T r ) {
        resources.add( r );

        r.start();

        return r;
    }


    private void writeMessage( long msgSeq ) {
        writeMessage( expectedFrom( msgSeq ), expectedTo(msgSeq), expectedAmount(msgSeq) );
    }

    private void writeMessage( long expectedFrom, long expectedTo, long expectedAmount ) {
        journal.writeMessageUsing( transaction, () -> {
            transaction.setFrom( expectedFrom );
            transaction.setTo( expectedTo );
            transaction.setAmount( expectedAmount );
        } );
    }

    private void assertNextMessageIs( long msgSeq ) {
        assertNextMessageIs( reader, msgSeq );
    }

    private void assertNextMessageIs( JournalReader r, long msgSeq ) {
        assertNextMessageIs( r, expectedFrom(msgSeq), expectedTo(msgSeq), expectedAmount(msgSeq) );
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

    private void assertNextMessageIs( long expectedFrom, long expectedTo, long expectedAmount ) {
        assertNextMessageIs( reader, expectedFrom, expectedTo, expectedAmount );
    }

    private void assertNextMessageIs( JournalReader r, long expectedFrom, long expectedTo, long expectedAmount ) {
        assertTrue( "reached end of data file early: " + (expectedFrom/10), r.readNextInto(transaction) );

        assertEquals( expectedFrom, transaction.getFrom() );
        assertEquals( expectedTo, transaction.getTo() );
        assertEquals( expectedAmount, transaction.getAmount(), 1e-6 );
    }

    private void deleteDataFile( long fileSeq ) {
        journal.stop();
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



