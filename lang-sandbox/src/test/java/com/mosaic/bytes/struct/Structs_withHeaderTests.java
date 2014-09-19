package com.mosaic.bytes.struct;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes.AutoResizingBytes;
import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.struct.examples.redbull.RedBullStructs;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class Structs_withHeaderTests extends BaseStructsTestCases {

    private SystemX system  = new DebugSystem();
    private Bytes bytes;

    protected RedBullStructs createNewRedBull( long initialSize ) {
        bytes = new AutoResizingBytes( system, new ArrayBytes(1024), "StructsTests", 100_000 );

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
