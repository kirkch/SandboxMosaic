package com.mosaic.io.filesystemx;

/**
 * An abstraction used for file access.  Designed to support unit testing.
 */
public interface FileSystemX {

    public int getNumberOfOpenFiles();

    /**
     * Returns true if this file system supports lockable files.  If it does not then calling
     * lockFile or unlockFile on FileX will throw an exception.
     */
    public boolean supportsLocking();

    public DirectoryX getRoot();
    public DirectoryX getCurrentWorkingDirectory();
    public void setCurrentWorkingDirectory( DirectoryX cwd );

    public DirectoryX getTempDirectory();
}
