package com.mosaic.io.bytes;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class BytesTest {

    @Test
    public void loadFromClassPath() throws IOException {
        InputBytes bytes = Bytes.loadFromClassPath( "/movies.txt" );

        assertEquals( 37, bytes.remaining() );
    }

}
