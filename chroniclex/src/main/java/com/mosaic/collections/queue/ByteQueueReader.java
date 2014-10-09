package com.mosaic.collections.queue;

import com.mosaic.bytes.ByteView;

import java.util.Iterator;


/**
 * A queue consumer.  Receives messages that were published to the queue.  Optimised to reduce
 * byte copying.
 */
public interface ByteQueueReader {

    /**
     *
     * @param view the ByteView which will share bytes from the queue
     *
     * @return false if there is no 'next' yet
     */
    public <T extends ByteView> boolean readNextInto( T view );

}
