package com.mosaic.bytes.struct;

/**
 * Provides access to an 8 bit ascii encoded character field within a structured record.
 */
public interface AsciiCharacterField {
    public char get();
    public void set( char newValue );
}