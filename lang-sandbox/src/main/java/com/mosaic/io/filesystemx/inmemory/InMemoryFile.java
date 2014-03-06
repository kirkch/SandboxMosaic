package com.mosaic.io.filesystemx.inmemory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.WrappedBytes;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.Validate;


/**
 *
 */
public class InMemoryFile implements FileX {

    private InMemoryDirectory parentDirectory;
    private String            fileName;
    private Bytes             bytes;

    private boolean           hasBeenDeletedFlag;

    InMemoryFile( InMemoryDirectory parentDirectory, String fileName, Bytes bytes ) {
        Validate.argNotNull( parentDirectory, "parentDirectory" );
        Validate.argNotBlank( fileName, "fileName" );
        Validate.argNotNull( bytes, "bytes" );

        this.parentDirectory = parentDirectory;
        this.fileName        = fileName;
        this.bytes           = bytes;
    }

    public String getFileName() {
        throwIfDeleted();

        return fileName;
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
                throwIfReleased();

                super.release();

                parentDirectory.decrementOpenFileCount();
            }
        };
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
