package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class FlyWeightBytesWithStartOffsetTest extends BaseFlyWeightTestCases {

    private DebugSystem system = new DebugSystem();
    private Bytes       bytes;

    protected RedBullFlyWeight createNewFlyWeight() {
        bytes = Bytes.allocOnHeap( 1024 );

        int stringLength = bytes.writeUTF8String( 0, "hello" );

        return new RedBullFlyWeight( system, stringLength, bytes );
    }

    @Test
    public void ensureThatAnyDataWithinTheStartOffsetRegionDoesNotGetOverwritten() {
        allocateAndPopulateBulls( 3 );

        StringBuilder buf = new StringBuilder();
        bytes.readUTF8String( 0, buf );

        assertEquals( "hello", buf.toString() );
    }

}

