package com.mosaic.bytes.struct;

import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a float field within a structured record.
 */
public class FloatField implements StructField {

    private final long base;


    public FloatField( long base ) {
        this.base = base;
    }

    public float get( Struct struct ) {
        return struct.readFloat(base);
    }

    public void set( Struct struct, float newValue ) {
        struct.writeFloat( base, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_FLOAT;
    }

}
