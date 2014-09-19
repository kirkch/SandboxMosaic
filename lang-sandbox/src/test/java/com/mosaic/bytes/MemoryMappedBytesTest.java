package com.mosaic.bytes;

import com.mosaic.io.filesystemx.FileModeEnum;
import org.junit.After;

import java.io.File;
import java.io.IOException;


/**
 *
 */
public class MemoryMappedBytesTest extends BaseBytesTest {

    private File file;


    protected Bytes _createBytes( long numBytes ) throws IOException {
        file = File.createTempFile( "MemoryMappedBytesTest", ".dat" );

        return MemoryMappedBytes.mapFile( file, FileModeEnum.READ_WRITE, numBytes );
    }

    @After
    public void tearDown() {
        super.tearDown();

        file.delete();
    }

}
