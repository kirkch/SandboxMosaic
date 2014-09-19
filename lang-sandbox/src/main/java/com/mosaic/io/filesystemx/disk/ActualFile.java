package com.mosaic.io.filesystemx.disk;

import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.MemoryMappedBytes;
import com.mosaic.bytes.WrappedBytes;
import com.mosaic.io.filesystemx.FileContents;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;


/**
 *
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
public class ActualFile implements FileX {

    private ActualFileSystem fileSystem;
    private File             file;


    ActualFile( ActualFileSystem fileSystem, File file ) {
        QA.argNotNull( fileSystem, "fileSystem" );
        QA.argNotNull( file, "file" );

        this.fileSystem = fileSystem;
        this.file       = file;

        mkdirs();
    }

    public String getFileName() {
        return file.getName();
    }

    public FileContents openFile( FileModeEnum mode ) {
        Bytes bytes = MemoryMappedBytes.mapFile( file, mode );

        return new ActualFileContents( wrapMemoryMappedBytes(bytes) );
    }

    public FileContents openFile( FileModeEnum mode, int sizeInBytes ) {
        Bytes bytes = MemoryMappedBytes.mapFile( file, mode, sizeInBytes );

        return new ActualFileContents( wrapMemoryMappedBytes(bytes) );
    }

    public String getFullPath() {
        return file.getAbsolutePath();
    }

    public void delete() {
        if ( file != null ) {
            this.file.delete();
        }

        this.file = null;
    }

    public long sizeInBytes() {
        return file.length();
    }


    public void isReadable( boolean isReadable ) {
        file.setReadable( isReadable );
    }

    public boolean isReadable() {
        return file.canRead();
    }

    public void isWritable( boolean isWritable ) {
        file.setWritable( isWritable );
    }

    public boolean isWritable() {
        return file.canWrite();
    }

    public void isExecutable( boolean isExecutable ) {
        file.setExecutable( isExecutable );
    }

    public boolean isExecutable() {
        return file.canExecute();
    }

    public String toString() {
        return file.toString();
    }



    private Bytes wrapMemoryMappedBytes( final Bytes bytes ) {
        fileSystem.incrementOpenFileCount();

        return new WrappedBytes(bytes) {
            public void release() {
                fileSystem.decrementOpenFileCount();
            }
        };
    }

    private void mkdirs() {
        File parentFile = file.getParentFile();

        if ( parentFile != null ) {
            parentFile.mkdirs();
        }
    }



    private class ActualFileContents extends FileContents {
        private FileLock fileLock;

        public ActualFileContents( Bytes delegate ) {
            super( delegate );
        }

        public boolean lockFile() {
            if ( isLocked() ) {
                return false;
            }

            try {
                RandomAccessFile raf = new RandomAccessFile( file, "rw" );

                this.fileLock = raf.getChannel().tryLock();

                return isLocked();
            } catch ( IOException e ) {
                Backdoor.throwException( e );
                return false;
            }
        }

        public boolean isLocked() {
            return fileLock != null && fileLock.isValid();
        }

        public boolean unlockFile() {
            if ( !isLocked() ) {
                return false;
            }

            try {
                this.fileLock.release();

                QA.isFalse( isLocked(), "requested release of file lock but it remains" );

                this.fileLock = null;

                return true;
            } catch ( IOException e ) {
                Backdoor.throwException( e );
                return false;
            }
        }
    }

}
