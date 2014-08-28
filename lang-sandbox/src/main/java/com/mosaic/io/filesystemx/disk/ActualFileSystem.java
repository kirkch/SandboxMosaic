package com.mosaic.io.filesystemx.disk;

import com.mosaic.io.filesystemx.FileSystemX;
import com.mosaic.lang.QA;

import java.io.File;


/**
 *
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class ActualFileSystem extends ActualDirectory implements FileSystemX {

    private int openFileCount;


    public ActualFileSystem() {
        this( new File( "/" ) );
    }

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

        QA.isGTEZero( openFileCount, "openFileCount" );
    }

    public boolean supportsLocking() {
        return true;
    }

}
