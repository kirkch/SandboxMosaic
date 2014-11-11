package com.mosaic.collections.concurrent;

import com.mosaic.lang.StartStopMixin;
import com.mosaic.lang.functional.VoidFunctionLong1;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.system.SystemX;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


/**
 *
 */
public class ForkJoinThreadPool extends StartStopMixin implements ThreadPool {

    private long forkThreshold = 100;
    private int  forkFactor    = 2;


    private ForkJoinPool forkJoinPool;


    public ForkJoinThreadPool() {
        this( ReflectionUtils.getCallersClass().getSimpleName() );
    }

    public ForkJoinThreadPool( String serviceName ) {
        super( serviceName );
    }


    /**
     * Set how large size() must be before a task will be forked.
     */
    public void setForkThreshold( long newForkThreshold ) {
        this.forkThreshold = newForkThreshold;
    }

    /**
     * When splitting up a task, up to how many child tasks should it be split into?
     */
    public void setForkFactor( int newForkFactor ) {
        this.forkFactor = newForkFactor;
    }



    public void executeInParallel( long from, long toExc, VoidFunctionLong1 job ) {
        throwIfNotReady();

        long    numEntries       = toExc - from;
        boolean isTooSmallToFork = numEntries < forkThreshold;

        Job action = spawnRecursiveAction( from, toExc, job );

        if ( isTooSmallToFork ) {
            action.compute();  // invoke in calling thread
        } else {
            SystemX.FORK_JOIN_POOL.invoke( action );  // schedule and block until completion
        }
    }

    protected Job spawnRecursiveAction( long from, long toExc, VoidFunctionLong1 job ) {
        return new Job( from, toExc, job );
    }


    protected void doStart() throws Exception {
        super.doStart();

        this.forkJoinPool = new ForkJoinPool( SystemX.getCoreCount() );
    }

    protected void doStop() throws Exception {
        super.doStop();

        this.forkJoinPool.shutdownNow();

        this.forkJoinPool = null;
    }



    protected class Job extends RecursiveAction implements Cloneable {
        private long              from;
        private long              toExc;
        private VoidFunctionLong1 job;


        public Job( long from, long toExc, VoidFunctionLong1 job ) {
            this.from  = from;
            this.toExc = toExc;
            this.job   = job;
        }

        protected void compute() {
            long    numEntries       = toExc - from;
            boolean isTooSmallToFork = numEntries <= forkThreshold;

            if ( isTooSmallToFork ) {
                invokeInCallingThread();
            } else {
                Collection<Job> childTasks = new ArrayList<>(2);

                long delta = Math.max(forkThreshold, numEntries/forkFactor);

                long f  = from;
                long to = Math.min(from+delta,toExc);

                do {
                    childTasks.add( spawnRecursiveAction(f, to, job) );

                    f  = to;
                    to = Math.min(to+delta,toExc);
                } while ( f < toExc );


                invokeAll( childTasks );
            }
        }

        private void invokeInCallingThread() {
            for ( long i=from; i<toExc; i++ ) {
                job.invoke( i );
            }
        }
    }

}
