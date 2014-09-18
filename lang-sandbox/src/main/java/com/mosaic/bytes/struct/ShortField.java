package com.mosaic.bytes.struct;

import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a short field within a structured record.
 */
public class ShortField implements StructField {

    private final long base;


    public ShortField( long base ) {
        this.base = base;
    }

    public short get( Struct struct ) {
        return struct.readShort( base );
    }

    public void set( Struct struct, short newValue ) {
        struct.writeShort( base, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_SHORT;
    }

}

