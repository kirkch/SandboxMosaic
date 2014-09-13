package com.mosaic.bytes;

import java.io.IOException;


public class ArrayBytes2Test extends BaseBytesTest {

    @Override
    protected Bytes2 _createBytes( long numBytes ) throws IOException {
        return new ArrayBytes2(numBytes);
    }

}