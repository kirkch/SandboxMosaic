package com.mosaic.io.bytes;

/**
 *
 */
public class ArrayBytesTest extends BaseBytesTest {

    protected Bytes doCreateBytes( long numBytes ) {
        return new ArrayBytes( numBytes );
    }

}
