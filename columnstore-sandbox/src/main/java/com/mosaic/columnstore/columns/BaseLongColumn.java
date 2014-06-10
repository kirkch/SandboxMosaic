package com.mosaic.columnstore.columns;

import com.mosaic.collections.concurrent.ForkJoinTask;
import com.mosaic.columnstore.LongColumn;


/**
 *
 */
public abstract class BaseLongColumn implements LongColumn {

    public void prePopulateColumn( final LongColumn destinationColumn ) {
        ForkJoinTask job = new ForkJoinTask(0, size()) {
            protected void doJob( long i ) {
                long v = BaseLongColumn.this.get( i );

                destinationColumn.set( i, v );
            }
        };

//        job.setForkThreshold( 10000 );

        job.execute();
    }

}
