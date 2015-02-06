package com.mosaic.bytes.heap;

/**
 * Every allocation within this heap must be of the same size.  Which results in a smaller record
 * footprint as the record does not need to store its length.
 */
public interface FixedSizeHeap extends Heap {
}
