package com.mosaic.bytes.struct;

import com.mosaic.lang.system.SystemX;


/**
* Provides access to a boolean field within a structured record.
*/
public class BooleanField implements StructField {

    private final long base;


    public BooleanField( long base ) {
        this.base = base;
    }

    public boolean get( Struct struct ) {
        return struct.readBoolean( base );
    }

    public void set( Struct struct, boolean newValue ) {
        struct.writeBoolean( base, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_BOOLEAN;
    }

}
