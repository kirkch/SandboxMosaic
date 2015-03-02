package com.mosaic.io.filesystemx.inmemory;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes.Bytes;
import com.mosaic.bytes2.Bytes2;
import com.mosaic.bytes2.impl.ArrayBytes2;
import com.mosaic.bytes2.impl.Bytes1ToBytes2Adapter;
import com.mosaic.io.filesystemx.FileContents;
import com.mosaic.io.filesystemx.FileContents2;
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
    private Bytes2             bytes2;

    private boolean            hasBeenDeletedFlag;

    private boolean isReadable   = true;
    private boolean isWritable   = true;
    private boolean isExecutable = false;


    private Object attachmentNbl;


    InMemoryFile( InMemoryFileSystem fileSystem, InMemoryDirectory parentDirectory, String fileName, int size ) {
        this( fileSystem, parentDirectory, fileName, new byte[size] );
    }

    private InMemoryFile( InMemoryFileSystem fileSystem, InMemoryDirectory parentDirectory, String fileName, byte[] bytes ) {
        this( fileSystem, parentDirectory, fileName, new ArrayBytes(bytes), new ArrayBytes2(bytes) );
    }

    private InMemoryFile( InMemoryFileSystem fileSystem, InMemoryDirectory parentDirectory, String fileName, Bytes bytes, Bytes2 bytes2 ) {
        QA.argNotNull( fileSystem, "fileSystem" );
        QA.argNotNull( parentDirectory, "parentDirectory" );
        QA.argNotBlank( fileName, "fileName" );
        QA.argNotNull( bytes, "bytes" );

        this.fileSystem      = fileSystem;
        this.parentDirectory = parentDirectory;
        this.fileName        = fileName;
        this.bytes           = bytes;
        this.bytes2          = bytes2;
    }

    public String getFileName() {
        throwIfDeleted();

        return fileName;
    }

    public FileContents openFile( FileModeEnum mode ) {
        throwIfDeleted();

        fileSystem.incrementOpenFileCount();

        return new InMemoryFileContents(bytes);
    }

    public FileContents openFile( FileModeEnum mode, long sizeInBytes ) {
        bytes.resize( sizeInBytes );
        bytes2.resize( sizeInBytes );

        return openFile( mode );
    }



    public FileContents2 openFile2( FileModeEnum mode ) {
        throwIfDeleted();

        fileSystem.incrementOpenFileCount();

        return new InMemoryFileContents2(bytes2);
    }

    public FileContents2 openFile2( FileModeEnum mode, long sizeInBytes ) {
        throwIfDeleted();

        bytes.resize( sizeInBytes );
        bytes2.resize( sizeInBytes );

        fileSystem.incrementOpenFileCount();

        return new InMemoryFileContents2(bytes2);
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

        bytes2.release();
        parentDirectory.notificationThatChildFileHasBeenDeleted( this );

        this.bytes              = null;
        this.bytes2             = null;
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
        this.bytes  = newBytes;
        this.bytes2 = new Bytes1ToBytes2Adapter( newBytes );
    }

    private boolean isLocked;


    private class InMemoryFileContents extends FileContents {
        public InMemoryFileContents( Bytes delegate ) {
            super( delegate );
        }

        public void release() {
            fileSystem.decrementOpenFileCount();
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

    private class InMemoryFileContents2 extends FileContents2 {
        public InMemoryFileContents2( Bytes2 delegate ) {
            super( delegate );
        }

        public void release() {
            fileSystem.decrementOpenFileCount();
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
