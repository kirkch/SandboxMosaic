package com.mosaic.io.filesystemx.inmemory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.Predicate;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@SuppressWarnings("unchecked")
public class InMemoryDirectory implements DirectoryX {

    private InMemoryDirectory parentDirectory;
    private String            directoryName;


    private List<InMemoryFile>      files       = new ArrayList();
    private List<InMemoryDirectory> directories = new ArrayList();


    public InMemoryDirectory( InMemoryDirectory parentDirectory, String directoryName ) {
        QA.argNotNull( directoryName, "directoryName" );

        this.parentDirectory = parentDirectory;
        this.directoryName   = directoryName;
    }


    public FileX addFile( String filePath, String...contents ) {
        InMemoryFile file = getOrCreateFile0( filePath );

        file.setBytes( Bytes.wrap(ArrayUtils.makeString(contents, "\n")) );

        return file;
    }

    public String getFullPath() {
        return parentDirectory == null ? "/"+directoryName : parentDirectory.getFullPath()+"/"+directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void deleteAll() {
        for ( FileX f : new ArrayList<>(files) ) {
            f.delete();
        }

        for ( DirectoryX d : new ArrayList<>(directories) ) {
            d.deleteAll();
        }

        if ( parentDirectory != null ) {
            parentDirectory.notificationThatChildDirectoryHasBeenDeleted( this );
        }
    }


    public boolean isEmpty() {
        return directories.isEmpty() && files.isEmpty();
    }

    public List<DirectoryX> directories() {
        return new ArrayList<DirectoryX>(directories);
    }

    public DirectoryX getOrCreateDirectory( String directoryName ) {
        DirectoryX d = getDirectory( directoryName );
        if ( d == null ) {
            d = createDirectory( directoryName );
        }

        return d;
    }

    public DirectoryX createDirectory( String dirPath ) {
        InMemoryDirectory existingDirectory = getDirectory0( dirPath );
        if ( existingDirectory != null ) {
            return existingDirectory;
        }

        InMemoryDirectory fromDir      = dirPath.startsWith("/") ? rootDirectory() : this;
        String[]          pathElements = dirPath.split( "/" );

        return fromDir.createDirectoryFrom( pathElements, dirPath.startsWith("/") ? 1 : 0 );
    }

    public DirectoryX getDirectory( String dirPath ) {
        return getDirectory0( dirPath );
    }



    public DirectoryX createDirectoryWithRandomName( String prefix, String postfix ) {
        return createDirectory( prefix + SystemX.nextRandomLong() + postfix );
    }

    public List<FileX> files() {
        return new ArrayList( files );
    }

    public List<FileX> files( final String targetFileExtension ) {
        return files( new Predicate<FileX>() {
            public boolean invoke( FileX arg ) {
                return arg.getFullPath().endsWith( targetFileExtension );
            }
        });
    }

    public List<FileX> files( final Predicate<FileX> matchingCondition ) {
        List<FileX> matchingFiles = new ArrayList<>();

        for ( FileX f : files ) {
            if ( matchingCondition.invoke(f) ) {
                matchingFiles.add( f );
            }
        }

        return matchingFiles;
    }

    public FileX getFile( String filePath ) {
        return getFile0( filePath );
    }

    public FileX getOrCreateFile( String filePath ) {
        return getOrCreateFile0( filePath );
    }

    private InMemoryFile getOrCreateFile0( String filePath ) {
        QA.argNotBlank( filePath, "filePath" );

        String[] path = filePath.split( "/" );
        if ( filePath.length() == 1 ) {
            return getOrCreateFileByName0( path[0] );
        } else if ( filePath.startsWith("/") ) {
            return rootDirectory().getOrCreateFile0( path, 1 );
        } else {
            return this.getOrCreateFile0( path, 0 );
        }
    }

    private InMemoryFile getFile0( String filePath ) {
        QA.argNotBlank( filePath, "filePath" );

        String[] path = filePath.split( "/" );
        if ( filePath.startsWith("/") ) {
            return rootDirectory().getFile0( path, 1 );
        } else {
            return this.getFile0( path, 0 );
        }
    }

    private InMemoryFile getOrCreateFile0( String[] path, int i ) {
        if ( i + 1 == path.length ) {
            return getOrCreateFileByName0( path[i] );
        } else {
            return getOrCreateDirectory0( path[i] ).getOrCreateFile0( path, i + 1 );
        }
    }

    private InMemoryFile getFile0( String[] path, int i ) {
        if ( i + 1 == path.length ) {
            return getFileByName0( path[i] );
        } else {
            return getDirectory0( path[i] ).getFile0( path, i + 1 );
        }
    }

    private InMemoryDirectory getOrCreateDirectory0( String directoryName ) {
        for ( InMemoryDirectory d : directories ) {
            if ( d.getDirectoryName().equals(directoryName) ) {
                return d;
            }
        }

        InMemoryDirectory newDirectory = new InMemoryDirectory( this, directoryName );

        directories.add( newDirectory );

        return newDirectory;
    }


    private InMemoryFile getFileByName0( String fileName ) {
        QA.argNotBlank( fileName, "fileName" );

        for ( InMemoryFile f : files ) {
            if ( f.getFileName().equals(fileName) ) {
                return f;
            }
        }

        return null;
    }

    private InMemoryFile getOrCreateFileByName0( String fileName ) {
        QA.argNotBlank( fileName, "fileName" );

        for ( InMemoryFile f : files ) {
            if ( f.getFileName().equals(fileName) ) {
                return f;
            }
        }

        InMemoryFile newFile = new InMemoryFile( this, fileName );

        files.add( newFile );

        return newFile;
    }

    void notificationThatChildDirectoryHasBeenDeleted( InMemoryDirectory d ) {
        directories.remove( d );
    }

    void notificationThatChildFileHasBeenDeleted( InMemoryFile f ) {
        files.remove( f );
    }

    void incrementOpenFileCount() {
        if ( parentDirectory != null ) {
            parentDirectory.incrementOpenFileCount();
        }
    }

    void decrementOpenFileCount() {
        if ( parentDirectory != null ) {
            parentDirectory.decrementOpenFileCount();
        }
    }




    private InMemoryDirectory getDirectory0( String dirPath ) {
        QA.argNotBlank( dirPath, "dirPath" );

        InMemoryDirectory fromDir      = dirPath.startsWith("/") ? rootDirectory() : this;
        String[]          pathElements = dirPath.split( "/" );

        return fromDir.getDirectory0( pathElements, dirPath.startsWith("/") ? 1 : 0 );
    }

    private InMemoryDirectory getDirectory0( String[] pathElements, int i ) {
        if ( pathElements.length  == i + 1 ) {
            String dirName = pathElements[i];

            for ( InMemoryDirectory d : directories ) {
                if ( d.getDirectoryName().equals(dirName) ) {
                    return d;
                }
            }

            return null;
        } else {
            InMemoryDirectory dir = getDirectory0( pathElements[i] );
            if ( dir == null ) {
                return null;
            } else {
                return dir.getDirectory0( pathElements, i+1 );
            }
        }
    }

    private DirectoryX createDirectoryFrom( String[] path, int i ) {
        if ( i == path.length ) {
            return this;
        }

        InMemoryDirectory d = getDirectory0( path[i] );
        if ( d == null ) {
            d = new InMemoryDirectory( this, path[i] );

            directories.add(d);
        }

        return d.createDirectoryFrom( path, i + 1 );
    }

    private InMemoryDirectory rootDirectory() {
        if ( parentDirectory == null ) {
            return this;
        } else {
            return parentDirectory.rootDirectory();
        }
    }
}