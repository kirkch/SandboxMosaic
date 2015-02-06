package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a byte field within a structured record.
 */
public class ByteField2 implements BytesField2 {

    private final long from;
    private final long toExc;


    public ByteField2( long base ) {
        this.from  = base;
        this.toExc = base + sizeBytes();
    }

    public byte get( Bytes2 bytes ) {
        return bytes.readByte( from, toExc );
    }

    public void set( Bytes2 bytes, byte newValue ) {
        bytes.writeByte( from, toExc, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_BYTE;
    }

}
