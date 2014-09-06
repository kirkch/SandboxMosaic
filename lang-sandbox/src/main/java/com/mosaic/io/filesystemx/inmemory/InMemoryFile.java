package com.mosaic.io.filesystemx.inmemory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.InputStreamAdapter;
import com.mosaic.io.bytes.WrappedBytes;
import com.mosaic.io.filesystemx.FileContents;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.utils.PropertyUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;


/**
 *
 */
public class InMemoryFile implements FileX {

    private InMemoryFileSystem fileSystem;
    private InMemoryDirectory  parentDirectory;
    private String             fileName;
    private Bytes              bytes;

    private boolean            hasBeenDeletedFlag;

    private boolean isReadable   = true;
    private boolean isWritable   = true;
    private boolean isExecutable = false;


    InMemoryFile( InMemoryFileSystem fileSystem, InMemoryDirectory parentDirectory, String fileName ) {
        this( fileSystem, parentDirectory, fileName, Bytes.wrap( "" ) );
    }

    InMemoryFile( InMemoryFileSystem fileSystem, InMemoryDirectory parentDirectory, String fileName, Bytes bytes ) {
        QA.argNotNull( fileSystem, "fileSystem" );
        QA.argNotNull( parentDirectory, "parentDirectory" );
        QA.argNotBlank( fileName, "fileName" );
        QA.argNotNull( bytes, "bytes" );

        this.fileSystem      = fileSystem;
        this.parentDirectory = parentDirectory;
        this.fileName        = fileName;
        this.bytes           = bytes;

        bytes.setName(fileName);
    }

    public String getFileName() {
        throwIfDeleted();

        return fileName;
    }

    public FileContents openFile( FileModeEnum mode ) {
        throwIfDeleted();

        fileSystem.incrementOpenFileCount();

        if ( this.bytes.bufferLength() > 0 ) {
            this.bytes.positionIndex( 0 );
        }

        return new InMemoryFileContents(
            new WrappedBytes(bytes) {
                public void release() {
                    if ( parentDirectory == null ) { // already deleted
                        return;
                    }

                    fileSystem.decrementOpenFileCount();
                }
            }
        );
    }

    public FileContents openFile( FileModeEnum mode, int sizeInBytes ) {
        bytes.resize( sizeInBytes );

        return openFile( mode );
    }

    public long sizeInBytes() {
        return bytes == null ? 0 : bytes.bufferLength();
    }

    public String getFullPath() {
        throwIfDeleted();

        return parentDirectory + "/" + fileName;
    }

    public void delete() {
        if ( hasBeenDeletedFlag ) {
            return;
        }

        bytes.release();
        parentDirectory.notificationThatChildFileHasBeenDeleted( this );

        this.bytes              = null;
        this.parentDirectory    = null;
        this.hasBeenDeletedFlag = true;
    }

    public void isReadable( boolean isReadable ) {
        this.isReadable = isReadable;
    }

    public boolean isReadable() {
        return isReadable;
    }

    public void isWritable( boolean isWritable ) {
        this.isWritable = isWritable;
    }

    public boolean isWritable() {
        return isWritable;
    }

    public void isExecutable( boolean isExecutable ) {
        this.isExecutable = isExecutable;
    }

    public boolean isExecutable() {
        return isExecutable;
    }

    public String toString() {
        return hasBeenDeletedFlag ? "DELETED" : getFullPath();
    }

    private void throwIfDeleted() {
        if ( hasBeenDeletedFlag ) {
            throw new IllegalStateException( this.fileName + " has been deleted" );
        }
    }

    void setBytes( Bytes newBytes ) {
        this.bytes = newBytes;

        newBytes.setName( fileName );
    }

    private boolean isLocked;
    private class InMemoryFileContents extends FileContents {

        public InMemoryFileContents( Bytes delegate ) {
            super( delegate );
        }

        public boolean lockFile() {
            if ( isLocked ) {
                return false;
            } else {
                isLocked = true;

                return true;
            }
        }

        public boolean isLocked() {
            return isLocked;
        }

        public boolean unlockFile() {
            if ( isLocked ) {
                isLocked = false;

                return true;
            } else {
                return false;
            }
        }
    }
}
