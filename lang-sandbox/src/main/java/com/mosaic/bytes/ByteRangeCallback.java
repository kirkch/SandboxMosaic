package com.mosaic.bytes;

/**
 *
 */
public interface ByteRangeCallback {

    public void receive( Bytes bytes, long offset, long maxExc );

}
