package com.mosaic.bytes.struct;

import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a byte field within a structured record.
 */
public class ByteField implements StructField {

    private final long base;


    public ByteField( long base ) {
        this.base = base;
    }

    public byte get( Struct struct ) {
        return struct.readByte( base );
    }

    public void set( Struct struct, byte newValue ) {
        struct.writeByte( base, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_BYTE;
    }

}
