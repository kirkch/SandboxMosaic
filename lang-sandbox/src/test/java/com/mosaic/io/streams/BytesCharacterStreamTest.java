package com.mosaic.io.streams;

import com.mosaic.io.bytes.Bytes;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class BytesCharacterStreamTest extends BaseCharacterStreamTestCases {

    private Bytes                bytes = Bytes.allocOnHeap( 1024 );
    private BytesCharacterStream out   = new BytesCharacterStream( bytes );


    protected CharacterStream createStream() {
        return out;
    }

    protected void assertStreamEquals( String expected ) {
        StringBuilder buf = new StringBuilder();

        long max = bytes.positionIndex();
        bytes.positionIndex(0);
        while ( bytes.positionIndex() < max ) {
            char c = bytes.readSingleUTF8Character();

            buf.append( c );
        }

        assertEquals( expected, buf.toString() );
    }

}
