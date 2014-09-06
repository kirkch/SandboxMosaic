package com.mosaic.io.filesystemx.inmemory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.filesystemx.BaseFileSystemTestCases;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileSystemX;
import com.mosaic.io.filesystemx.FileX;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class InMemoryFileSystemTest extends BaseFileSystemTestCases {

    protected FileSystemX createFileSystem( String path ) {
        return new InMemoryFileSystem(path);
    }


    @Test
    public void createUnnamedInMemoryFileSystem() {
        InMemoryFileSystem fs = new InMemoryFileSystem();

        FileX newFile = fs.getCurrentWorkingDirectory().addFile( "welcome_msg.txt", "Hello" );

        Bytes fileContents = newFile.openFile( FileModeEnum.READ_ONLY );

        assertEquals( "Hello", fileContents.toString() );
    }

}
