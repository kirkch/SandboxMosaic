package com.mosaic.columnstore;

import com.mosaic.io.codecs.IntCodec;


/**
 *
 */
public interface IntColumn extends Column<IntColumn> {

    public int get( long row );
    public void set( long row, int value );

    public IntCodec getCodec();

}
