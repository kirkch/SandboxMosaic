package com.mosaic.bytes.struct;

/**
 * Provides access to an int field within a structured record.
 */
public interface IntegerField {
    public int get();
    public void set( int newValue );
}
