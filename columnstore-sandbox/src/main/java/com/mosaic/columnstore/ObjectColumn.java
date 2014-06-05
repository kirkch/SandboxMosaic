package com.mosaic.columnstore;

import com.mosaic.io.codecs.ObjectCodec;


/**
 *
 */
public interface ObjectColumn<T> extends Column {

    public T get( long row );
    public void set( long row, T value );

    public ObjectCodec<T> getCodec();

}
