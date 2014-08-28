package com.mosaic.io.filesystemx;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.InputBytes;

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
     * Read the entire file in in one go.  Under the hood it will memory map the
     * file, thus most of the loading will happen asynchronously by the operating
     * system.  This approach is generally at least 50 times faster than using
     * java.io.InputStream.
     *
     * Be sure to free() the bytes when done.
     *
     * Measurements:
     *   Jan 2014
     *   on an SSD, mapped 1000 files (20-400kb in sizeInBytes - totalled 200MB) and
     *   counted the number of lines in them.  It took 93.4s using
     *   java.lang.InputStream and 1.7s using this.
     */
    public Bytes loadBytes( FileModeEnum mode );

    public Bytes loadBytes( FileModeEnum mode, int sizeInBytes );

    public String getFullPath();

    public void delete();

    public InputBytes loadBytesRO();
    public InputBytes loadBytesRW();


    public long sizeInBytes();

    /**
     * Load the file, parsing it as a file of key value pairs.
     */
    public Map<String,String> loadProperties();

    public void isReadable( boolean isReadable );
    public boolean isReadable();

    public void isWritable( boolean isWritable );
    public boolean isWritable();

    public void isExecutable( boolean isExecutable );
    public boolean isExecutable();

    /**
     * Takes out a file system level lock.  Depending on the underlying OS, the lock may only
     * be honored if the lock is explicitly checked;  So be sure to test the lock before using
     * the file.
     *
     * @return true if the lock has been acquired by this call
     */
    public boolean lockFile();

    /**
     * Returns true if this file is currently locked.  NB one must have invoked lockFile() first,
     * otherwise this method will always return true even if the file has been locked by another
     * JVM.  This is because of a limitation in the underlying API, which requires a lock attempt
     * before determining if the file is locked.
     */
    public boolean isLocked();

    /**
     * Releases the file lock.
     *
     * @return returns true if the lock was released by this call, otherwise false
     */
    public boolean unlockFile();


    public default String getFileNameExcludingTag() {
        String name = getFileName();
        int    i    = name.lastIndexOf('.');

        return i < 0 ? name : name.substring(0,i);
    }
}
