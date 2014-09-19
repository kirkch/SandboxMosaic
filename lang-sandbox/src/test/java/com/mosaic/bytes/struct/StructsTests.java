package com.mosaic.bytes.struct;

import com.mosaic.bytes.ArrayBytes2;
import com.mosaic.bytes.AutoResizingBytes2;
import com.mosaic.bytes.Bytes2;
import com.mosaic.bytes.struct.examples.redbull.RedBullStructs;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;


/**
 *
 */
public class StructsTests extends BaseStructsTestCases {

    private SystemX system  = new DebugSystem();

    protected RedBullStructs createNewRedBull( long initialSize ) {
        Bytes2 bytes = new AutoResizingBytes2( system, new ArrayBytes2(1024), "StructsTests", 100_000 );

        return new RedBullStructs( bytes );
    }

}
