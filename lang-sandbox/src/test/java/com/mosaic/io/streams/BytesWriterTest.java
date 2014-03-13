package com.mosaic.io.streams;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.text.DecodedCharacter;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
@Ignore
public class BytesWriterTest {

    private Bytes       bytes = Bytes.allocOnHeap( 1024 );
    private BytesWriter out   = new BytesWriter( bytes );


    @Test
    public void writeByte() {
        out.writeByte( (byte) 127 );
        out.writeByte( (byte) 0 );
        out.writeByte( Byte.MAX_VALUE );
        out.writeByte( Byte.MIN_VALUE );

        assertBytes( "1270127-128" );
    }


    private void assertBytes( String expected ) {
        assertEquals( expected.length(), bytes.positionIndex() );

        DecodedCharacter buf = new DecodedCharacter();
        for ( int i=0; i<expected.length(); i++ ) {
            bytes.readSingleUTF8Character( i, buf );

            assertEquals( expected.charAt(i),  buf.c );
        }
    }

}
