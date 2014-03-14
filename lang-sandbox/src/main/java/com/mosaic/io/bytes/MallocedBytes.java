package com.mosaic.io.bytes;

import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;


/**
 *
 */
public class MallocedBytes extends NativeBytes {


    /**
     * Reserves n bytes of memory.  The bytes are not guaranteed to be zero'd out,
     * so they may hold junk in them.
     */
    public static Bytes alloc( long numBytes ) {
        return alloc( numBytes, SystemX.getCacheLineLengthBytes() );
    }

    /**
     * Reserves n bytes of memory.  The bytes are not guaranteed to be zero'd out,
     * so they may hold junk in them.
     */
    public static Bytes alloc( long numBytes, int cpuCacheLineSizeBytes ) {
        QA.argIsGTZero( numBytes, "numBytes" );

        long baseAddress    = Backdoor.alloc( numBytes + cpuCacheLineSizeBytes );
        long alignedAddress = Backdoor.alignAddress( baseAddress, cpuCacheLineSizeBytes );

        return new MallocedBytes( baseAddress, alignedAddress, alignedAddress+numBytes );
    }


    private long releaseAddress;


    protected MallocedBytes( long baseAddress, long alignedAddress, long maxAddressExc ) {
        super( baseAddress, alignedAddress, maxAddressExc );

        this.releaseAddress = baseAddress;
    }


    public void release() {
        super.release();

        Backdoor.free( releaseAddress );
    }

    public void resize( long newLength ) {
        QA.argIsGTZero( newLength, "newLength" );

        long newBaseAddress    = Backdoor.alloc( newLength + SystemX.getCacheLineLengthBytes() );
        long newAlignedAddress = Backdoor.alignAddress( newBaseAddress, SystemX.getCacheLineLengthBytes() );


        Backdoor.copyBytes( getBaseAddress(), newAlignedAddress, Math.min(newLength,this.bufferLength()) );

        Backdoor.free( releaseAddress );

        this.releaseAddress = newBaseAddress;

        super.resized( newBaseAddress, newAlignedAddress, newAlignedAddress+newLength );
    }
}
