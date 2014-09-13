package com.mosaic.bytes.struct;

import com.mosaic.bytes.ByteView;
import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.QA;


/**
*
*/
public class Struct implements ByteView {

    private final long structSizeBytes;

    private Bytes bytes;
    private long  base;
    private long  maxExc;


    /**
     * Create a new instance of StructuredBytes.
     *
     * @param structSizeBytes structs are fixed width
     */
    Struct( long structSizeBytes ) {
        QA.argIsGTZero( structSizeBytes, "structSizeBytes" );

        this.structSizeBytes = structSizeBytes;
    }

    public void setFlyWeightBytes( Bytes bytes, long base, long maxExc ) {
        this.bytes  = bytes;
        this.base   = base;

        // do not go over the registered fields, even if the supplied maxExc says that we can
        this.maxExc = Math.min( maxExc, base+structSizeBytes );
    }


    public boolean readBoolean( long offset ) {
//        return bytes.readBoolean( offset, maxExc );
        return false;
    }

    public void writeBoolean( long offset, boolean newValue ) {
//        bytes.writeBoolean( offset, maxExc, newValue );
    }
}
