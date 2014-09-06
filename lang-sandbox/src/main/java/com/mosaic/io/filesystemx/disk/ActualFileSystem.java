package com.mosaic.io.filesystemx.disk;

import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileSystemX;
import com.mosaic.lang.QA;

import java.io.File;


/**
 *
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class ActualFileSystem implements FileSystemX {

    private DirectoryX root;
    private DirectoryX cwd;

    private int        openFileCount;


    public ActualFileSystem( String root ) {
        this( new File(root) );
    }

    public ActualFileSystem( File root ) {
        QA.argIsNotEqualTo( "/", root.getAbsolutePath(), "root" );

        this.root = new ActualDirectory( this, root );
        this.cwd  = this.root;
    }


    public int getNumberOfOpenFiles() {
        return openFileCount;
    }

    void incrementOpenFileCount() {
        openFileCount++;
    }

    void decrementOpenFileCount() {
        openFileCount--;

        QA.isGTEZero( openFileCount, "openFileCount" );
    }

    public boolean supportsLocking() {
        return true;
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

}
