package com.mosaic.collections.queue;

import com.mosaic.bytes.ByteRangeCallback;
import com.mosaic.bytes.ByteView;

import java.util.Iterator;


/**
 * A queue consumer.  Receives messages that were published to the queue.  Optimised to reduce
 * byte copying.
 */
public interface ByteQueueReader {

    /**
     *
     * @return false if there is no 'next' yet
     */
    public boolean readNext( ByteRangeCallback readerFunction );

}
