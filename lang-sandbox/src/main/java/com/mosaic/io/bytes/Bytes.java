package com.mosaic.io.bytes;

import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 *
 */
public abstract class Bytes implements OutputBytes, InputBytes {

//    public static final Bytes EMPTY = wrap( "" );

    public static Bytes wrap( String str ) {
        return new ArrayBytes( str.getBytes(SystemX.UTF8) );
    }

    public static Bytes allocOffHeap( long numBytes ) {
        return MallocedBytes.alloc( numBytes );
    }

    public static Bytes allocOnHeap( long numBytes ) {
        return new ArrayBytes( numBytes );
    }

    public static Bytes memoryMapFile( File f, FileModeEnum mode ) {
        return MemoryMappedBytes.mapFile( f, mode, f.length() );
    }

    public static Bytes memoryMapFile( File f, FileModeEnum mode, long numBytes ) {
        return MemoryMappedBytes.mapFile( f, mode, numBytes );
    }

    public static Bytes allocAutoResizingOffHeap( SystemX system, long numBytes, long maxExpectedSize ) {
        return new ResizableBytes(system, MallocedBytes.alloc(numBytes), maxExpectedSize);
    }

    public static Bytes allocAutoResizingOnHeap( SystemX system, long numBytes, long maxExpectedSize ) {
        return new ResizableBytes(system, new ArrayBytes(numBytes), maxExpectedSize);
    }

    public static Bytes memoryAutoResizingMapFile( SystemX system, File f, FileModeEnum mode, long numBytes, long maxExpectedSize ) {
        return new ResizableBytes(system, MemoryMappedBytes.mapFile(f, mode, numBytes), maxExpectedSize);
    }


    /**
     * Loads the resource into an on-heap byte[].
     *
     * @return null if the resource was not found
     */
    public static InputBytes loadFromClassPath( ClassLoader classLoader, String resourcePath ) throws IOException {
        InputStream in = classLoader.getResourceAsStream( resourcePath );
        if ( in == null ) {
            return null;
        }

        BufferedInputStream bufferedInput = new BufferedInputStream( in );

        int    numBytes = countRemainingBytes( bufferedInput );
        byte[] bytes    = new byte[numBytes];

        bufferedInput.read( bytes );

        return new ArrayBytes( bytes );
    }

    private static int countRemainingBytes( BufferedInputStream in ) throws IOException {
        in.mark( Integer.MAX_VALUE );

        long lastSkipCount;
        long count = 0;
        do {
            lastSkipCount = in.skip( 1024 );

            count += lastSkipCount;
        } while ( lastSkipCount == 1024);

        QA.isInt( count, "input stream is too large for an int" );

        in.reset();

        return (int) count;
    }

}
