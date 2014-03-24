package com.mosaic.collections.concurrent;

import com.mosaic.lang.QA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Parallelise the processing of a data set that is suitable for 'divide and
 * conquer' style processing.  A wrapper around Doug Lea's JDK ForkJoin library.
 * To create a new job, extend this class and override the abstract methods.<p/>
 *
 * A note on types.  I is the type of the input data set, and O is the result
 * of the output.
 */
public abstract class ForkJoinJob<I,O> {

    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();


    /**
     * Split the data set up into smaller data sets.  If the data set is already
     * small enough to process efficiently on a single thread then do not
     * split it any further and return a single element collection containing
     * the unmodified data in it.
     *
     * @return null or empty list to not fork
     */
    protected Collection<I> forkData( I data ) {
        return null;
    }

    /**
     * Perform the work on the specified data.
     */
    protected abstract O processData( I data );

    /**
     * Merge the specified result data sets together.  This is undoing the
     * splitting of the input data set that was carried out by forkData.
     */
    protected abstract O joinData( O r1, O r2 );


    /**
     * Dispatch the specified data to the Fork/Join framework.  This method will
     * not return until processing is complete.  If the data set is suitably large
     * and the processing takes suitably long then multiple threads will be
     * used.
     */
    public O exec( I data ) {
        QA.notNull( data, "data" );

        FJTask job = new FJTask(data);

        return FORK_JOIN_POOL.invoke( job );
    }




    @SuppressWarnings("unchecked")
    private class FJTask extends RecursiveTask<O> {
        private I inputData;

        public FJTask( I inputData ) {
            QA.notNull( inputData, "inputData" );

            this.inputData = inputData;
        }

        public O compute() {
            Collection<I> jobs = forkData( inputData );
            if ( jobs == null ) {
                return processData( inputData );
            }

            switch ( jobs.size() ) {
                case 0:
                    return processData( inputData );
                case 1:
                    return processData( jobs.iterator().next() );
                default:
                    return doForkJoinOn( jobs );
            }
        }

        private O doForkJoinOn( Collection<I> forkedDataSets ) {
            List<FJTask> childTasks = createTasksFor( forkedDataSets );

            ForkJoinTask.invokeAll( childTasks );

            return mergeResults(childTasks);
        }

        private List<FJTask> createTasksFor( Collection<I> forkedDataSets ) {
            List<FJTask> childTasks = new ArrayList( forkedDataSets.size() );

            for ( I dataSet : forkedDataSets ) {
                childTasks.add( new FJTask(dataSet) );
            }

            return childTasks;
        }

        private O mergeResults( List<FJTask> childTasks ) {
            assert !childTasks.isEmpty() : "merge results should never have been called with an empty list of childTasks";

            Iterator<FJTask> it = childTasks.iterator();

            O resultSoFar = it.next().getRawResult();
            while ( it.hasNext() ) {
                O r2 = it.next().getRawResult();

                resultSoFar = joinData( resultSoFar, r2 );
            }

            return resultSoFar;
        }
    }

}
