package com.mosaic.columnstore;

import com.mosaic.io.codecs.DoubleCodec;


/**
 *
 */
public interface DoubleColumn extends Column<DoubleColumn> {

    public double get( long row );
    public void set( long row, double value );

    public DoubleCodec getCodec();

}
