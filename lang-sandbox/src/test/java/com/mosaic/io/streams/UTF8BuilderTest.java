package com.mosaic.io.streams;

import com.mosaic.bytes.ArrayBytes2;
import com.mosaic.bytes.Bytes2;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.DecodedCharacter;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class UTF8BuilderTest extends BaseCharacterStreamTestCases {

    private SystemX     system = new DebugSystem();
    private Bytes2      bytes  = new ArrayBytes2( 1024 );
    private UTF8Builder out    = new UTF8Builder( system, bytes );


    protected CharacterStream createStream() {
        return out;
    }

    protected void assertStreamEquals( String expected ) {
        StringBuilder buf = new StringBuilder();

        long max = out.positionIndex();
        long pos = 0;

        DecodedCharacter dec = new DecodedCharacter();
        while ( pos < max ) {
            bytes.readUTF8Character( pos, max, dec );

            buf.append( dec.c );

            pos += dec.numBytesConsumed;
        }

        assertEquals( expected, buf.toString() );
    }

}
