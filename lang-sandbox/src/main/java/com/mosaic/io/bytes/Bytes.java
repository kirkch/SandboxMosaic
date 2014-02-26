package com.mosaic.io.bytes;

import com.mosaic.io.FileModeEnum;
import com.mosaic.lang.SystemX;

import java.io.File;
import java.io.IOException;


/**
 *
 */
public abstract class Bytes implements OutputBytes, InputBytes {

    public static Bytes wrap( String str ) {
        return new ArrayBytes( str.getBytes(SystemX.UTF8) );
    }

    public static Bytes alloc( long numBytes ) {
        return MallocedBytes.alloc( numBytes );
    }

    public static Bytes memoryMapFile( File f, FileModeEnum mode ) throws IOException {
        return MemoryMappedBytes.mapFile( f, mode, f.length() );
    }

    public static Bytes memoryMapFile( File f, FileModeEnum mode, long numBytes ) throws IOException {
        return MemoryMappedBytes.mapFile( f, mode, numBytes );
    }

}
