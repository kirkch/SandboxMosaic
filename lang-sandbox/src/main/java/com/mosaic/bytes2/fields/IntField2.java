package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;


/**
 * Provides access to a int field within a structured record.
 */
public class IntField2 implements BytesField2 {

    private final long from;
    private final long toExc;


    public IntField2( long base ) {
        this.from  = base;
        this.toExc = base + sizeBytes();
    }

    public int get( Bytes2 bytes ) {
        return bytes.readInt( from, toExc );
    }

    public void set( Bytes2 bytes, int newValue ) {
        bytes.writeInt( from, toExc, newValue );
    }

    public long sizeBytes() {
        return SystemX.SIZEOF_INT;
    }

    public long incrementByOne( Bytes2 bytes ) {
        int v = get(bytes);

        QA.isLT( v, Integer.MAX_VALUE, "v" ); // overflow check

        int newValue = v + 1;
        set( bytes, newValue );

        return newValue;
    }
}
