package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a long field within a structured record.
 */
public class LongField2 implements BytesField2 {

    private final long from;
    private final long toExc;


    public LongField2( long base ) {
        this.from  = base;
        this.toExc = base + sizeBytes();
    }

    public long get( Bytes2 bytes ) {
        return bytes.readLong( from, toExc );
    }

    public void set( Bytes2 bytes, long newValue ) {
        bytes.writeLong( from, toExc, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_LONG;
    }

}
