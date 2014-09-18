package com.mosaic.bytes.struct;

import com.mosaic.lang.system.SystemX;


/**
 * Provides access to an int field within a structured record.
 */
public class IntField implements StructField {

    private final long base;


    public IntField( long base ) {
        this.base = base;
    }

    public int get( Struct struct ) {
        return struct.readInt( base );
    }

    public void set( Struct struct, int newValue ) {
        struct.writeInt( base, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_INT;
    }

}
