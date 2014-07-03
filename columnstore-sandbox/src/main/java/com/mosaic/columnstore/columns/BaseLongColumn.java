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
                if ( BaseLongColumn.this.isSet(i) ) {
                    long v = BaseLongColumn.this.get( i );

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

    public LongColumn createAuditor( int expectedCellCount ) {
        return new LongColumnAuditor( this, expectedCellCount );
    }

}
