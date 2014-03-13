package com.mosaic.io.filesystemx;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.bytes.InputBytes;

import java.util.Comparator;


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
     * Be sure to release() the bytes when done.
     *
     * Measurements:
     *   Jan 2014
     *   on an SSD, mapped 1000 files (20-400kb in sizeInBytes - totalled 200MB) and
     *   counted the number of lines in them.  It took 93.4s using
     *   java.lang.InputStream and 1.7s using this.
     */
    public Bytes loadBytes( FileModeEnum mode );

    public String getFullPath();

    public void delete();

    public InputBytes loadBytesRO();
    public InputBytes loadBytesRW();


    public long sizeInBytes();
}