package com.mosaic.io.journal;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.Service;
import com.softwaremosaic.junit.JUnitMosaic;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;


/**
 * @see com.mosaic.io.journal.Journal2#createReaderAsync(JournalReaderCallback)
 * @see com.mosaic.io.journal.Journal2#createReaderAsync(JournalReaderCallback,long)
 */
@RunWith(JUnitMosaicRunner.class)
public class JournalAsyncReaderTests extends Tests {

    // Size each journal file to be able to store exactly 20 transactions before overflowing
    private static final long TRANSACTION_COUNT_PERDATAFILE = 20;
    private static final long JOURNAL_FILE_SIZE             = Journal2.FILEHEADER_SIZE + Journal2.FILEFOOTER_SIZE +
        + TRANSACTION_COUNT_PERDATAFILE*(Transaction2.SIZE_BYTES+Journal2.PER_MSGHEADER_SIZE);


    private Journal2       journal = new Journal2( dataDir, "junitJournal", JOURNAL_FILE_SIZE );
    private JournalWriter2 writer  = journal.createWriter();


    private List<String> audit = new Vector<>();
    private Service reader = journal.createReaderAsync( new JournalReaderCallback() {
        public void entryReceived( long seq, Bytes2 bytes, long from, long toExc ) {
            Transaction2 t = new Transaction2();

            t.bytes.setBytes( bytes,from, toExc );

            audit.add( "entry("+seq+","+t+")" );
        }
    } );

    private Transaction2 transaction = new Transaction2();


    public JournalAsyncReaderTests() {
        system.registerServicesAfter(writer,reader);
    }


    @Test(threadCheck = true)
    public void givenAnEmptyJournal_expectSubscriptionToBeActiveAndTheAuditEmpty() {
        assertTrue( reader.isRunning() );
        assertTrue( audit.isEmpty() );
    }

    @Test(threadCheck = true)
    public void givenAnEmptyJournal_writeASingleEntry_expectAsyncReaderToPickUpTheEntry() {
        writeMessage( 10, 11, 100 );

        JUnitMosaic.spinUntilTrue( () -> audit.size() == 1 );


        List<String> expected = Arrays.asList("entry(0,Transaction(10,11,100.0))" );

        assertEquals( expected, audit );
    }


    private void writeMessage( long expectedFrom, long expectedTo, long expectedAmount ) {
        writer.allocateTo( transaction, Transaction2.SIZE_BYTES );

        transaction.setFrom( expectedFrom );
        transaction.setTo( expectedTo );
        transaction.setAmount( expectedAmount );

        writer.completeMessage();
    }
}
