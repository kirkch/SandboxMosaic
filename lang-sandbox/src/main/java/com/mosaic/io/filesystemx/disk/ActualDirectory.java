package com.mosaic.io.filesystemx.disk;

import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.MemoryMappedBytes;
import com.mosaic.io.FileUtils;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.Predicate;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.ArrayUtils;
import com.mosaic.utils.SetUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 *
 */
@SuppressWarnings("ConstantConditions")
public class ActualDirectory implements DirectoryX {

    private static final Set<String> SecurePaths = SetUtils.asImmutableSet(
        "/", "/bin", "/usr", "/home", "/var", "/User", "/Applications", "/Network", "/mnt", "/Volumes", ""
    );


    private ActualFileSystem fileSystem;
    private ActualDirectory parentDirectoryNbl;
    private File             file;


    public ActualDirectory( ActualFileSystem fileSystem, File file ) {
        QA.argNotNull( file, "file" );

        this.fileSystem      = fileSystem;
        this.file            = file;
    }

    public ActualDirectory( ActualFileSystem fileSystem, ActualDirectory parentDirectory, File file ) {
        QA.argNotNull( fileSystem, "fileSystem" );
        QA.argNotNull( parentDirectory, "parentDirectory" );
        QA.argNotNull( file, "file" );

        this.fileSystem         = fileSystem;
        this.parentDirectoryNbl = parentDirectory;
        this.file               = file;
    }


    public String getFullPath() {
        return file.getAbsolutePath();
    }

    public boolean isEmpty() {
        File[] contents = file.listFiles();

        return contents == null || contents.length == 0;
    }

    public List<FileX> files() {
        List<FileX> files = new ArrayList<>();

        File[] children = file.listFiles();
        if ( children != null ) {
            for ( File f : children ) {
                if ( f.isFile() ) {
                    files.add( new ActualFile(fileSystem, f) );
                }
            }
        }

        return files;
    }

    public List<FileX> files( String targetFileExtension ) {
        List<FileX> files = new ArrayList<>();

        File[] children = file.listFiles();
        if ( children != null ) {
            for ( File f : children ) {
                if ( f.isFile() && f.getName().endsWith(targetFileExtension) ) {
                    files.add( new ActualFile(fileSystem, f) );
                }
            }
        }

        return files;
    }

    public List<FileX> files( Predicate<FileX> matchingCondition ) {
        List<FileX> files = new ArrayList<>();

        File[] children = file.listFiles();
        if ( children != null ) {
            for ( File f : children ) {
                if ( f.isFile()  ) {
                    ActualFile wrappedFile = new ActualFile( fileSystem, f );

                    if ( matchingCondition.invoke(wrappedFile) ) {
                        files.add( wrappedFile );
                    }
                }
            }
        }

        return files;
    }

    public FileX getFile( String fileName ) {
        QA.argNotBlank( fileName, "fileName" );

        File fromDirectory = fileName.startsWith( "/" ) ? rootDirectory().file : file;
        File child         = new File( fromDirectory, fileName);

        if ( !child.exists() ) {
            return null;
        }

//        throwIfDirectory( child );
        if ( child.isDirectory() ) {
            return null;
        }

        return new ActualFile( fileSystem, child );
    }

    public FileX getOrCreateFile( String fileName ) {
        QA.argNotBlank( fileName, "fileName" );

        File child = new File(file, fileName);

        throwIfDirectory( child );

        return new ActualFile( fileSystem, child );
    }

    public List<DirectoryX> directories() {
        List<DirectoryX> files = new ArrayList<>();

        File[] children = file.listFiles();
        if ( children != null ) {
            for ( File f : children ) {
                if ( f.isDirectory() ) {
                    files.add( new ActualDirectory(fileSystem, this, f) );
                }
            }
        }

        return files;
    }

    public DirectoryX getDirectory( String dirPath ) {
        QA.argNotBlank( dirPath, "dirPath" );

        if ( isAbsolutePath(dirPath) && parentDirectoryNbl != null ) {
            return getRoot().getDirectory(dirPath);
        }

        File child = new File(file, dirPath);

        if ( !child.exists() ) {
            return null;
        }

        throwIfNotDirectory(child);

        return new ActualDirectory( fileSystem, this, child );
    }

    public DirectoryX getRoot() {
        DirectoryX c = this;
        DirectoryX p = c.getParentDirectoryNbl();

        while ( p != null ) {
            c = p;
            p = c.getParentDirectoryNbl();
        }

        return c;
    }

    private boolean isAbsolutePath( String dirPath ) {
        return dirPath.startsWith("/");
    }

    public DirectoryX getOrCreateDirectory( String directoryName ) {
        DirectoryX d = getDirectory( directoryName );
        if ( d == null ) {
            d = createDirectory( directoryName );
        }

        return d;
    }

    public DirectoryX createDirectory( String dirName ) {
        QA.argNotBlank( dirName, "dirName" );

        File child = new File(file, dirName);
        if ( !child.exists() ) {
            child.mkdirs();
        } else {
            throwIfNotDirectory(child);
        }

        return new ActualDirectory( fileSystem, this, child );
    }

    public FileX addFile( String filePath, String... contents ) {
        File child = new File(file, filePath);
        throwIfDirectory( child );

        child.getParentFile().mkdirs();

        String text  = ArrayUtils.makeString( contents, "\n" );
        byte[] bytes = text.getBytes( SystemX.UTF8 );


        Bytes textFileBytes = MemoryMappedBytes.mapFile( child, FileModeEnum.READ_WRITE, bytes.length );

        textFileBytes.writeBytes( 0, bytes.length, bytes );
        textFileBytes.release();

        return new ActualFile( fileSystem, child );
    }

    public String getDirectoryNameNbl() {
        return file.getName();
    }

    public void deleteAll() {
        if ( SecurePaths.contains( file.getAbsolutePath() ) ) {
            throw new SecurityException( file.getAbsolutePath() );
        }

        FileUtils.deleteAll( file );
    }

    public DirectoryX getParentDirectoryNbl() {
        return parentDirectoryNbl;
    }

    public FileX copyFile( FileX sourceFile, String destinationPath ) {
        File destinationFile = new File(file, destinationPath);
        throwIfDirectory( destinationFile );

        destinationFile.getParentFile().mkdirs();

        Bytes sourceBytes = sourceFile.openFile( FileModeEnum.READ_ONLY );
        try {
            Bytes textFileBytes = MemoryMappedBytes.mapFile( destinationFile, FileModeEnum.READ_WRITE, sourceBytes.sizeBytes() );

            try {
                textFileBytes.writeBytes( 0, sourceBytes.sizeBytes(), sourceBytes );
            } finally {
                textFileBytes.release();
            }
        } finally {
            sourceBytes.release();
        }

        return new ActualFile( fileSystem, destinationFile );
    }

    public String toString() {
        return file.toString();
    }

    private void throwIfNotDirectory( File child ) {
        if ( !child.isDirectory() ) {
            throw new IllegalStateException( "Expected "+child.getAbsoluteFile()+" to be a directory" );
        }
    }

    private void throwIfDirectory( File child ) {
        if ( child.isDirectory() ) {
            throw new IllegalStateException( "Did not expect "+child.getAbsoluteFile()+" to be a directory" );
        }
    }

    private ActualDirectory rootDirectory() {
        if ( parentDirectoryNbl == null ) {
            return this;
        } else {
            return parentDirectoryNbl.rootDirectory();
        }
    }
}
