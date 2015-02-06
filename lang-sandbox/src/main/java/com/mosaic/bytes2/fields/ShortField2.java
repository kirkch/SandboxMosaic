package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a short field within a structured record.
 */
public class ShortField2 implements BytesField2 {

    private final long from;
    private final long toExc;


    public ShortField2( long base ) {
        this.from  = base;
        this.toExc = base + sizeBytes();
    }

    public short get( Bytes2 bytes ) {
        return bytes.readShort( from, toExc );
    }

    public void set( Bytes2 bytes, short newValue ) {
        bytes.writeShort( from, toExc, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_SHORT;
    }

}
