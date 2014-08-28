package com.mosaic.io.filesystemx.disk;

import com.mosaic.io.FileUtils;
import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.WrappedBytes;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.utils.PropertyUtils;
import com.mosaic.utils.StringUtils;
import sun.management.VMManagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileLock;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 *
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
public class ActualFile implements FileX {

    private File            file;
    private ActualDirectory parentDirectory;
    private FileLock        fileLock;

    ActualFile( ActualDirectory parentDirectory, File file ) {
        QA.argNotNull( file, "file" );
        QA.argNotNull( parentDirectory, "parentDirectory" );

        this.file            = file;
        this.parentDirectory = parentDirectory;

        mkdirs();
    }

    public String getFileName() {
        return file.getName();
    }

    public Bytes loadBytesRO() {
        return loadBytes( FileModeEnum.READ_ONLY );
    }

    public Bytes loadBytesRW() {
        return loadBytes( FileModeEnum.READ_WRITE );
    }

    public Bytes loadBytes( FileModeEnum mode ) {
        Bytes bytes = Bytes.memoryMapFile( file, mode );

        return wrapMemoryMappedBytes( bytes );
    }

    public Bytes loadBytes( FileModeEnum mode, int sizeInBytes ) {
        Bytes bytes = Bytes.memoryMapFile( file, mode, sizeInBytes );

        return wrapMemoryMappedBytes( bytes );
    }

    private Bytes wrapMemoryMappedBytes( final Bytes bytes ) {
        parentDirectory.incrementOpenFileCount();

        return new WrappedBytes(bytes) {
            @Override
            public void release() {
                super.release();

                parentDirectory.decrementOpenFileCount();
            }
        };
    }

    public String getFullPath() {
        return file.getAbsolutePath();
    }

    public void delete() {
        this.file.delete();

        this.file            = null;
        this.parentDirectory = null;
    }

    public long sizeInBytes() {
        return file.length();
    }

    public Map<String,String> loadProperties() {
        Properties props = loadPropertiesFromFileSystem();

        return PropertyUtils.processProperties( props );
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

    public boolean lockFile() {
        if ( isLocked() ) {
            return false;
        }

        try {
            RandomAccessFile raf = new RandomAccessFile( file, "rw" );

            this.fileLock = raf.getChannel().tryLock();

            VMManagement vmManagement = ReflectionUtils.getPrivateField( ManagementFactory.getRuntimeMXBean(), "jvm" );

            Integer pid = ReflectionUtils.invokePrivateMethod( vmManagement, "getProcessId" );

            raf.writeChars( pid.toString() );

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

    public String toString() {
        return file.toString();
    }

    private Properties loadPropertiesFromFileSystem() {
        Properties props = new Properties();

        try {
            props.load( new FileInputStream(file) );
        } catch ( IOException e ) {
            Backdoor.throwException( e );
        }

        return props;
    }

    private void mkdirs() {
        File parentFile = file.getParentFile();

        if ( parentFile != null ) {
            parentFile.mkdirs();
        }
    }

}
