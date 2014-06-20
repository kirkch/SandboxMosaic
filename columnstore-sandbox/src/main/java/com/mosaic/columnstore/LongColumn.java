package com.mosaic.columnstore;

import com.mosaic.io.codecs.LongCodec;


/**
 *
 */
public interface LongColumn extends Column<LongColumn> {

    public abstract long get( long row );
    public abstract void set( long row, long value );

    public abstract LongCodec getCodec();

}
