package com.mosaic.bytes2;

import com.mosaic.bytes2.impl.ArrayBytes2;

import java.io.IOException;


public class ArrayBytes2Test extends BaseBytesTest2 {

    @Override
    protected Bytes2 _createBytes( long numBytes ) throws IOException {
        return new ArrayBytes2(numBytes);
    }

}