package com.mosaic.columnstore;

import com.mosaic.io.codecs.IntCodec;
import com.mosaic.io.codecs.LongCodec;


/**
 *
 */
public interface LongColumn {

    public String getColumnName();

    public boolean isSet( long row );
    public long get( long row );
    public void set( long row, long value );

    /**
     * How many rows are in this column.  Starts from row zero and goes through to rowCount-1.
     */
    public long rowCount();

    /**
     * Captures how the value in a specific cell was calculated.
     */
    public CellExplanation explain( long row );

    public LongCodec getCodec();

}
