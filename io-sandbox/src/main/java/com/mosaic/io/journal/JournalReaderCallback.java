package com.mosaic.io.journal;

import com.mosaic.bytes2.Bytes2;


/**
 * An async notification that a journal message has been received.
 *
 * @see com.mosaic.io.journal.Journal2#createReaderAsync
 */
public interface JournalReaderCallback {

    /**
     * This method is called when a message has appeared within the journal.  The callback has
     * been optimised to keep GC churn low, thus the bytes object will tend to point directly
     * to the underlying files via the use of the flyweight pattern.  Bytes must not be written to,
     * and references to it must not be kept between calls to entryReceived as the backing files
     * could be closed on us.
     *
     * @param seq   unique identifier for the message, starts from zero
     * @param bytes the message itself, it may have visibility of other messages so be sure to
     *              restrict the view and to copy the data that you require out
     * @param from  the index to start reading from (inclusive)
     * @param toExc the index to read up to (exclusive)
     */
    public void entryReceived( long seq, Bytes2 bytes, long from, long toExc );

}
