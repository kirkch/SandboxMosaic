package com.mosaic.bytes;

import java.io.IOException;


public class OffHeapBytesTest extends BaseBytesTest {

    @Override
    protected Bytes _createBytes( long numBytes ) throws IOException {
        return new OffHeapBytes(numBytes);
    }

}