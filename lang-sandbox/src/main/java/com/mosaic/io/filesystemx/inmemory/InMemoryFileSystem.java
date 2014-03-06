package com.mosaic.io.filesystemx.inmemory;

import com.mosaic.io.filesystemx.FileSystemX;
import com.mosaic.lang.Validate;


/**
 *
 */
@SuppressWarnings("unchecked")
public class InMemoryFileSystem extends InMemoryDirectory implements FileSystemX {

    private int openFileCount;


    public InMemoryFileSystem() {
        this("");
    }

    public InMemoryFileSystem( String name ) {
        super( null, name );
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
