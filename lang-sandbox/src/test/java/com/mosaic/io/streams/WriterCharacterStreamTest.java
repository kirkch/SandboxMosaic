package com.mosaic.io.streams;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class WriterCharacterStreamTest extends BaseCharacterStreamTestCases {

    private StringWriter    stringWriter = new StringWriter();
    private CharacterStream out          = new WriterCharacterStream( stringWriter );


    protected CharacterStream createStream() {
        return out;
    }

    protected void assertStreamEquals( String expected ) {
        assertEquals( expected, stringWriter.toString() );
    }

}
