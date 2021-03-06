package com.mosaic.collections.queue.journal;

import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(JUnitMosaicRunner.class )
public class JournalWriterMemoryMappedFileTests extends BaseJournalWriterTestCases {

    protected SystemX createSystem() {
        return DebugSystem.withActualFileSystem();
    }

}
