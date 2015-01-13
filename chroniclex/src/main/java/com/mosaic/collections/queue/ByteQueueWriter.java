package com.mosaic.collections.queue;


import com.mosaic.bytes.ByteView;
import com.mosaic.bytes.Bytes;
import com.mosaic.lang.functional.VoidFunction1;


/**
 * A queue producer.  This byte producer is designed to reduce byte copying.  First one
 * reserves a chunk of bytes to write to, then writes to those bytes and calls flush.  <p/>
 *
 * Typical usage:<p/>
 *
 * <pre>
 *     long seq = queue.reserveUsing( flyweight1, 30 );
 *
 *     flyweight1.setAccountId(42);        // writing to underlying queue bytes via flyweight implementing ByteView
 *     flyweight1.setBalance(1_000_000);
 *
 *     queue.complete(seq);
 * </pre>
 *
 * or using a Java8 lambda
 *
 * <pre>
 *     queue.writeMessage( 30, acc -> {
 *        acc.setAccountId(42);
 *        acc.setBalance(1_000_000);
 *     });
 * </pre>
 */
public interface ByteQueueWriter {

    /**
     * Reserve the bytes for the next message to go out on the queue and assign them to the
     * specified message.  This helps to avoid byte copying.  The message will not be made
     * available to readers until the message is marked as complete.
     *
     * @see #complete(long)
     *
     * @return the seq number of the message that has just been reserved
     */
    public <T extends ByteView> long reserveUsing( T message, int messageSizeBytes );

    /**
     * Marks the message as ready for readers.  Before this call, readers will not be allowed
     * to read this message, which also means that the reader will wait on this message as messages
     * have to be read in the order that they were reserved in.
     *
     * @param messageSeq the seq number of the reserved message
     */
    public void complete( long messageSeq );

    /**
     * Ensures that all data has been written to persistent store.  A fairly expensive call, and
     * so it should only be called when we need to ensure that the messages written to a queue
     * will not get lost even in a power outage.
     */
    public void sync();


    /**
     * A convenience method for reserving and then completing a message.
     *
     * @param messageSizeBytes the size of the message that will be written
     * @param writerFunction   a function that will write the message
     */
    public void writeMessage( int messageSizeBytes, VoidFunction1<Bytes> writerFunction );

    public void writeMessage( int messageSizeBytes, ByteQueueCallback writerFunction );

}
