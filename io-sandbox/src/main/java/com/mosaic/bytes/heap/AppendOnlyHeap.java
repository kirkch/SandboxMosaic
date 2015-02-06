package com.mosaic.bytes.heap;


/**
 * AppendOnlyHeaps allocate records in sequence and never release the previously allocated record.
 * Without having to manage gaps/compaction/free lists etc, this results in a simpler heap
 * implementation.
 */
public interface AppendOnlyHeap extends SequentialHeap {

}