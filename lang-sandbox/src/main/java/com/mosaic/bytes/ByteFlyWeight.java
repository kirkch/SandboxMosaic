package com.mosaic.bytes;

import com.mosaic.io.bytes.Bytes;


/**
 * Implementations of ByteFlyWeight reduces memory consumption, buffer copying and object allocations
 * by sharing mutable state.  A trade-off that favours performance over the risk of mutating data
 * that should not be mutated.  Only use this class when it is clear that the la
 */
public interface ByteFlyWeight {

    /**
     * Updates the shared data region of this fly weight.
     *
     * @param bytes The shared instance of Bytes.
     * @param base The base address that this entry starts at.
     * @param maxExc The max address-1 that may safely be written to/read from.
     */
    public void setFlyWeightBytes( Bytes bytes, long base, long maxExc );

}
