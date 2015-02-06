package com.mosaic.bytes.heap;


/**
 * A grid heap is a heap that visually resembles a table.  Each record is the same size, and
 * records can only be allocated.  This type of heap is useful for representing trees
 * where the nodes index can easily be mapped to the heap location and once allocated are never
 * released.
 */
public interface GridHeap extends SequentialHeap, FixedSizeHeap {

}
