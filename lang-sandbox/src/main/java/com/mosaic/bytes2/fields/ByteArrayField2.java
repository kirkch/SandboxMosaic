package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.bytes2.BytesView2;
import com.mosaic.lang.system.Backdoor;


/**
 * Provides access to a fixed width array of bytes within a structured record.
 */
public class ByteArrayField2 implements BytesField2 {

    private final long from;
    private final long toExc;


    public ByteArrayField2( long base, long numElements ) {
        this.from  = base;
        this.toExc = base + numElements;
    }

    public byte[] toArray( Bytes2 bytes ) {
        int    numElements = Backdoor.toInt( sizeBytes() );
        byte[] out         = new byte[numElements];

        bytes.readBytes(from, toExc, out  );

        return out;
    }

    public void getInto( Bytes2 source, BytesView2 dest ) {
        dest.setBytes( source, from, toExc );
    }

    public void set( Bytes2 dest, byte[] source ) {
        long numElementsToCopy = Math.min( sizeBytes(), source.length );

        dest.writeBytes( from, toExc, source, 0, numElementsToCopy );
    }

    public boolean isEqualTo( Bytes2 bytes, byte[] candidate ) {
        return bytes.compareBytes( from, toExc, candidate );
    }

    public long sizeBytes() {
        return toExc-from;
    }

}
