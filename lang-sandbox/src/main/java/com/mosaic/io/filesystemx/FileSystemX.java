package com.mosaic.io.filesystemx;

/**
 * An abstraction used for file access.  Designed to support unit testing.
 */
public interface FileSystemX extends DirectoryX {

    public int getNumberOfOpenFiles();

}
