package com.mosaic.bytes.struct;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes.AutoResizingBytes;
import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.struct.examples.redbull.RedBullStructs;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;


/**
 *
 */
public class StructsTests extends BaseStructsTestCases {

    private SystemX system  = new DebugSystem();

    protected RedBullStructs createNewRedBull( long initialSize ) {
        Bytes bytes = new AutoResizingBytes( system, new ArrayBytes(1024), "StructsTests", 100_000 );

        return new RedBullStructs( bytes );
    }

}
