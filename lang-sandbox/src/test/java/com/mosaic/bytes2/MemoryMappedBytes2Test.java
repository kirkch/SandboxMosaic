package com.mosaic.bytes2;

import com.mosaic.bytes2.impl.MemoryMappedBytes2;
import com.mosaic.io.filesystemx.FileModeEnum;
import org.junit.After;

import java.io.File;
import java.io.IOException;


/**
 *
 */
public class MemoryMappedBytes2Test extends BaseBytesTest2 {

    private File file;


    protected Bytes2 _createBytes( long numBytes ) throws IOException {
        file = File.createTempFile( "MemoryMappedBytesTest", ".dat" );

        return MemoryMappedBytes2.mapFile( file, FileModeEnum.READ_WRITE, numBytes );
    }

    @After
    public void tearDown() {
        super.tearDown();

        file.delete();
    }

}
