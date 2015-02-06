package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a boolean field within a structured record.
 */
public class BooleanField2 implements BytesField2 {

    private final long from;
    private final long toExc;


    public BooleanField2( long base ) {
        this.from  = base;
        this.toExc = base + sizeBytes();
    }

    public boolean get( Bytes2 bytes ) {
        return bytes.readBoolean( from, toExc );
    }

    public void set( Bytes2 bytes, boolean newValue ) {
        bytes.writeBoolean( from, toExc, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_BOOLEAN;
    }

}
