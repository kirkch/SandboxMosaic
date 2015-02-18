package com.mosaic.bytes2;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes2.impl.Bytes1ToBytes2Adapter;

import java.io.IOException;


/**
 *
 */
public class Bytes1ToBytes2AdapterTest extends BaseBytesTest2 {

    @Override
    protected Bytes2 _createBytes( long numBytes ) throws IOException {
        return new Bytes1ToBytes2Adapter(new ArrayBytes(numBytes));
    }

}
