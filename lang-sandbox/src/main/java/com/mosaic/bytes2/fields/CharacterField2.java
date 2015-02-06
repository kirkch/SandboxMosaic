package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a char field within a structured record.
 */
public class CharacterField2 implements BytesField2 {

    private final long from;
    private final long toExc;


    public CharacterField2( long base ) {
        this.from  = base;
        this.toExc = base + sizeBytes();
    }

    public char get( Bytes2 bytes ) {
        return bytes.readCharacter( from, toExc );
    }

    public void set( Bytes2 bytes, char newValue ) {
        bytes.writeCharacter( from, toExc, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_CHAR;
    }

}
