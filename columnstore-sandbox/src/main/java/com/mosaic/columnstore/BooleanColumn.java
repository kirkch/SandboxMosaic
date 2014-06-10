package com.mosaic.columnstore;

import com.mosaic.io.codecs.BooleanCodec;


/**
 *
 */
public interface BooleanColumn extends Column {

    public boolean get( long row );
    public void set( long row, boolean value );

    public BooleanCodec getCodec();

}
