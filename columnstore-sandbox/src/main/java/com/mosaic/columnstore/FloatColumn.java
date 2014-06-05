package com.mosaic.columnstore;

import com.mosaic.io.codecs.FloatCodec;


/**
 *
 */
public interface FloatColumn extends Column {

    public float get( long row );
    public void set( long row, float value );

    /**
     * The codec to use for reading/writing this column.
     */
    public FloatCodec getCodec();

}
