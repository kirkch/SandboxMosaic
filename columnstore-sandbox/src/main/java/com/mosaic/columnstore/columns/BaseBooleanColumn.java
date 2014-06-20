package com.mosaic.columnstore.columns;

import com.mosaic.collections.concurrent.ForkJoinTask;
import com.mosaic.columnstore.BooleanColumn;


/**
 *
 */
public abstract class BaseBooleanColumn implements BooleanColumn {

    public boolean isFalse( long row ) {
        return !isTrue(row);
    }

    public boolean isTrue( long row ) {
        return isSet(row) && get(row);
    }

    public void prePopulateColumn( final BooleanColumn destinationColumn ) {
        ForkJoinTask job = new ForkJoinTask(0, size()) {
            protected void doJob( long i ) {
                if ( BaseBooleanColumn.this.isSet(i) ) {
                    boolean v = BaseBooleanColumn.this.get( i );

                    destinationColumn.set( i, v );
                }
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
