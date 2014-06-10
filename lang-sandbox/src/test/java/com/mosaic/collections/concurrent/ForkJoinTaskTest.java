package com.mosaic.collections.concurrent;

import org.junit.Test;

import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 *
 */
public class ForkJoinTaskTest {

    private Vector        results = new Vector();
    private AtomicInteger childTaskCount = new AtomicInteger();

    @Test
    public void simpleFunctionalExample_eachInvocationStoresTheProcessedIndexInResults_expectAllIndexesToBeProcessesd() {
        MyJob job = new MyJob( 0, 100, 10, 2 );

        job.execute();


        assertEquals( 100, results.size() );
        assertEquals( 35, childTaskCount.get() );

        for ( long i=0; i<100; i++ ) {
            assertTrue( results.contains(i) );

            results.remove( i );
        }

        assertTrue( results.isEmpty() );
    }

    @Test
    public void varyForkingParameters() {
        MyJob job = new MyJob( 0, 100, 10, 10 );

        job.execute();


        assertEquals( 100, results.size() );
        assertEquals( 11, childTaskCount.get() );

        for ( long i=0; i<100; i++ ) {
            assertTrue( results.contains(i) );

            results.remove( i );
        }

        assertTrue( results.isEmpty() );
    }

    @Test
    public void varyForkingParametersLarge() {
        MyJob job = new MyJob( 0, 1000, 10, 10 );

        job.execute();


        assertEquals( 1000, results.size() );
        assertEquals( 111, childTaskCount.get() );

        for ( long i=0; i<1000; i++ ) {
            assertTrue( results.contains(i) );

            results.remove( i );
        }

        assertTrue( results.isEmpty() );
    }

    @Test
    public void varyForkingParametersVLarge() {
        MyJob job = new MyJob( 0, 100, 10, 3 );

        job.execute();


        assertEquals( 100, results.size() );
//        assertEquals( 287, childTaskCount.get() );

        for ( long i=0; i<100; i++ ) {
            assertTrue( "row "+i, results.contains(i) );

            results.remove( i );
        }

        assertTrue( results.isEmpty() );
    }


    private class MyJob extends ForkJoinTask {
        protected MyJob( long from, long toExc, long forkThreshold, int forkFactor ) {
            super( from, toExc );

            setForkThreshold( forkThreshold );
            setForkFactor( forkFactor );

            childTaskCount.incrementAndGet();
        }

        protected void doJob( long i ) {
            results.add( i );
        }

        protected ForkJoinTask spawnChild( long from, long toExc ) {
            childTaskCount.incrementAndGet();

            return super.spawnChild( from, toExc );
        }
    }
}
