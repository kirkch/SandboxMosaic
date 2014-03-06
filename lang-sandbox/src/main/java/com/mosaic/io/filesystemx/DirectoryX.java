package com.mosaic.io.filesystemx;

import com.mosaic.lang.functional.Predicate;

import java.util.List;


/**
 * An abstraction used for file access.  Designed to support unit testing.
 */
public interface DirectoryX {

    public String getFullPath();

    public List<FileX> files();
    public List<FileX> files( String targetFileExtension );
    public List<FileX> files( Predicate<FileX> matchingCondition );
    public FileX getFile( String fileName );

    public List<DirectoryX> directories();
    public DirectoryX getDirectory( String dirPath );

    public DirectoryX createDirectory( String dirPath );
    public DirectoryX createDirectoryWithRandomName( String prefix, String postfix );



    /**
     * Add a text file to this directory.
     */
    public FileX addFile( String fileName, String...contents );

    public String getDirectoryName();

    public void deleteAll();


    public DirectoryX getOrCreateDirectory( String directoryName );
}
