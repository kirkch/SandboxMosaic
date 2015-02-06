package com.mosaic.bytes.heap;

import com.mosaic.bytes2.BytesView2;


/**
 * Manages a region of memory.
 */
public interface Heap {

    /**
     * Allocate a new record starting from the beginning of the heap and then incrementing along
     * in order.
     *
     * @param numBytes  the number of bytes to allocate
     *
     * @return the pointer to the start of the record, -1 if the allocation failed due to lack of space
     */
    public long allocateRecord( int numBytes );

    public void selectIntoView( BytesView2 view, long ptr );

    public boolean releaseRecord( long ptr );

    public long sizeBytes();

    public long remainingBytes();

    public long usedBytes();

    public long numAllocatedRecords();

    public void flush();

}
