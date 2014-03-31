package com.mosaic.io.bytes;

import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;

import java.io.IOException;


/**
 *
 */
public class ResizableOffHeapBytesTest extends BaseBytesTest {

    private SystemX system = new DebugSystem();


    protected Bytes doCreateBytes( long numBytes ) throws IOException {
        return Bytes.allocAutoResizingOffHeap( "test", system, numBytes, numBytes+500 );
    }

}
