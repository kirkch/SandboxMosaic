package com.mosaic.columnstore.columns;

import com.mosaic.collections.concurrent.ForkJoinTask;
import com.mosaic.columnstore.FloatColumn;


/**
 *
 */
public abstract class BaseFloatColumn implements FloatColumn {

    public void prePopulateColumn( final FloatColumn destinationColumn ) {
        ForkJoinTask job = new ForkJoinTask(0, size()) {
            protected void doJob( long i ) {
                float v = BaseFloatColumn.this.get( i );

                destinationColumn.set( i, v );
            }
        };

        destinationColumn.resizeIfNecessary( this.size() );

        job.setForkThreshold( 10000 );
        job.setForkFactor( 3 );

        job.execute();
    }

    public int reserveWidth() {
        return getCodec().reserveWidth();
    }

}
