package com.mosaic.collections.concurrent;

import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.system.SystemX;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RecursiveAction;


/**
 * Execute in parallel multiple jobs that return no data.  Typically this is a large batch of
 * update jobs.  Under the hood this is implemented using the Fork/Join framework by Doug Lea.<p/>
 *
 * To use, the minimum to implement doJob(i).  If any of the fields within a task has side effects
 * with other tasks, then one must also override spawnChild.  To speed the processing up, the
 * default implementation of spawnChild uses reflection and so it may be worth giving ones own
 * implementation anyway.  Also experiment with different forkThresholds/implementations of
 * isTooSmallToFork. <p/>
 *
 * Once an instance of ForkJoinTask has been created, just call execute() to fork it off and wait
 * for it to complete.
 */
public abstract class ForkJoinTask extends RecursiveAction implements Cloneable {

    private long from;
    private long toExc;
    private long forkThreshold = 100;
    private int  forkFactor    = 2;


    protected ForkJoinTask( long from, long toExc ) {
        this.from  = from;
        this.toExc = toExc;
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


    /**
     * Update the specified fly weight.  Will kick off the work using
     * multiple threads and it will block the calling thread until the work is complete.
     */
    public void execute() {
        // There is no benefit in going parallel, so invoke immediately
        if ( isTooSmallToFork() ) {
            compute();
        } else {
            SystemX.FORK_JOIN_POOL.invoke( this );
        }
    }


    protected void compute() {
        if ( isTooSmallToFork() ) {
            invokeInCallingThread();
        } else {
            Collection<ForkJoinTask> childTasks = new ArrayList<>(2);

            long delta = Math.max(forkThreshold, size()/forkFactor);

            long f  = from;
            long to = Math.min(from+delta,toExc);

            do {
                childTasks.add( this.spawnChild(f, to)  );

                f  = to;
                to = Math.min(to+delta,toExc);
            } while ( f < toExc );


            invokeAll( childTasks );
        }
    }

    private void invokeInCallingThread() {
        for ( long i=from; i<toExc; i++ ) {
            doJob(i);
        }
    }

    /**
     * Perform the work for index i.
     */
    protected abstract void doJob( long i );

    /**
     * Creates a clone of this task that ranges over a subset of this tasks work.  The
     * default implementation uses reflection to clone, override to provide a faster
     * implementation.
     */
    protected ForkJoinTask spawnChild( long from, long toExc ) {
        ForkJoinTask clone = ReflectionUtils.clone( this );

        clone.from  = from;
        clone.toExc = toExc;

        return clone;
    }

    /**
     * Returns true when this task should be forked rather than executed directly in the
     * calling thread.  By default uses size() and the supplied forkThreshold to determine.
     * May be overridden.
     */
    public boolean isTooSmallToFork() {
        return size() <= forkThreshold;
    }

    /**
     * Approximates the size of the work to be performed by this task.  The default implementation
     * approximates this by returning the to/from range.  This function may be overridden.
     */
    protected long size() {
        return toExc - from;
    }
}
