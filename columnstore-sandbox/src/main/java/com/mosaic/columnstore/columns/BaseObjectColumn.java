package com.mosaic.columnstore.columns;

import com.mosaic.collections.concurrent.ForkJoinTask;
import com.mosaic.columnstore.ObjectColumn;


/**
 *
 */
public abstract class BaseObjectColumn<T> implements ObjectColumn<T> {

    public void prePopulateColumn( final ObjectColumn destinationColumn ) {
        ForkJoinTask job = new ForkJoinTask(0, size()) {
            protected void doJob( long i ) {
                T v = BaseObjectColumn.this.get( i );

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
