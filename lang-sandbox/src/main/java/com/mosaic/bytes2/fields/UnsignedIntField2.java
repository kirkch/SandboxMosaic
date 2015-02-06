package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a unsigned int field within a structured record.
 */
public class UnsignedIntField2 implements BytesField2 {

    private final long from;
    private final long toExc;


    public UnsignedIntField2( long base ) {
        this.from  = base;
        this.toExc = base + sizeBytes();
    }

    public long get( Bytes2 bytes ) {
        return bytes.readUnsignedInt( from, toExc );
    }

    public void set( Bytes2 bytes, long newValue ) {
        bytes.writeUnsignedInt( from, toExc, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_UNSIGNED_INT;
    }

}
