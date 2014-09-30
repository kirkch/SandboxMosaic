package com.mosaic.collections.queue;

import com.mosaic.bytes.Bytes;


/**
 *
 */
public interface BytesCallback {
    public void receive( Bytes bytes, long offset, long maxExc );
}
