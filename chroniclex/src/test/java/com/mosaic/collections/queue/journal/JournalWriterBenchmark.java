package com.mosaic.collections.queue.journal;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.system.LiveSystem;
import com.mosaic.lang.system.SystemX;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;


/**
 *
 */
//@RunWith(JUnitMosaicRunner.class)
@SuppressWarnings("UnusedDeclaration")
public class JournalWriterBenchmark {

    private SystemX system  = new LiveSystem();
    private DirectoryX dataDir = system.fileSystem.getCurrentWorkingDirectory().getOrCreateDirectory( "benchmark_tmp" );

    private JournalWriter<Transaction> writer;



    @Before
    public void setup() {
        writer = new JournalWriter<>( dataDir, "journal", Transaction.class, 100*SystemX.MEGABYTE );

        writer.start();
    }

    @After
    public void tearDown() {
        writer.stop();

        dataDir.deleteAll();
    }

    // 40ms per million transaction objects written (!?!)
    // 46ms after adding checksum
    @Benchmark( value=3, batchCount=6, units="per million 24 byte messages" )
    public void writer() {
        for ( int i=0; i<1000000; i++ ) {
            long v = i;

            writer.writeMessage( Transaction.RECORD_SIZE, t -> {
                t.setFrom( v );
                t.setTo( v );
                t.setAmount( v );
            });
        }
    }


    // 38ms per million
    // NB to run, relies on there being data in a journal first... which can be created by commenting
    //   out the deleteAll in the tearDown function before running the writer benchmark.
    @Benchmark( value=3, batchCount=6, durationResultMultiplier=1.0/21, units="per million 24 byte messages" )
    public long reader() {
        JournalReader reader = new JournalReader<>( dataDir, "journal", Transaction.class );
        reader.start();

        Transaction t = new Transaction();

        long s = 0;
        long c = 0;
        while ( reader.readNextInto(t) ) {
            s += t.getFrom() + t.getTo();
            c++;
        }

        reader.stop();

        return s;
    }

}
