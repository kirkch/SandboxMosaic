package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a double field within a structured record.
 */
public class DoubleField2 implements BytesField2 {

    private final long from;
    private final long toExc;


    public DoubleField2( long base ) {
        this.from  = base;
        this.toExc = base + sizeBytes();
    }

    public double get( Bytes2 bytes ) {
        return bytes.readDouble( from, toExc );
    }

    public void set( Bytes2 bytes, double newValue ) {
        bytes.writeDouble( from, toExc, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_DOUBLE;
    }

}
