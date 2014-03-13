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
    // faster than using an on heap byte array even though both used Unsafe under
    // the hood.


//    private Bytes bytes = Bytes.allocOffHeap( 8024 );
    private Bytes       bytes = Bytes.allocOnHeap( 8024 );
    private BytesWriter out   = new BytesWriter( bytes );
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

converting a digit at a time (custom)
    1737.58ns per call
    1654.43ns per call
    1674.50ns per call
    1659.78ns per call
    1648.56ns per call
    1659.73ns per call

with if statement to separate positive and negative paths
    1628.38ns per call
    1618.56ns per call
    1631.85ns per call
    1604.17ns per call
    1631.47ns per call
    1608.90ns per call

using specialised MathUtils.charactersLengthOf for bytes
    1081.05ns per call
    1118.01ns per call
    1127.53ns per call
    1108.85ns per call
    1122.90ns per call
    1127.72ns per call

reversed scan order of MathUtils.charactersLengthOf; positive bytes become faster to write
    1077.36ns per call
    1020.73ns per call
    1018.71ns per call
    1018.17ns per call
    1008.95ns per call
    1018.50ns per call

Conclusion:  6.5x faster than the standard Java approach, and has no GC impact.
 */
    @Benchmark
    public long writeByte( int numIterations ) {
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


    private byte[] bytesConstant = new byte[] {1,2,3,4,5,6,7,8,9,10};

/*
first attempt, for loop indexes array and then calls writeByte
    559.17ns per call
    538.61ns per call
    536.73ns per call
    550.48ns per call
    549.93ns per call
    562.65ns per call

uses Unsafe in an attempt to avoid bounds checking
    615.49ns per call
    607.34ns per call
    608.71ns per call
    624.50ns per call
    614.30ns per call
    610.28ns per call

attempt 2 (reduced amount of calculation for offset while Unsafe)
    596.66ns per call
    571.74ns per call
    580.97ns per call
    568.19ns per call
    566.64ns per call
    577.31ns per call

Conclusion:  Hotspot is doing a fine job of optimising the bounds check out for us  RAR :)
 */
    @Benchmark
    public long writeIndexedBytes( int numIterations ) {
        long v = 0;

        while ( numIterations > 0 ) {
            for ( int i=0; i<10; i++ ) {
                out.writeBytes( bytesConstant, 2, 8 );
            }
            bytes.positionIndex(6);

            v += bytes.readByte( 1 );

            numIterations--;
        }

        return v;
    }

/*
first stab (uses optimised code in UTF8Tools)
    34.17ns per call
    7.07ns per call
    7.40ns per call
    6.92ns per call
    7.15ns per call
    6.92ns per call
 */
    @Benchmark
    public long writeCharacter( int numIterations ) {
        long v = 0;

        while ( numIterations > 0 ) {
            for ( int i=0; i<10; i++ ) {
                out.writeCharacter( (char) i );
            }
            bytes.positionIndex(2);

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
