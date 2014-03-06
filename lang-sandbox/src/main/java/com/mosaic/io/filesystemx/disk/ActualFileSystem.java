package com.mosaic.io.filesystemx.disk;

import com.mosaic.io.filesystemx.FileSystemX;
import com.mosaic.lang.Validate;

import java.io.File;


/**
 *
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class ActualFileSystem extends ActualDirectory implements FileSystemX {

    private int openFileCount;


    public ActualFileSystem( File file ) {
        super( null, file );
    }


    public int getNumberOfOpenFiles() {
        return openFileCount;
    }

    @Override
    void incrementOpenFileCount() {
        openFileCount++;
    }

    @Override
    void decrementOpenFileCount() {
        openFileCount--;

        Validate.isGTEZero( openFileCount, "openFileCount" );
    }

}
