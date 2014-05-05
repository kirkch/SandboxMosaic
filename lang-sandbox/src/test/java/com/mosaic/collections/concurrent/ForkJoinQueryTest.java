package com.mosaic.collections.concurrent;

import com.mosaic.lang.functional.Function2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ForkJoinQueryTest {

    private static final int MAX_BATCH_SIZE = 10000;


// HAPPY PATH

    @Test
    public void processAnEmptyInputDataSet_expectEmptyResults() {
        ParseNumbersJob job = new ParseNumbersJob();

        List<Long> results = job.exec( createInputDataSet(0) );

        assertEquals( createExpectedResults(0), results );
        assertEquals( 1, job.numThreadsUsed() );
    }

    @Test
    public void processASingleBatchOfNumbers_expectItToBeProcessedOnASingleThread() {
        ParseNumbersJob job = new ParseNumbersJob();

        int dataSetSize = MAX_BATCH_SIZE / 2;
        List<Long> results = job.exec( createInputDataSet( dataSetSize ) );

        assertEquals( createExpectedResults(dataSetSize), results );
        assertEquals( 1, job.numThreadsUsed() );
    }

    @Test
    public void processTenBatchesWorthOfData_expectItToBeProcessedAcrossMultipleThreads() {
        ParseNumbersJob job = new ParseNumbersJob();

        int dataSetSize = MAX_BATCH_SIZE * 10;
        List<Long> results = job.exec( createInputDataSet(dataSetSize), new ListMerge() );

        assertEquals( createExpectedResults(dataSetSize), results );
        assertTrue( job.numThreadsUsed() > 1 );
    }



// ERROR PATHS


    @Test
    public void givenAJobThatReturnsEmptyDataSetsFromSplitData_expectItToNotForkAndToBeProcessedByItself() {
        ParseNumbersJob job = new ParseNumbersJob() {
            protected Collection<List<String>> forkData( List<String> data ) {
                return Collections.EMPTY_LIST;
            }
        };

        int dataSetSize = MAX_BATCH_SIZE;


        List<Long> actualResults = job.exec( createInputDataSet(dataSetSize) );

        assertEquals( createExpectedResults(dataSetSize), actualResults );
    }

    @Test
    public void givenAJobThatReturnsNullDataSetsFromSplitData_expectItToNotBeSplitAndProcessedDirectly() {
        ParseNumbersJob job = new ParseNumbersJob() {
            protected Collection<List<String>> forkData( List<String> data ) {
                return null;
            }
        };

        int dataSetSize = MAX_BATCH_SIZE;

        List<Long> actualResults = job.exec( createInputDataSet(dataSetSize) );

        assertEquals( createExpectedResults( dataSetSize ), actualResults );
    }

    @Test
    public void givenAJobThatThrowsAnExceptionFromSplitData_expectException() {
        ParseNumbersJob job = new ParseNumbersJob() {
            protected Collection<List<String>> forkData( List<String> data ) {
                throw new RuntimeException( "splat" );
            }
        };

        int dataSetSize = MAX_BATCH_SIZE;

        try {
            job.exec( createInputDataSet( dataSetSize ) );

            fail( "expected RuntimeException" );
        } catch ( RuntimeException ex ) {
            assertEquals( "java.lang.RuntimeException: splat", ex.getMessage() );
        }
    }

    @Test
    public void givenAJobThatAnExceptionFromProcessData_expectException() {
        final Thread jUnitThread = Thread.currentThread();

        ParseNumbersJob job = new ParseNumbersJob() {
            protected List<Long> processData( List<String> inputData ) {
                //ensure that the exception propagates across threads
                if ( Thread.currentThread() != jUnitThread ) {
                    throw new RuntimeException( "splat" );
                }

                return super.processData( inputData );
            }
        };

        int dataSetSize = MAX_BATCH_SIZE*10;

        try {
            job.exec( createInputDataSet( dataSetSize ) );

            fail( "expected RuntimeException" );
        } catch ( RuntimeException ex ) {
            assertEquals( "java.lang.RuntimeException: splat", ex.getMessage() );
        }
    }

    @Test
    public void givenAJobThatAnExceptionFromMergeData_expectException() {
        ParseNumbersJob job = new ParseNumbersJob();

        final Thread jUnitThread = Thread.currentThread();

        int dataSetSize = MAX_BATCH_SIZE*10;

        try {
            job.exec( createInputDataSet( dataSetSize ), new Function2<List<Long>,List<Long>,List<Long>>() {
                public List<Long> invoke( List<Long> arg1, List<Long> arg2 ) {
                    //ensure that the exception propagates across threads
                    if ( Thread.currentThread() != jUnitThread ) {
                        throw new RuntimeException( "splat" );
                    }

                    return new ListMerge().invoke( arg1, arg2 );
                }
            });

            fail( "expected RuntimeException" );
        } catch ( RuntimeException ex ) {
            assertEquals( "java.lang.RuntimeException: splat", ex.getMessage() );
        }
    }





    private List<String> createInputDataSet( int numElements ) {
        List<String> dataSet = new ArrayList<>( numElements );

        for ( int i=1; i<=numElements; i++ ) {
            dataSet.add( Integer.toString(i) );
        }

        return dataSet;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    private List<Long> createExpectedResults( int numElements ) {
        List<Long> dataSet = new ArrayList<>( numElements );

        for ( int i=1; i<=numElements; i++ ) {
            dataSet.add( new Long(i) );
        }

        return dataSet;
    }

    private static class ListMerge implements Function2<List<Long>,List<Long>,List<Long>> {
        public List<Long> invoke( List<Long> r1, List<Long> r2 ) {
            r1.addAll( r2 );

            Collections.sort(r1);

            return r1;
        }
    }


    private static class ParseNumbersJob extends ForkJoinQuery<List<String>,List<Long>> {
        private Set<Thread> threadCounter = new HashSet<>();

        public int numThreadsUsed() {
            return threadCounter.size();
        }


        protected Collection<List<String>> forkData( List<String> data ) {
            int numElements = data.size();

            if ( numElements > MAX_BATCH_SIZE ) {
                int n = numElements/2;

                List<String> lhs = data.subList( 0, n );
                List<String> rhs = data.subList( n, numElements );

                return Arrays.asList( lhs, rhs );
            } else {
                return Arrays.asList(data);
            }
        }

        protected List<Long> processData( List<String> inputData ) {
            threadCounter.add( Thread.currentThread() );

            int        numElements = inputData.size();
            List<Long> parsedData  = new ArrayList<>( numElements );

            for ( String str : inputData ) {
                parsedData.add( Long.parseLong(str) );
            }

            return parsedData;
        }
    }
}
