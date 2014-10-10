package com.mosaic.collections.queue.journal;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.lang.system.LiveSystem;
import com.mosaic.lang.system.SystemX;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicLong;


/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
@SuppressWarnings("UnusedDeclaration")
public class JournalWriterBenchmark {

    private SystemX    system  = new LiveSystem();
    private DirectoryX dataDir = system.fileSystem.getCurrentWorkingDirectory().getOrCreateDirectory( "benchmark_tmp" );

    private JournalWriter writer;



    @Before
    public void setup() {
        writer = new JournalWriter( dataDir, "journal", 100*SystemX.MEGABYTE );

        writer.start();
    }

    @After
    public void tearDown() {
        writer.stop();

        dataDir.deleteAll();
    }

    // 40ms per million transaction objects written (!?!)
    // 46ms after adding checksum
    // 40ms after thinning down the wrapping of bytes classes (80ms when syncing every 1m messages)
    // 28ms after thinning the wrapping of byte classes down further  (70ms when syncing every 1m messages)
    @Benchmark( value=3, batchCount=6, units="per million 24 byte messages" )
    public void writer() {
        Transaction t = new Transaction();


        for ( int i=0; i<1000000; i++ ) {
            long v = i;

            writer.writeMessageUsing( t, () -> {
                t.setFrom( v );
                t.setTo( v );
                t.setAmount( v );
            });

            // saves 8ms over the version that uses Transaction (ie drops to 20ms, rather than 28ms)
            // AND with sync every 1m messages;  the time drops from 70ms to 60ms
//            writer.writeMessage( (int) t.sizeBytes(), ( bytes, offset, maxExc ) -> {
//                bytes.writeLong( offset, maxExc, v );
//                bytes.writeLong( offset + 8, maxExc, v );
//                bytes.writeDouble( offset+16, maxExc, v );
//            });
        }

//        writer.sync();   // 80ms per 1m to get the data to disk in large batches
         // 32 bytes/msg == 30.5MB/0.08s == 381.5MB/s   === 3Gb/s
    }


    // 38ms per million
    // 35ms after reducing the wrapping
    // 34ms after reducing the wrapping further
    //  32-34ms when using the ByteRangeCallback and 36-39ms when using BytesView as a flyweight.. )
    //
    // added readNext, and tweaked readNextInto to use readNext.. the idea being to reduce the wrapping of
    // of bytes which showed excellent speed boosts with writes.  This actually slowed the reads by 1-3ms
    // and made them more variable.  Using the newer readNext method performs at about the same speed
    // as before.  This is okay as I want the performance gain when writing, and for reader to match the interface style.


    // NB to run, relies on there being data in a journal first... which can be created by commenting
    //   out the deleteAll in the tearDown function before running the writer benchmark.
    @Benchmark( value=3, batchCount=6, durationResultMultiplier=1.0/21, units="per million 24 byte messages" )
    public long reader() {
        JournalReader reader = new JournalReader( dataDir, "journal" );
        reader.start();

        Transaction t = new Transaction();

        long s = 0;
        long c = 0;
        while ( reader.readNextInto(t) ) {
            s += t.getFrom() + t.getTo();
            c++;
        }
//        AtomicLong ns = new AtomicLong();
//        while ( reader.readNext( (bytes,offset,maxExc) -> {ns.lazySet( bytes.readLong(offset,maxExc)+bytes.readLong(offset+8,maxExc) );}) ) {
//            c++;
//        }

        reader.stop();

//        return ns.get();
        return s;
    }

}
