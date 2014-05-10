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

    public static Bytes allocAutoResizingOffHeap( String name, SystemX system, long numBytes, long maxExpectedSize ) {
        Bytes alloc = MallocedBytes.alloc( numBytes );

        return new ResizableBytes(name, system, alloc, maxExpectedSize);
    }

    public static Bytes allocAutoResizingOnHeap( String name, SystemX system, long numBytes, long maxExpectedSize ) {
        return new ResizableBytes(name, system, new ArrayBytes(numBytes), maxExpectedSize);
    }

    public static Bytes memoryAutoResizingMapFile( String name, SystemX system, File f, FileModeEnum mode, long numBytes, long maxExpectedSize ) {
        return new ResizableBytes(name, system, MemoryMappedBytes.mapFile(f, mode, numBytes), maxExpectedSize);
    }


    /**
     * Loads the resource into an on-heap byte[].
     *
     * @return null if the resource was not found
     */
    public static Bytes loadFromClassPath( ClassLoader classLoader, String resourcePath ) throws IOException {
        if ( resourcePath.startsWith("/") ) {
            resourcePath = resourcePath.substring(1);
        }

        InputStream in = classLoader.getResourceAsStream( resourcePath );
        if ( in == null ) {
            return null;
        }

        BufferedInputStream bufferedInput = new BufferedInputStream( in );

        int    numBytes = countRemainingBytes( bufferedInput );
        byte[] bytes    = new byte[numBytes];

        bufferedInput.read( bytes );

        ArrayBytes arrayBytes = new ArrayBytes( bytes );
        arrayBytes.setName( resourcePath );

        return arrayBytes;
    }

    public static InputBytes loadFromClassPath( String resourcePath ) throws IOException {
        return loadFromClassPath( Bytes.class.getClassLoader(), resourcePath );
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


    public abstract Bytes narrow( long fromInc, long toExc );




    public short incrementUnsignedByte( long index ) {
        short initialValue = readUnsignedByte( index );
        short updatedValue = (short) (initialValue + 1);

        QA.isLT( initialValue, 0xFF, "initialValue" );

        writeUnsignedByte( index, updatedValue );

        return updatedValue;
    }

    public long incrementUnsignedInt( long index ) {
        long initialValue = readUnsignedInt( index );
        long updatedValue = initialValue + 1;

        writeUnsignedInt( index, updatedValue );

        return updatedValue;
    }

    public int incrementInt( long index ) {
        int initialValue = readInt( index );
        int updatedValue = initialValue + 1;

        writeInt( index, updatedValue );

        return updatedValue;
    }

    public long incrementLong( long index ) {
        long initialValue = readLong( index );
        long updatedValue = initialValue + 1;

        writeLong( index, updatedValue );

        return updatedValue;
    }

}
