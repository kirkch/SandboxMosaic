package com.mosaic.collections.queue;

import com.mosaic.bytes.Bytes;


/**
 *
 */
public interface ByteQueueCallback {

    public void invoke( long seq, Bytes bytes, long offset, long maxExc );

}
