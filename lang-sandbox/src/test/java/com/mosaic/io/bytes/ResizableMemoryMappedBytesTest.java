package com.mosaic.io.bytes;

import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.After;

import java.io.File;
import java.io.IOException;


/**
 *
 */
public class ResizableMemoryMappedBytesTest extends BaseBytesTest {

    private SystemX system = new DebugSystem();
    private File file;


    protected Bytes doCreateBytes( long numBytes ) throws IOException {
        file = File.createTempFile( "MemoryMappedBytesTest", ".dat" );

        return Bytes.memoryAutoResizingMapFile( "test", system, file, FileModeEnum.READ_WRITE, numBytes, numBytes + 500 );
    }

    @After
    public void tearDown() {
        super.tearDown();

        file.delete();
    }

}
