package com.mosaic.columnstore;

import com.mosaic.io.codecs.LongCodec;


/**
 *
 */
public interface LongColumn extends Column {

    public abstract long get( long row );
    public abstract void set( long row, long value );

    public abstract LongCodec getCodec();


    /**
     * Pre-calculate the value for every cell in this column and store it in the specified
     * destination cell.<p/>
     *
     * By default this is done in parallel, spreading the cells out amongst
     * multiple threads.  Such a parallel approach is extremely effective when there is little
     * to no dependencies between cells.  If however there is a very high level of dependency
     * between the cells, for example if cell N depends on every previous cell before it then
     * calculating each cell separately will be very very inefficient.  For example, consider
     * a column that is the sum of every previous cell.  In such a case override this method
     * and provide a more efficient implementation that knows the specifics of the algorithm at hand.
     */
    public void prePopulateColumn( LongColumn destinationColumn );

}
