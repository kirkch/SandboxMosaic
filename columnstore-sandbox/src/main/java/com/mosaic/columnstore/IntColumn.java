package com.mosaic.columnstore;

import com.mosaic.io.codecs.IntCodec;


/**
 *
 */
public interface IntColumn {

    public String getColumnName();

    public boolean isSet( long row );
    public int get( long row );
    public void set( long row, int value );

    /**
     * How many rows are in this column.  Starts from row zero and goes through to rowCount-1.
     */
    public long rowCount();

    /**
     * Captures how the value in a specific cell was calculated.
     */
    public CellExplanation explain( long row );

    public IntCodec getCodec();
}
