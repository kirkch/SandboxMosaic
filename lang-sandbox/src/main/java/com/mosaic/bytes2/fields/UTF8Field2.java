package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.text.UTF8;


/**
 * Provides access to a utf8 string field of max width within a structured record.
 */
public class UTF8Field2 implements BytesField2 {

    private final long from;
    private final int  maxLength;
    private final long toExc;


    public UTF8Field2( long base, int maxLength ) {
        this.from      = base;
        this.maxLength = maxLength;
        this.toExc     = base + maxLength;
    }

    public UTF8 get( Bytes2 bytes ) {
        return bytes.readUTF8String( from, toExc );
    }

    public void set( Bytes2 bytes, UTF8 newValue ) {
        bytes.writeUTF8String( from, toExc, newValue.truncateToNumOfBytes(maxLength-2) );
    }

    public long sizeBytes() {
        return maxLength;
    }

}
