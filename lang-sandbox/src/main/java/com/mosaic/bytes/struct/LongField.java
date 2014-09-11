package com.mosaic.bytes.struct;

/**
 * Provides access to a long field within a structured record.
 */
public interface LongField {
    public long get();
    public void set( long newValue );
}
