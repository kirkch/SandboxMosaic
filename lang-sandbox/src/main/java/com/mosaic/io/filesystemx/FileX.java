package com.mosaic.io.filesystemx;

import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.text.UTF8Tools;
import com.mosaic.utils.PropertyUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;


/**
 * An abstraction used for file access.  Designed to support unit testing.
 */
public interface FileX {

    public static final Comparator<FileX> PATH_COMPARATOR = new Comparator<FileX>() {
        public int compare( FileX f1, FileX f2 ) {
            return f1.getFullPath().compareTo( f2.getFullPath() );
        }
    };


    public String getFileName();

    /**
     * Open the file for reading and or writing. Under the hood it will memory map the
     * file, thus most of the loading will happen asynchronously by the operating
     * system.  This approach is generally at least 50 times faster than using
     * java.io.InputStream.
     *
     * Be sure to release() the bytes when done.
     *
     * Measurements:
     *   Jan 2014
     *   on an SSD, mapped 1000 files (20-400kb in sizeInBytes - totalled 200MB) and
     *   counted the number of lines in them.  It took 93.4s using
     *   java.lang.InputStream and 1.7s using this.
     */
    public FileContents openFile( FileModeEnum mode );
    public FileContents openFile( FileModeEnum mode, long sizeInBytes );

    public FileContents2 openFile2( FileModeEnum mode );
    public FileContents2 openFile2( FileModeEnum mode, long sizeInBytes );

    public default <T> T processFile( Function1<FileContents,T> action, FileModeEnum fileModeEnum ) {
        FileContents f = openFile( fileModeEnum );

        try {
            return action.invoke( f );
        } finally {
            f.release();
        }
    }

    public default <T> T ro( Function1<FileContents,T> action ) {
        return processFile( action, FileModeEnum.READ_ONLY );
    }

    public default <T> T rw( Function1<FileContents,T> action ) {
        return processFile( action, FileModeEnum.READ_WRITE );
    }

    public String getFullPath();

    public void delete();

    public long sizeInBytes();

    public void isReadable( boolean isReadable );
    public boolean isReadable();

    public void isWritable( boolean isWritable );
    public boolean isWritable();

    public void isExecutable( boolean isExecutable );
    public boolean isExecutable();


    public default String getFileNameExcludingTag() {
        String name = getFileName();
        int    i    = name.lastIndexOf('.');

        return i < 0 ? name : name.substring(0,i);
    }

    /**
     * Overwrites the file with the specified string.
     */
    public default void writeText( String s ) {
        rw( f -> {
            int byteLength = UTF8Tools.countBytesFor( s );

            f.resize( byteLength );
            f.writeUTF8StringUndemarcated( 0, byteLength, s );

            return null;
        });
    }

    /**
     * Load the file, parsing it as a file of key value pairs.
     */
    public default Map<String, String> loadProperties() {
        return ro( f -> {
            Properties props = new Properties();
            try {
                props.load( f.toInputStream() );
            } catch ( IOException e ) {
                Backdoor.throwException( e );
            }

            return PropertyUtils.processProperties( props );
        });
    }

}
