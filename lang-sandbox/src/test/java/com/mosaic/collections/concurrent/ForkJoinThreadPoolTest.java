package com.mosaic.collections.concurrent;

import com.mosaic.lang.functional.VoidFunctionLong1;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;


public class ForkJoinThreadPoolTest {

    private Vector<Long>  results        = new Vector<>();
    private AtomicInteger childTaskCount = new AtomicInteger();

    private ForkJoinThreadPool pool = new ForkJoinThreadPool() {
        // overriden so that we can count how many times the fork joiner creates a child task
        protected Job spawnRecursiveAction( long from, long toExc, VoidFunctionLong1 f ) {
            Job job = super.spawnRecursiveAction( from, toExc, f );

            childTaskCount.incrementAndGet();

            return job;
        }
    };


    @Before
    public void setup() {
        pool.start();
    }

    @After
    public void tearDown() {
        pool.start();
    }


    @Test
    public void simpleFunctionalExample_eachInvocationStoresTheProcessedIndexInResults_expectAllIndexesToBeProcessesd() {
        pool.setForkFactor( 2 );
        pool.setForkThreshold( 10 );


        pool.executeInParallel( 0, 100, results::add );


        assertResults( 100, 35 );
    }

    @Test
    public void varyForkingParameters() {
        pool.setForkFactor( 10 );
        pool.setForkThreshold( 10 );


        pool.executeInParallel( 0, 100, results::add );


        assertResults( 100, 11 );
    }

    @Test
    public void varyForkingParametersLarge() {
        pool.setForkFactor( 10 );
        pool.setForkThreshold( 10 );


        pool.executeInParallel( 0, 1000, results::add );


        assertResults( 1000, 111 );
    }

    private void assertResults( long expectedNumberOfResults, long expectedChildTaskCount ) {
        assertEquals( expectedNumberOfResults, results.size() );
        assertEquals( expectedChildTaskCount, childTaskCount.get() );

        for ( long i=0; i<expectedNumberOfResults; i++ ) {
            assertTrue( results.contains(i) );

            results.remove( i );
        }

        assertTrue( results.isEmpty() );
    }

}