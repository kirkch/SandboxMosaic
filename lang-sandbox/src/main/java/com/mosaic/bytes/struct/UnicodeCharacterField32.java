package com.mosaic.bytes.struct;


/**
 * Provides access to a 32bit unicode encoded character field within a structured record.
 */
public interface UnicodeCharacterField32 {
    public char get();
    public void set( char newValue );
}
