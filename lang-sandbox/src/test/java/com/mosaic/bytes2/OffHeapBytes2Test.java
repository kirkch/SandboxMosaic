package com.mosaic.bytes2;

import com.mosaic.bytes2.impl.OffHeapBytes2;

import java.io.IOException;


public class OffHeapBytes2Test extends BaseBytesTest2 {

    @Override
    protected Bytes2 _createBytes( long numBytes ) throws IOException {
        return new OffHeapBytes2(numBytes);
    }

}