package com.mosaic.io.bytes;

import com.mosaic.io.RuntimeIOException;
import com.mosaic.io.filesystemx.FileModeEnum;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.QA;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 *
 */
public class MemoryMappedBytes extends NativeBytes {

    public static Bytes mapFile( File f, FileModeEnum mode, long numBytes ) {
        QA.argIsGTZero( numBytes, "numBytes" );

        try {
            RandomAccessFile raf     = new RandomAccessFile( f, mode.toString() );
            FileChannel      channel = raf.getChannel();

            MappedByteBuffer buf  = channel.map( mode.toMemoryMapMode(), 0, numBytes );

            DirectBuffer     db   = (DirectBuffer) buf;
            long             addr = db.address();

            return new MemoryMappedBytes( raf, db, addr, addr, addr+buf.capacity() );
        } catch ( IOException ex ) {
            throw RuntimeIOException.recast( ex );
        }
    }


    private RandomAccessFile raf;
    private DirectBuffer     buf;

    private MemoryMappedBytes( RandomAccessFile raf, DirectBuffer buf, long baseAddress, long alignedAddress, long maxAddressExc ) {
        super( baseAddress, alignedAddress, maxAddressExc );

        this.raf = raf;
        this.buf = buf;
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

}
