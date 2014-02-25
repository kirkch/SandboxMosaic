package com.mosaic.io.bytes;

import com.mosaic.io.FileModeEnum;
import org.junit.After;

import java.io.File;
import java.io.IOException;


/**
 *
 */
public class MemoryMappedBytesTest extends BaseBytesTest {

    private File file;

    protected Bytes doCreateBytes( long numBytes ) throws IOException {
        file = File.createTempFile( "MemoryMappedBytesTest", ".dat" );

        return NativeBytes.memoryMapFile( file, FileModeEnum.READ_WRITE, numBytes );
    }


    @After
    public void tearDown() {
        super.tearDown();

        file.delete();
    }
}