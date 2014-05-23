package com.mosaic.io.filesystemx.classpath;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.InputBytes;
import com.mosaic.io.bytes.InputBytesAdapter;
import com.mosaic.io.bytes.InputStreamAdapter;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.io.filesystemx.FileX;
import com.mosaic.lang.NotFoundException;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.utils.PropertyUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;


/**
 *
 */
public class ClassPathFileX implements FileX {

    private String fullPath;

    public ClassPathFileX( String fullPath ) {
        this.fullPath = fullPath;
    }

    public String getFileName() {
        int i = fullPath.lastIndexOf( '/' );

        if ( i >= 0 ) {
            return fullPath.substring( i+1 );
        } else {
            return fullPath;
        }
    }

    public String getFullPath() {
        return fullPath;
    }

    public Bytes loadBytes( FileModeEnum mode ) {
        if ( mode.isWritable() || !mode.isReadable() ) {
            throw new UnsupportedOperationException( "not implemented, use loadBytesRO" );
        }

        return new InputBytesAdapter( loadBytesRO() );
    }

    public Bytes loadBytes( FileModeEnum mode, int sizeInBytes ) {
        throw new UnsupportedOperationException( "not implemented, use loadBytesRO" );
    }

    public void delete() {
        throw new UnsupportedOperationException( "cannot delete files from the classpath" );
    }

    public InputBytes loadBytesRO() {
        return lazyLoad();
    }

    public InputBytes loadBytesRW() {
        throw new UnsupportedOperationException( "cannot modify files from the classpath" );
    }

    public long sizeInBytes() {
        lazyLoad();


        return lazyLoad().bufferLength();
    }

    public Map<String, String> loadProperties() {
        InputBytes b = loadBytesRO();

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

    public void isReadable( boolean isReadable ) {
        throw new UnsupportedOperationException();
    }

    public boolean isReadable() {
        return true;
    }

    public void isWritable( boolean isWritable ) {
        throw new UnsupportedOperationException();
    }

    public boolean isWritable() {
        return false;
    }

    public void isExecutable( boolean isExecutable ) {
        throw new UnsupportedOperationException();
    }

    public boolean isExecutable() {
        return false;
    }


    private InputBytes bytes;

    private InputBytes lazyLoad() {
        if ( bytes == null ) {
            try {
                bytes = Bytes.loadFromClassPath( this.getClass().getClassLoader(), fullPath );

                if ( bytes == null ) {
                    throw new NotFoundException( String.format("unable to find '%s' on the classpath", fullPath) );
                }
            } catch ( IOException e ) {
                return Backdoor.throwException( e );
            }
        }

        return bytes;
    }
}
