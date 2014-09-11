package com.mosaic.bytes.struct;

/**
 * Provides access to a byte field within a structured record.
 */
public interface ByteField {
    public byte get();
    public void set( byte newValue );
}
