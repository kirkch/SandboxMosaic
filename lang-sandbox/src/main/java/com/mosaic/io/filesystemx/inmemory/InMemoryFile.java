package com.mosaic.io.filesystemx.inmemory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.WrappedBytes;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;


/**
 *
 */
public class InMemoryFile implements FileX {

    private InMemoryDirectory parentDirectory;
    private String            fileName;
    private Bytes             bytes;

    private boolean           hasBeenDeletedFlag;

    InMemoryFile( InMemoryDirectory parentDirectory, String fileName ) {
        this( parentDirectory, fileName, Bytes.wrap("") );
    }

    InMemoryFile( InMemoryDirectory parentDirectory, String fileName, Bytes bytes ) {
        QA.argNotNull( parentDirectory, "parentDirectory" );
        QA.argNotBlank( fileName, "fileName" );
        QA.argNotNull( bytes, "bytes" );

        this.parentDirectory = parentDirectory;
        this.fileName        = fileName;
        this.bytes           = bytes;

        bytes.setName(fileName);
    }

    public String getFileName() {
        throwIfDeleted();

        return fileName;
    }

    public void setBytes( Bytes newBytes ) {
        this.bytes = newBytes;

        newBytes.setName( fileName );
    }

    public Bytes loadBytesRO() {
        return loadBytes( FileModeEnum.READ_ONLY );
    }

    public Bytes loadBytesRW() {
        return loadBytes( FileModeEnum.READ_WRITE );
    }

    public Bytes loadBytes( FileModeEnum mode ) {
        throwIfDeleted();

        parentDirectory.incrementOpenFileCount();

        return new WrappedBytes(bytes) {
            public void release() {
                if ( parentDirectory == null ) { // already deleted
                    return;
                }

                parentDirectory.decrementOpenFileCount();
            }
        };
    }

    public Bytes loadBytes( FileModeEnum mode, int sizeInBytes ) {
        bytes.resize( sizeInBytes );

        return loadBytes( mode );
    }

    public long sizeInBytes() {
        return bytes == null ? 0 : bytes.bufferLength();
    }

    public String getFullPath() {
        throwIfDeleted();

        return parentDirectory + "/" + fileName;
    }

    public void delete() {
        throwIfDeleted();

        bytes.release();
        parentDirectory.notificationThatChildFileHasBeenDeleted( this );

        this.hasBeenDeletedFlag = true;
        this.bytes              = null;
        this.parentDirectory    = null;
    }

    public String toString() {
        return hasBeenDeletedFlag ? "DELETED" : getFullPath();
    }

    private void throwIfDeleted() {
        if ( hasBeenDeletedFlag ) {
            throw new IllegalStateException( this.fileName + " has been deleted" );
        }
    }
}
