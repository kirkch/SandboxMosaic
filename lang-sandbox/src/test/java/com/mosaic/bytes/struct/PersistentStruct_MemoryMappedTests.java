package com.mosaic.bytes.struct;

import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;


/**
 *
 */
public class PersistentStruct_MemoryMappedTests extends BasePersistentStructTestCases {

    @Override
    protected SystemX createSystem() {
        return DebugSystem.withActualFileSystem();
    }

}
