package com.mosaic.bytes.struct;

import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a unsigned int field within a structured record.
 */
public class UnsignedIntField implements StructField {

    private final long base;


    public UnsignedIntField( long base ) {
        this.base = base;
    }

    public long get( Struct struct ) {
        return struct.readUnsignedInt( base);
    }

    public void set( Struct struct, long newValue ) {
        struct.writeUnsignedInt( base, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_UNSIGNED_INT;
    }

}
