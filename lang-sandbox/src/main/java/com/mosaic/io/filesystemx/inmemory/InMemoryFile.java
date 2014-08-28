package com.mosaic.io.filesystemx.inmemory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.InputStreamAdapter;
import com.mosaic.io.bytes.WrappedBytes;
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

    private InMemoryDirectory parentDirectory;
    private String            fileName;
    private Bytes             bytes;

    private boolean           hasBeenDeletedFlag;

    private boolean isReadable   = true;
    private boolean isWritable   = true;
    private boolean isExecutable = false;


    InMemoryFile( InMemoryDirectory parentDirectory, String fileName ) {
        this( parentDirectory, fileName, Bytes.wrap( "" ) );
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

    public Map<String, String> loadProperties() {
        Bytes b = loadBytes( FileModeEnum.READ_ONLY );

        Properties props = new Properties();
        try {
            props.load( new InputStreamAdapter(b) );
        } catch ( IOException e ) {
            Backdoor.throwException( e );
        } finally {
            b.release();
        }

        return PropertyUtils.processProperties( props );
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

    private boolean isLocked;

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

    public String toString() {
        return hasBeenDeletedFlag ? "DELETED" : getFullPath();
    }

    private void throwIfDeleted() {
        if ( hasBeenDeletedFlag ) {
            throw new IllegalStateException( this.fileName + " has been deleted" );
        }
    }
}
