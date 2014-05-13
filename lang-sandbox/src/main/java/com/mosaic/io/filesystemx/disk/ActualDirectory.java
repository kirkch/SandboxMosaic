package com.mosaic.io.filesystemx.disk;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.filesystemx.DirectoryX;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.Predicate;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.ArrayUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@SuppressWarnings("ConstantConditions")
public class ActualDirectory implements DirectoryX {

    private ActualDirectory parentDirectory;
    private File            file;


    public ActualDirectory( ActualDirectory parentDirectory, File file ) {
        QA.argNotNull( file, "file" );

        this.parentDirectory = parentDirectory;
        this.file            = file;
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
                    files.add( new ActualFile(this, f) );
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
                    files.add( new ActualFile(this, f) );
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
                    ActualFile wrappedFile = new ActualFile( this, f );

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

        throwIfDirectory( child );

        return new ActualFile( this, child );
    }

    public FileX getOrCreateFile( String fileName ) {
        QA.argNotBlank( fileName, "fileName" );

        File child = new File(file, fileName);

        throwIfDirectory( child );

        return new ActualFile( this, child );
    }

    public List<DirectoryX> directories() {
        List<DirectoryX> files = new ArrayList<>();

        File[] children = file.listFiles();
        if ( children != null ) {
            for ( File f : children ) {
                if ( f.isDirectory() ) {
                    files.add( new ActualDirectory(this, f) );
                }
            }
        }

        return files;
    }

    public DirectoryX getDirectory( String dirName ) {
        QA.argNotBlank( dirName, "dirName" );

        File child = new File(file, dirName);

        if ( !child.exists() ) {
            return null;
        }

        throwIfNotDirectory(child);

        return new ActualDirectory( this, child );
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

        return new ActualDirectory( this, child );
    }

    public DirectoryX createDirectoryWithRandomName( String prefix, String postfix ) {
        return createDirectory( prefix+ SystemX.nextRandomLong()+postfix );
    }

    public FileX addFile( String filePath, String... contents ) {
        File child = new File(file, filePath);
        throwIfDirectory( child );

        child.getParentFile().mkdirs();

        String text  = ArrayUtils.makeString( contents, "\n" );
        byte[] bytes = text.getBytes( SystemX.UTF8 );


        Bytes textFileBytes = Bytes.memoryMapFile(child, FileModeEnum.READ_WRITE, bytes.length );

        textFileBytes.writeBytes( bytes );
        textFileBytes.release();

        return new ActualFile( this, child );
    }

    public String getDirectoryName() {
        return file.getName();
    }

    public void deleteAll() {
        File[] children = file.listFiles();
        if ( children != null ) {
            for ( File child : children ) {
                if ( child.isDirectory() ) {
                    new ActualDirectory(this,child).deleteAll();
                } else {
                    child.delete();
                }
            }
        }

        file.delete();
    }

    public FileX copyFile( FileX sourceFile, String destinationPath ) {
        File destinationFile = new File(file, destinationPath);
        throwIfDirectory( destinationFile );

        destinationFile.getParentFile().mkdirs();

        Bytes sourceBytes = sourceFile.loadBytes( FileModeEnum.READ_ONLY );
        try {
            Bytes textFileBytes = Bytes.memoryMapFile(destinationFile, FileModeEnum.READ_WRITE, sourceBytes.bufferLength() );

            try {
                textFileBytes.writeBytes( sourceBytes );
            } finally {
                textFileBytes.release();
            }
        } finally {
            sourceBytes.release();
        }

        return new ActualFile( this, destinationFile );
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
        if ( parentDirectory == null ) {
            return this;
        } else {
            return parentDirectory.rootDirectory();
        }
    }
}
