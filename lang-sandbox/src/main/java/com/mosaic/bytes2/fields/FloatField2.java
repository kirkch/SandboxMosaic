package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a float field within a structured record.
 */
public class FloatField2 implements BytesField2 {

    private final long from;
    private final long toExc;


    public FloatField2( long base ) {
        this.from  = base;
        this.toExc = base + sizeBytes();
    }

    public float get( Bytes2 bytes ) {
        return bytes.readFloat( from, toExc );
    }

    public void set( Bytes2 bytes, float newValue ) {
        bytes.writeFloat( from, toExc, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_FLOAT;
    }

}
