package com.mosaic.io.filesystemx.disk;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.WrappedBytes;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.utils.PropertyUtils;
import com.mosaic.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    ActualFile( ActualDirectory parentDirectory, File file ) {
        QA.argNotNull( file, "file" );
        QA.argNotNull( parentDirectory, "parentDirectory" );

        this.file            = file;
        this.parentDirectory = parentDirectory;
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

    private Properties loadPropertiesFromFileSystem() {
        Properties props = new Properties();

        try {
            props.load( new FileInputStream(file) );
        } catch ( IOException e ) {
            Backdoor.throwException( e );
        }

        return props;
    }

}
