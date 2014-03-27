package com.mosaic.io.memory;

import com.mosaic.lang.QA;
import com.mosaic.lang.functional.Function0;
import com.mosaic.lang.system.SystemX;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RecursiveAction;


/**
 * Performs bulk updates operations in parallel against a FlyWeight data
 * structure.
 */
@SuppressWarnings("unchecked")
public abstract class FlyWeightUpdateOp<T extends FlyWeight<T>> {

    /**
     * Update the specified fly weight.  Will kick of the work using
     * multiple threads and block until the work is complete.
     */
    public final void execute( FlyWeight<T> flyWeight ) {
        if ( flyWeight.isEmpty() ) {
            return;
        }

        FlyWeightRegion region = new FlyWeightRegion( flyWeight, 0, flyWeight.getRecordCount() );

        if ( region.fitsInCacheLine() ) { // There is no benefit in going parallel, so invoke immediately
            invokeInCallingThread( region );
        } else {
            FJTask task = new FJTask( region );

            SystemX.FORK_JOIN_POOL.invoke( task );
        }
    }


    /**
     * Perform the update on the specified region of the fly weight.
     *
     * @return schedule more work against the specified subregions in different threads, or null
     */
    protected abstract Collection<FlyWeightRegion> doUpdate( FlyWeight<T> flyWeight, long fromInc, long toExc );




    private void invokeInCallingThread( FlyWeightRegion region ) {
        Collection<FlyWeightRegion> subregions = doUpdate(
            region.getFlyWeight(),
            region.getFromInc(),
            region.getToExc()
        );

        if ( subregions == null || subregions.isEmpty() ) {
            return;
        }


        for ( FlyWeightRegion subregion : subregions ) {
            assertIsSubRegion( region, subregion );

            invokeInCallingThread( subregion );
        }
    }

    private void assertIsSubRegion( final FlyWeightRegion region, final FlyWeightRegion subregion ) {
        QA.assertCondition( new Function0<String>() {
            public String invoke() {
                if ( region.contains( subregion ) ) {
                    return null;
                }

                return String.format( "%s does not contain %s", region, subregion );
            }
        } );
    }


    @SuppressWarnings("unchecked")
    private class FJTask extends RecursiveAction {
        private FlyWeightRegion region;

        public FJTask( FlyWeightRegion region ) {
            QA.argNotNull( region, "region" );

            this.region = region;
        }

        protected void compute() {
            Collection<FlyWeightRegion> subregions = doUpdate(
                region.getFlyWeight().clone(),
                region.getFromInc(),
                region.getToExc()
            );

            if ( subregions == null || subregions.isEmpty() ) {
                return;
            }

            Collection<FJTask> childTasks = new ArrayList(2);

            for ( FlyWeightRegion subregion : subregions ) {
                if ( subregion.fitsInCacheLine() ) {
                    invokeInCallingThread( subregion );
                } else {
                    childTasks.add( new FJTask(subregion) );
                }
            }

            if ( !childTasks.isEmpty() ) {
                invokeAll( childTasks );
            }
        }
    }

}