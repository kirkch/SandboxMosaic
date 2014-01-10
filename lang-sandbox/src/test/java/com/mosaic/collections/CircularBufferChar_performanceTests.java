package com.mosaic.collections;

import org.junit.Test;

/**
 *
 */
public class CircularBufferChar_performanceTests {

    /*
   Tests on 2011 2GHz MBP running Java 1.6.
                                             After replacing modulo with bitmask
       4.228
       5.471   runtime optimiser kicks in
       1.431                                   0.871
       1.395                                   0.846
    */
    @Test
    public void perfTest() {
        CircularBufferChar buf = new CircularBufferChar( 4 );

        for ( int i=0; i<100000; i++ ) {
            buf.append( 'a' );
            buf.popFromLHS();
        }


        long s = System.nanoTime();
        for ( int i=0; i<100000; i++ ) {
            buf.append( 'a' );
            buf.popFromLHS();
        }
        long e = System.nanoTime();

        System.out.println( ((double)(e-s))/1000000 );


        s = System.nanoTime();
        for ( int i=0; i<100000; i++ ) {
            buf.append( 'a' );
            buf.popFromLHS();
        }
        e = System.nanoTime();

        System.out.println( ((double)(e-s))/1000000 );


        s = System.nanoTime();
        for ( int i=0; i<100000; i++ ) {
            buf.append( 'a' );
            buf.popFromLHS();
        }
        e = System.nanoTime();

        System.out.println( ((double)(e-s))/1000000 );


        s = System.nanoTime();
        for ( int i=0; i<100000; i++ ) {
            buf.append( 'a' );
            buf.popFromLHS();
        }
        e = System.nanoTime();

        System.out.println( ((double)(e-s))/1000000 );
    }

}
