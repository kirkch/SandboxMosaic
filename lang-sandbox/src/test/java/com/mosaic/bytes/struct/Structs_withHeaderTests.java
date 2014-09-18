package com.mosaic.bytes.struct;

import com.mosaic.bytes.ArrayBytes2;
import com.mosaic.bytes.AutoResizingBytes2;
import com.mosaic.bytes.Bytes2;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class Structs_withHeaderTests extends BaseStructsTestCases {

    private SystemX system  = new DebugSystem();
    private Bytes2  bytes;

    protected RedBullStructs createNewRedBull( long initialSize ) {
        bytes = new AutoResizingBytes2( system, new ArrayBytes2(1024), "StructsTests", 100_000 );

        int stringLength = bytes.writeUTF8String( 0, 20, "hello" );

        return new RedBullStructs( bytes, stringLength );
    }

    @Test
    public void ensureThatAnyDataWithinTheStartOffsetRegionDoesNotGetOverwritten() {
        allocateAndPopulateBulls( 3 );

        StringBuilder buf = new StringBuilder();
        bytes.readUTF8String( 0, 20, buf );

        assertEquals( "hello", buf.toString() );
    }

}
