package com.mosaic.io.filesystemx;

import com.mosaic.lang.functional.Predicate;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.system.SystemX;

import java.util.List;


/**
 * An abstraction used for file access.  Designed to support unit testing.
 */
public interface DirectoryX {

    public String getFullPath();

    public boolean isEmpty();

    public List<FileX> files();
    public List<FileX> files( String targetFileExtension );
    public List<FileX> files( Predicate<FileX> matchingCondition );

    /**
     *
     * @return null if the file does not exist
     */
    public FileX getFile( String fileName );

    public FileX getOrCreateFile( String fileName );

    public List<DirectoryX> directories();
    public DirectoryX getDirectory( String dirPath );

    public DirectoryX createDirectory( String dirPath );

    public default DirectoryX createDirectoryWithRandomName() {
        return createDirectory( ReflectionUtils.getCallersClass().getSimpleName() + "_" + SystemX.nextRandomLong() );
    }

    public default DirectoryX createDirectoryWithRandomName( String prefix, String postfix ) {
        return createDirectory( prefix + SystemX.nextRandomLong() + postfix );
    }


    /**
     * Add a text file to this directory.
     */
    public FileX addFile( String filePath, String...contents );

    public String getDirectoryNameNbl();

    public void deleteAll();


    public DirectoryX getOrCreateDirectory( String directoryName );


    /**
     * Copy the specified sourceFile to the destinationPath.
     *
     * @return the new file created at destinationPath
     */
    public FileX copyFile( FileX sourceFile, String destinationPath );

    public DirectoryX getParentDirectoryNbl();
    public DirectoryX getRoot();

}
