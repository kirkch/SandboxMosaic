package com.mosaic.io.filesystemx.inmemory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileSystemX;
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

    private final InMemoryFileSystem fileSystem;
    private final InMemoryDirectory  parentDirectoryNbl;

    private       String      directoryNameNbl;


    private List<InMemoryFile>      files       = new ArrayList();
    private List<InMemoryDirectory> directories = new ArrayList();


    /**
     * Create the root directory for a file system.
     */
    public InMemoryDirectory( InMemoryFileSystem fileSystem ) {
        QA.argNotNull( fileSystem, "fileSystem" );

        this.fileSystem         = fileSystem;
        this.parentDirectoryNbl = null;
    }

    public InMemoryDirectory( InMemoryFileSystem fileSystem, InMemoryDirectory parentDirectory, String directoryName ) {
        QA.argNotNull( fileSystem,      "fileSystem" );
        QA.argNotNull( parentDirectory, "parentDirectory" );
        QA.argNotNull( directoryName,   "directoryName" );

        this.fileSystem         = fileSystem;
        this.parentDirectoryNbl = parentDirectory;
        this.directoryNameNbl   = directoryName;
    }


    public FileX addFile( String filePath, String...contents ) {
        InMemoryFile file = getOrCreateFile0( filePath );

        file.setBytes( Bytes.wrap(ArrayUtils.makeString(contents, "\n")) );

        return file;
    }

    public String getFullPath() {
        if ( parentDirectoryNbl == null ) {
            return "/";
        } else {
            String parentPath = parentDirectoryNbl.getFullPath();

            return parentPath.equals("/")  ? parentPath + directoryNameNbl : parentDirectoryNbl.getFullPath()+"/"+ directoryNameNbl;
        }
    }

    public String getDirectoryNameNbl() {
        return directoryNameNbl;
    }

    public void deleteAll() {
        for ( FileX f : new ArrayList<>(files) ) {
            f.delete();
        }

        for ( DirectoryX d : new ArrayList<>(directories) ) {
            d.deleteAll();
        }

        if ( parentDirectoryNbl != null ) {
            parentDirectoryNbl.notificationThatChildDirectoryHasBeenDeleted( this );
        }
    }


    public boolean isEmpty() {
        return directories.isEmpty() && files.isEmpty();
    }

    public List<DirectoryX> directories() {
        return new ArrayList<>(directories);
    }

    public DirectoryX getOrCreateDirectory( String directoryName ) {
        DirectoryX d = getDirectory( directoryName );
        if ( d == null ) {
            d = createDirectory( directoryName );
        }

        return d;
    }

    public DirectoryX getParentDirectoryNbl() {
        return parentDirectoryNbl;
    }

    public DirectoryX getRoot() {
        return parentDirectoryNbl == null ? this : parentDirectoryNbl.getRoot();
    }

    public FileX copyFile( FileX sourceFile, String destinationPath ) {
        Bytes data = sourceFile.openFile( FileModeEnum.READ_ONLY );

        try {
            Bytes copy = Bytes.allocOnHeap( data.bufferLength() );
            copy.writeBytes( data );

            InMemoryFile destinationFile = getOrCreateFile0( destinationPath );

            destinationFile.setBytes( copy );

            return destinationFile;
        } finally {
            data.release();
        }
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
            if ( d.getDirectoryNameNbl().equals(directoryName) ) {
                return d;
            }
        }

        InMemoryDirectory newDirectory = new InMemoryDirectory( fileSystem, this, directoryName );

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

        InMemoryFile newFile = new InMemoryFile( fileSystem, this, fileName );

        files.add( newFile );

        return newFile;
    }

    void notificationThatChildDirectoryHasBeenDeleted( InMemoryDirectory d ) {
        directories.remove( d );
    }

    void notificationThatChildFileHasBeenDeleted( InMemoryFile f ) {
        files.remove( f );
    }



    private InMemoryDirectory getDirectory0( String dirPath ) {
//        QA.argNotBlank( dirPath, "dirPath" );

        InMemoryDirectory fromDir      = dirPath.startsWith("/") ? rootDirectory() : this;
        String[]          pathElements = dirPath.split( "/" );

        return fromDir.getDirectory0( pathElements, dirPath.startsWith("/") ? 1 : 0 );
    }

    private InMemoryDirectory getDirectory0( String[] pathElements, int i ) {
        if ( pathElements.length  == i + 1 ) {
            String dirName = pathElements[i];

            for ( InMemoryDirectory d : directories ) {
                if ( d.getDirectoryNameNbl().equals(dirName) ) {
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
            d = new InMemoryDirectory( fileSystem, this, path[i] );

            directories.add(d);
        }

        return d.createDirectoryFrom( path, i + 1 );
    }

    private InMemoryDirectory rootDirectory() {
        return (InMemoryDirectory) fileSystem.getRoot();
    }
}
