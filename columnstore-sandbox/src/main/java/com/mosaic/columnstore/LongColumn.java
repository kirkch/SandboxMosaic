package com.mosaic.columnstore;

import com.mosaic.io.codecs.LongCodec;


/**
 *
 */
public interface LongColumn extends Column {

    public long get( long row );
    public void set( long row, long value );

    public LongCodec getCodec();

}
