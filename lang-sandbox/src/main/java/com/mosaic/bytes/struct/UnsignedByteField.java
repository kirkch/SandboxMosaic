package com.mosaic.bytes.struct;

import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a unsigned byte field within a structured record.
 */
public class UnsignedByteField implements StructField {

    private final long base;


    public UnsignedByteField( long base ) {
        this.base = base;
    }

    public short get( Struct struct ) {
        return struct.readUnsignedByte( base);
    }

    public void set( Struct struct, short newValue ) {
        struct.writeUnsignedByte( base, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_UNSIGNED_BYTE;
    }

}
