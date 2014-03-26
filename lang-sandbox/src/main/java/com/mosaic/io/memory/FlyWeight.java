package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;


/**
 *
 */
public interface FlyWeight<T extends FlyWeight<T>> extends Cloneable {

    T clone();

    long getRecordCount();
    long getRecordWidth();

    boolean hasNext();

    boolean next();

    /**
     * Which record is currently selected?
     *
     * @return -1 if no record has been selected
     */
    long selectedIndex();

    T select( long recordIndex );

    long allocateNewRecords( int numElements );

    public FlyWeight<T> subview( long fromInc, long toExc );

    void copySelectedRecordTo( long toDestinationIndex );

    void copySelectedRecordTo( Bytes destinationBytes, long destinationOffsetBytes );

    void copySelectedRecordFrom( Bytes sourceBytes, long sourceOffsetBytes );

    /**
     * Swap recordIndex1 and recordIndex2 over.  Use the specified tmpBuffer during the
     * swap;  this gives us the opportunity to reuse the same buffer thus keeping GC
     * down.
     *
     * @param tmpBuffer        temporarily use this buffer while copying the records between each other
     * @param tmpBufferOffset  will use bytes tmpBufferOffset to tmpBufferOffset+recordWidth (exc) of tmpBuffer
     */
    void swapRecords( long recordIndex1, long recordIndex2, Bytes tmpBuffer, long tmpBufferOffset );

    /**
     * Sorts all of the records within this store.  Does not preserve the selected
     * index.
     */
    void sort( FlyWeightComparator<T> comparator );

    /**
     * Sorts all of the records within this store.  Does not preserve the selected
     * index.
     */
    void sort( FlyWeightComparator<T> comparator, Bytes tmpBuffer, long tmpBufferOffset );

    void sort( FlyWeightComparator<T> comparator, long fromInc, long toExc, Bytes tmpBuffer, long tmpBufferOffset );

    boolean isEmpty();
}
