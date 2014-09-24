package com.mosaic.io.filesystemx.inmemory;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.WrappedBytes;
import com.mosaic.io.filesystemx.FileContents;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;


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


    private Object attachmentNbl;


    InMemoryFile( InMemoryFileSystem fileSystem, InMemoryDirectory parentDirectory, String fileName, int size ) {
        this( fileSystem, parentDirectory, fileName, new ArrayBytes(size) );
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
    }

    public String getFileName() {
        throwIfDeleted();

        return fileName;
    }

    public FileContents openFile( FileModeEnum mode ) {
        throwIfDeleted();

        fileSystem.incrementOpenFileCount();

        return new InMemoryFileContents(
            new WrappedBytes(bytes) {
                public void release() {
                    fileSystem.decrementOpenFileCount();
                }
            }
        );
    }

    public FileContents openFile( FileModeEnum mode, long sizeInBytes ) {
        bytes.resize( sizeInBytes );

        return openFile( mode );
    }

    public long sizeInBytes() {
        return bytes == null ? 0 : bytes.sizeBytes();
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


    /**
     * Fetch this files attachment.  An attachment is only supported by InMemoryFile, and is used
     * by inmemory fakes to share data as though it was persisted in a file but without having to
     * serialize/deserialize.  This is useful when the serialized form is reasonably complex, such
     * as when mocking Chronicle Queue and Map.
     */
    public Object getAttachmentNbl() {
        return attachmentNbl;
    }

    public void setAttachmentNbl( Object attachmentNbl ) {
        this.attachmentNbl = attachmentNbl;
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
