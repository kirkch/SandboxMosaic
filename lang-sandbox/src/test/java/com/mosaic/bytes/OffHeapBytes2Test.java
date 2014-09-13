package com.mosaic.bytes;

import java.io.IOException;


public class OffHeapBytes2Test extends BaseBytesTest {

    @Override
    protected Bytes2 _createBytes( long numBytes ) throws IOException {
        return OffHeapBytes2.alloc(numBytes);
    }

}