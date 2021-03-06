package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a unsigned short field within a structured record.
 */
public class UnsignedShortField2 implements BytesField2 {

    private final long from;
    private final long toExc;


    public UnsignedShortField2( long base ) {
        this.from  = base;
        this.toExc = base + sizeBytes();
    }

    public int get( Bytes2 bytes ) {
        return bytes.readUnsignedShort( from, toExc );
    }

    public void set( Bytes2 bytes, int newValue ) {
        bytes.writeUnsignedShort( from, toExc, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_UNSIGNED_SHORT;
    }

}
