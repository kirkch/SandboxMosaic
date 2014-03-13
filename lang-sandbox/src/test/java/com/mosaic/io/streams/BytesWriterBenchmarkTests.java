package com.mosaic.io.streams;

import com.mosaic.io.bytes.Bytes;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.runner.RunWith;


/**
 *
 */
//@Ignore
@RunWith(JUnitMosaicRunner.class)
public class BytesWriterBenchmarkTests {

    // CONCLUSIONS:

    // QUESTION,
    //  how much faster is
    //
    //    int i = 3;
    //    buf[0] = (byte) v;
    //    buf[1] = (byte) v;
    //    buf[2] = (byte) v;
    //
    //    bytes.writeBytes( buf, 0, i );
    //
    // compare to
    //
    //        byte[] bytes1 = Integer.toString(v).getBytes( SystemX.UTF8 );
    //
    //        bytes.writeBytes( bytes1 );
    //
    //
    // Answer:  10x difference
    //
    // Also of note,  running the test against off heap memory was noticeably
    // faster than using an onheap byte array even though both used Unsafe under
    // the hood.


//    private Bytes bytes = Bytes.allocOffHeap( 8024 );
    private Bytes bytes = Bytes.allocOnHeap( 8024 );
    private BytesWriter out = new BytesWriter( bytes );
/*
3 byte array hack
    714.89ns per call
    676.27ns per call
    681.51ns per call
    681.46ns per call
    694.29ns per call
    683.00ns per call

using Integer.toString and then getBytes
    6390.64ns per call
    6508.08ns per call
    6482.80ns per call
    6549.88ns per call
    6582.01ns per call
    6653.79ns per call
 */
    @Benchmark
    public long writeBytes( int numIterations ) {
        long v = 0;

        while ( numIterations > 0 ) {
            for ( int i=0; i<100; i++ ) {
                out.writeByte( (byte) i );
            }
            bytes.positionIndex(0);

            v += bytes.readByte( 1 );

            numIterations--;
        }

        return v;
    }

/*
    1895.39ns per call
    2127.67ns per call
    2076.58ns per call
    2053.94ns per call
    2089.91ns per call
    2066.34ns per call

*/
    @Benchmark
    public void writeInt( int numIterations ) {
        while ( numIterations > 0 ) {
            for ( int i=0; i<5000; i++ ) {
                bytes.writeByte( (byte) i );
            }
            bytes.positionIndex(0);

            numIterations--;
        }
    }

/*
writeBytes directly in binary
    638.42ns per call   (1000 times each call)
    614.19ns per call
    615.00ns per call
    609.55ns per call
    609.94ns per call
    613.00ns per call


*/
    @Benchmark
    public long writeFloat( int numIterations ) {
        long v = 0;

        while ( numIterations > 0 ) {
            for ( int i=0; i<1000; i++ ) {
                bytes.writeFloat( i + 0.3f );
            }
            bytes.positionIndex(0);

            v += bytes.readByte( 1 );

            numIterations--;
        }

        return v;
    }

}
