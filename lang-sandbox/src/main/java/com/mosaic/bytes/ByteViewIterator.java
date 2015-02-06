package com.mosaic.bytes;

/**
 * Iterate over a collection of records using the zero-copy flyweight pattern.
 */
public interface ByteViewIterator {

    /**
     * Scroll on to the next record.
     *
     * @return true if successful, false if there is no next record
     */
    public boolean next();

    /**
     * Point the specified flyweight at the currently selected record.
     *
     * @param view  the flyweight that will point at the underlying bytes
     *
     * @return true if the view was updated, else false
     */
    public boolean selectCurrentInto( ByteView view );

    /**
     * Returns the sequence number of the current record.  Sequence numbers start from zero.
     */
    public long getCurrentSeq();

}
