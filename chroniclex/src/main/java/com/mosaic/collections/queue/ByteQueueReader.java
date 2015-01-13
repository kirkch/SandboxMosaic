package com.mosaic.collections.queue;


/**
 * A queue consumer.  Receives messages that were published to the queue.  Optimised to reduce
 * byte copying.
 */
public interface ByteQueueReader {

    /**
     *
     * @return false if there is no 'next' yet
     */
    public boolean readNext( ByteQueueCallback readerFunction );

}
