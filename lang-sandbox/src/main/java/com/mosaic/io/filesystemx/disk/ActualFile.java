package com.mosaic.io.filesystemx.disk;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.WrappedBytes;
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

    private File            file;
    private ActualDirectory parentDirectoryNbl;


    ActualFile( ActualDirectory parentDirectory, File file ) {
        QA.argNotNull( file, "file" );
        QA.argNotNull( parentDirectory, "parentDirectory" );

        this.file               = file;
        this.parentDirectoryNbl = parentDirectory;

        mkdirs();
    }

    public String getFileName() {
        return file.getName();
    }

    public FileContents openFile( FileModeEnum mode ) {
        Bytes bytes = Bytes.memoryMapFile( file, mode );

        return new ActualFileContents( wrapMemoryMappedBytes(bytes) );
    }

    public FileContents openFile( FileModeEnum mode, int sizeInBytes ) {
        Bytes bytes = Bytes.memoryMapFile( file, mode, sizeInBytes );

        return new ActualFileContents( wrapMemoryMappedBytes(bytes) );
    }

    public String getFullPath() {
        return file.getAbsolutePath();
    }

    public void delete() {
        if ( file != null ) {
            this.file.delete();
        }

        this.file            = null;
        this.parentDirectoryNbl = null;
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
        parentDirectoryNbl.incrementOpenFileCount();

        return new WrappedBytes(bytes) {
            @Override
            public void release() {
                super.release();

                if ( parentDirectoryNbl != null ) { // will be null after being deleted
                    parentDirectoryNbl.decrementOpenFileCount();
                }
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
