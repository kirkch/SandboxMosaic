package com.mosaic.bytes;

/**
 *
 */
public interface ByteRangeCallback {

    public void invoke( Bytes bytes, long offset, long maxExc );

}
