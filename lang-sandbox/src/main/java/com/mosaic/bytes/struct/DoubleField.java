package com.mosaic.bytes.struct;

import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a double field within a structured record.
 */
public class DoubleField implements StructField {

    private final long base;


    public DoubleField( long base ) {
        this.base = base;
    }

    public double get( Struct struct ) {
        return struct.readDouble( base );
    }

    public void set( Struct struct, double newValue ) {
        struct.writeDouble( base, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_DOUBLE;
    }

}

