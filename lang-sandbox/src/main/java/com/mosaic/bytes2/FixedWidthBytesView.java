package com.mosaic.bytes2;

import com.mosaic.bytes2.fields.ByteFieldsRegistry2;
import com.mosaic.lang.QA;


/**
 * A specialisation of BytesView for views that always view the same number of bytes at a time.
 * It overrides setBytes to guard against overrunning maxExc.
 */
public class FixedWidthBytesView extends BytesView2 {

    private long widthBytes;

    public FixedWidthBytesView( ByteFieldsRegistry2 registry ) {
        this( registry.sizeBytes() );
    }

    public FixedWidthBytesView( long widthBytes ) {
        this.widthBytes = widthBytes;
    }

    public FixedWidthBytesView( ByteFieldsRegistry2 registry, Bytes2 bytes ) {
        this( registry.sizeBytes(), bytes, 0, bytes.sizeBytes() );
    }

    public FixedWidthBytesView( long widthBytes, Bytes2 bytes ) {
        this( widthBytes, bytes, 0, bytes.sizeBytes() );
    }

    public FixedWidthBytesView( long widthBytes, Bytes2 bytes, long offset, long maxExc ) {
        this.widthBytes = widthBytes;

        setBytes( bytes, offset, maxExc );
    }


    public void setBytes( Bytes2 bytes, long base ) {
        this.setBytes( bytes, base, base+widthBytes );
    }

    @Override
    public void setBytes( Bytes2 bytes, long base, long maxExc ) {
        long toExc = base + widthBytes;

        QA.argIsLTE( toExc, maxExc, "maxExc" );

        super.setBytes( bytes, base, toExc );
    }
}
