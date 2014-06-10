package com.mosaic.columnstore.columns;

import com.mosaic.collections.concurrent.ForkJoinTask;
import com.mosaic.columnstore.BooleanColumn;


/**
 *
 */
public abstract class BaseBooleanColumn implements BooleanColumn {

    public void prePopulateColumn( final BooleanColumn destinationColumn ) {
        ForkJoinTask job = new ForkJoinTask(0, size()) {
            protected void doJob( long i ) {
                boolean v = BaseBooleanColumn.this.get( i );

                destinationColumn.set( i, v );
            }
        };

        destinationColumn.resizeIfNecessary( this.size() );

        job.setForkThreshold( 10000 );
        job.setForkFactor( 3 );

        job.execute();
    }
}
