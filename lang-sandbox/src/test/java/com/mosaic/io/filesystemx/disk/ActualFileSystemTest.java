package com.mosaic.io.filesystemx.disk;

import com.mosaic.io.filesystemx.BaseFileSystemTestCases;
import com.mosaic.io.filesystemx.FileSystemX;

import java.io.File;


/**
 *
 */
public class ActualFileSystemTest extends BaseFileSystemTestCases {

    protected FileSystemX createFileSystem( String path ) {
        return new ActualFileSystem( new File(path) );
    }

}
