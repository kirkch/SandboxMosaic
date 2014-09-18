package com.mosaic.bytes.struct;

import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a unsigned short field within a structured record.
 */
public class UnsignedShortField implements StructField {

    private final long base;


    public UnsignedShortField( long base ) {
        this.base = base;
    }

    public int get( Struct struct ) {
        return struct.readUnsignedShort( base);
    }

    public void set( Struct struct, int newValue ) {
        struct.writeUnsignedShort( base, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_UNSIGNED_SHORT;
    }
    
}
