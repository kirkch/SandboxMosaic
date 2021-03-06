package com.mosaic.bytes2.impl;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.io.RuntimeIOException;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 *
 */
public class MemoryMappedBytes2 extends NativeBytes2 {

    public static Bytes2 mapFile( File f, FileModeEnum mode ) {
        long size = Math.max( f.length(), 1 );  // if the file does not exist, use a default size.. 0  was not suitable for native buffers

        return mapFile( f, mode, size );
    }

    public static Bytes2 mapFile( File f, FileModeEnum mode, long numBytes ) {
        QA.argIsGTZero( numBytes, "numBytes" );

        try {
            RandomAccessFile raf = new RandomAccessFile( f, mode.toString() );
            FileChannel channel = raf.getChannel();
            MappedByteBuffer buf = channel.map( mode.toMemoryMapMode(), 0, numBytes );

            DirectBuffer db = (DirectBuffer) buf;
            long addr = db.address();

            return new MemoryMappedBytes2( raf, mode, db, addr, addr + buf.capacity() );
        } catch ( FileNotFoundException ex ) {
            throw new RuntimeIOException( "File not found, and cannot create it on a RO request.  Change the call to RW and try again. " + f );
        } catch ( IOException ex ) {
            throw RuntimeIOException.recast( ex );
        }
    }


    private RandomAccessFile raf;
    private FileModeEnum     mode;
    private DirectBuffer     buf;


    private MemoryMappedBytes2( RandomAccessFile raf, FileModeEnum mode, DirectBuffer buf, long baseAddress, long maxAddressExc ) {
        super( baseAddress, maxAddressExc );

        this.raf  = raf;
        this.mode = mode;
        this.buf  = buf;
    }


    public void release() {
        super.release();

        try {
            this.raf.close();
        } catch ( IOException ex ) {
            Backdoor.throwException( ex );
        } finally {
            Cleaner cleaner = this.buf.cleaner();
            if ( cleaner != null ) {
                cleaner.clean();
            }

            this.raf = null;
            this.buf = null;
        }
    }

    public void resize( long newLength ) {
        QA.argIsGTZero( newLength, "newLength" );

        try {
            FileChannel      channel         = raf.getChannel();
            MappedByteBuffer newBuf          = channel.map( mode.toMemoryMapMode(), 0, newLength );

            DirectBuffer     newDirectBuffer = (DirectBuffer) newBuf;
            long             newAddress      = newDirectBuffer.address();

            Cleaner cleaner = this.buf.cleaner();
            if ( cleaner != null ) {
                cleaner.clean();
            }

            this.buf    = newDirectBuffer;
            this.base   = newAddress;
            this.maxExc = newAddress+newLength;
        } catch ( IOException ex ) {
            throw RuntimeIOException.recast( ex );
        }
    }

    public void flush() {
        ((MappedByteBuffer) buf).force();
    }
}
