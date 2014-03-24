package com.mosaic.collections.concurrent;

import com.mosaic.lang.MergeOp;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.Function2;

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
 * The data tobe processed is past in to the jobs constructor.  Other variations have
 * the data as an argument to the processData function, however we have moved it
 * so that zero copy semantics are supported for fork/join jobs run across
 * large fly weight collections.
 */
@SuppressWarnings("unchecked")
public abstract class ForkProcessJoin<R> {

    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();


    private Function2<R, R, R> mergeOp;


    protected ForkProcessJoin() {
        this(
            new Function2<R,R,R>() {
                public R invoke( R arg1, R arg2 ) {
                    MergeOp<R> a = (MergeOp<R>) arg1;
                    MergeOp<R> b = (MergeOp<R>) arg2;

                    if ( a.size() <= b.size() ) {
                        a.merge( arg2 );

                        return arg1;
                    } else {
                        b.merge(arg1);

                        return arg2;
                    }
                }
            }
        );
    }

    protected ForkProcessJoin( Function2<R,R,R> mergeOp ) {
        QA.argNotNull( mergeOp, "mergeOp" );

        this.mergeOp = mergeOp;
    }


    /**
     * Split the data set up into smaller data sets.  If the data set is already
     * small enough to process efficiently on a single thread then do not
     * split it any further and return a single element collection containing
     * the unmodified data in it.
     *
     * @return null or empty list to not fork
     */
    protected Collection<ForkProcessJoin<R>> forkJobs() {
        return null;
    }

    /**
     * Perform the work on the specified data.  This version of processData
     * will require O to implement MergeOp&lt;O>.
     */
    protected abstract R processData();


    /**
     * Dispatch the specified data to the Fork/Join framework.  This method will
     * not return until processing is complete.  If the data set is suitably large
     * and the processing takes suitably long then multiple threads will be
     * used.
     */
    public R execute() {
        FJTask job = new FJTask();

        // todo exec in this thread if small enough

        return FORK_JOIN_POOL.invoke( job );
    }


    private FJTask createFJTask() {
        return new FJTask();
    }



    @SuppressWarnings("unchecked")
    private class FJTask extends RecursiveTask<R> {
        public R compute() {
            Collection<ForkProcessJoin<R>> jobs = forkJobs();
            if ( jobs == null ) {
                return processData();
            }

            switch ( jobs.size() ) {
                case 0:
                    return processData();
                default:
                    return doForkJoinOn( jobs );
            }
        }

        private R doForkJoinOn( Collection<ForkProcessJoin<R>> forkedDataSets ) {
            List<FJTask> childTasks = createTasksFor( forkedDataSets );

            ForkJoinTask.invokeAll( childTasks );

            return mergeResults(childTasks);
        }

        private List<FJTask> createTasksFor( Collection<ForkProcessJoin<R>> childJobs ) {
            List<FJTask> childTasks = new ArrayList( childJobs.size() );

            for ( ForkProcessJoin<R> childJob : childJobs ) {
                childTasks.add( childJob.createFJTask() );
            }

            return childTasks;
        }

        private R mergeResults( List<FJTask> childTasks ) {
            assert !childTasks.isEmpty() : "merge results should never have been called with an empty list of childTasks";

            Iterator<FJTask> it = childTasks.iterator();

            R resultSoFar = it.next().getRawResult();
            while ( it.hasNext() ) {
                R r2 = it.next().getRawResult();

                resultSoFar = mergeOp.invoke( resultSoFar, r2 );
            }

            return resultSoFar;
        }
    }

}
