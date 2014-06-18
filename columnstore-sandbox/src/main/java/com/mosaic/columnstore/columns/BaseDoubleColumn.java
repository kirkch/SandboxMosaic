package com.mosaic.columnstore.columns;

import com.mosaic.collections.concurrent.ForkJoinTask;
import com.mosaic.columnstore.DoubleColumn;


/**
 *
 */
public abstract class BaseDoubleColumn implements DoubleColumn {

    public void prePopulateColumn( final DoubleColumn destinationColumn ) {
        ForkJoinTask job = new ForkJoinTask(0, size()) {
            protected void doJob( long i ) {
                double v = BaseDoubleColumn.this.get( i );

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
