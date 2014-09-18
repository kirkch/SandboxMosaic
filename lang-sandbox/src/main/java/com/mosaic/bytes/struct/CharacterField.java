package com.mosaic.bytes.struct;


import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a char field within a structured record.
 */
public class CharacterField implements StructField {

    private final long base;


    public CharacterField( long base ) {
        this.base = base;
    }

    public char get( Struct struct ) {
        return struct.readCharacter(base);
    }

    public void set( Struct struct, char newValue ) {
        struct.writeCharacter( base, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_CHAR;
    }
    
}
