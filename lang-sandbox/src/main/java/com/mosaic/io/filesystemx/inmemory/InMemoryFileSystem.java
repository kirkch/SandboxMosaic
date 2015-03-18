package com.mosaic.io.filesystemx.inmemory;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileSystemX;
import com.mosaic.lang.QA;

import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 */
@SuppressWarnings("unchecked")
public class InMemoryFileSystem implements FileSystemX {

    private DirectoryX root;
    private DirectoryX cwd;

    private AtomicInteger openFileCount = new AtomicInteger(0);


    public InMemoryFileSystem() {
        this.root = new InMemoryDirectory( this );
        this.cwd  = this.root;
    }

    public InMemoryFileSystem( String path ) {
        this.root = new InMemoryDirectory( this );
        this.cwd  = root.createDirectory( path );
        this.root = cwd;
    }


    public int getNumberOfOpenFiles() {
        return openFileCount.get();
    }

    public boolean supportsLocking() {
        return true;
    }

    void incrementOpenFileCount() {
        openFileCount.incrementAndGet();
    }

    void decrementOpenFileCount() {
        int newValue = openFileCount.decrementAndGet();

        QA.isGTEZero( newValue, "openFileCount" );
    }

    public DirectoryX getRoot() {
        return root;
    }

    public DirectoryX getCurrentWorkingDirectory() {
        return cwd;
    }

    public void setCurrentWorkingDirectory( DirectoryX newCWD ) {
        QA.argIsTrue( cwd.getFullPath().startsWith(this.root.getFullPath()), "newCWD" );

        this.cwd = newCWD;
    }

    public DirectoryX getTempDirectory() {
        return getRoot().getOrCreateDirectory( "tmp" );
    }
}
