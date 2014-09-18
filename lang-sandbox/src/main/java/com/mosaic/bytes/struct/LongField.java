package com.mosaic.bytes.struct;

import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a long field within a structured record.
 */
public class LongField implements StructField {

    private final long base;


    public LongField( long base ) {
        this.base = base;
    }

    public long get( Struct struct ) {
        return struct.readLong(base);
    }

    public void set( Struct struct, long newValue ) {
        struct.writeLong( base, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_LONG;
    }
    
}
