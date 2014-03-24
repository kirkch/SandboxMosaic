package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.system.DebugSystem;


/**
 *
 */
public class FlyWeightBytesTest extends BaseFlyWeightTestCases {

    private DebugSystem system = new DebugSystem();


    protected RedBullFlyWeight createNewFlyWeight() {
        Bytes bytes = Bytes.allocOnHeap( 1024 );

        return new RedBullFlyWeight( system, bytes );
    }


}
