package com.mosaic.columnstore;

import com.mosaic.io.codecs.DoubleCodec;
import com.mosaic.io.codecs.IntCodec;


/**
 *
 */
public interface DoubleColumn {

    public boolean isSet( long row );
    public double get( long row );
    public void set( long row, double value );

    /**
     * How many rows are in this column.  Starts from row zero and goes through to rowCount-1.
     */
    public long rowCount();

    /**
     * Captures how the value in a specific cell was calculated.
     */
    public CellExplanation explain( long row );

    public DoubleCodec getCodec();

}
