package com.mosaic.lang;

/**
 * A mutable version of PlusOp.
 */
public interface MergeOp<T> {

    /**
     * The contents of this object will have the contents of v added to it.  v will be unmodified.
     */
    public void merge( T v );

    /**
     * The size of this object.  Must be usable as an indication as to how expensive this object
     * is to merge into another.  Thus we can use it to place the more expensive object on the lhs
     * of a merge operation.
     */
    public int size();

}
