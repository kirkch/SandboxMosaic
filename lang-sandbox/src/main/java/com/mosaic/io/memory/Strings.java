package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.QA;
import com.mosaic.lang.text.UTF8;
import com.mosaic.lang.text.UTF8Tools;


/**
 * A collection of Strings.  Offers compact, off heap storage of UTF8 Strings.  Essentially
 * a convenience wrapper around MemoryRegion, specialised for UTF8 String handling.
 */
public class Strings {

    private MemoryRegion region;

    public Strings( MemoryRegion region ) {
        QA.argNotNull( region, "region" );

        this.region = region;
    }


    public int allocate( String s ) {
        int byteLength = UTF8Tools.countBytesFor(s);
        int address    = region.malloc( byteLength+2 );

        region.writeUTF8String( address, 0, s );

        return address;
    }

    public int allocate( UTF8 s ) {
        int address = region.malloc( s.getByteCount()+2 );

        region.writeUTF8String( address, 0, s );

        return address;
    }

    public void free( int address ) {
        region.free( address );
    }

    public UTF8 fetch( int address ) {
        Bytes bytes = region.asBytes( address );

        return new UTF8( bytes, 2, bytes.bufferLength() );
    }

    public void writeTo( CharacterStream out ) {

    }

}
