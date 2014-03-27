package com.mosaic.io.memory;


import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;

import java.util.concurrent.RecursiveTask;


/**
 * Performs full 'table' queries in parallel.  The flyweight is broken up
 * into sub regions, and spread out across multiple threads.  As each task
 * completes, their results are merged together,
 */
@SuppressWarnings("unchecked")
public abstract class FlyWeightQueryOp<T extends FlyWeight<T>, R> {

    private long batchSize;

    public FlyWeightQueryOp() {
        this(100000);
    }

    /**
     * @param batchSize The target number of records to process per batch.
     */
    public FlyWeightQueryOp( long batchSize ) {
        QA.argIsGTZero( batchSize, "batchSize" );

        this.batchSize = batchSize;
    }


    /**
     * Invoke the query against the specified flyweight.  Will block until the
     * result for all sub queries have been collected and merged together.
     */
    public final R query( T flyWeight ) {
        if ( flyWeight.isEmpty() ) {
            return null;
        }

        FlyWeightRegion region = new FlyWeightRegion( flyWeight, 0, flyWeight.getRecordCount() );

        if ( region.getRecordCount() <= batchSize ) {
            return doQuery( region );
        } else {
            FJTask task = new FJTask( region );

            return SystemX.FORK_JOIN_POOL.invoke( task );
        }
    }


    /**
     * Perform the update on the specified region of the fly weight.
     *
     * @return schedule more work against the specified subregions in different threads, or null
     */
    protected abstract R doQuery( T flyWeight, long fromInc, long toExc );

    /**
     * Merge the results of two sub calls.  Neither r1 or r2 will be null.
     */
    protected abstract R doMerge( R r1, R r2 );



    protected R mergeResults( R r1, R r2 ) {
        if ( r1 == null ) {
            return r2;
        } else if ( r2 == null ) {
            return r1;
        }

        return doMerge( r1, r2 );
    }



    private R doQuery( FlyWeightRegion<T> region ) {
        return doQuery( region.getFlyWeight(), region.getFromInc(), region.getToExc() );
    }


    @SuppressWarnings("unchecked")
    private class FJTask extends RecursiveTask<R> {
        private FlyWeightRegion region;

        public FJTask( FlyWeightRegion region ) {
            QA.argNotNull( region, "region" );

            this.region = region;
        }

        public FJTask( FlyWeight flyWeight, long fromInc, long toExc ) {
            this( new FlyWeightRegion(flyWeight, fromInc, toExc) );
        }

        protected R compute() {
            if ( region.getRecordCount() <= batchSize ) {
                return doQuery( region );
            } else {
                long fromInc  = region.getFromInc();
                long toExc    = region.getToExc();
                long midPoint = fromInc + (toExc-fromInc)/2;

                FJTask lhs = new FJTask( region.getFlyWeight().clone(), fromInc, midPoint );
                FJTask rhs = new FJTask( region.getFlyWeight().clone(), midPoint, toExc );

                invokeAll( lhs, rhs );

                return mergeResults( lhs.getRawResult(), rhs.getRawResult() );
            }
        }
    }

}