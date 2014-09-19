package com.mosaic.bytes;

import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;


/**
 *
 */
public class OffHeapBytes extends NativeBytes {

    public OffHeapBytes( long numBytes ) {
        this( Backdoor.alloc(numBytes), numBytes );
    }

    private OffHeapBytes( long base, long length ) {
        super( base, base+length);
    }

    public void release() {
        super.release();

        Backdoor.free( base );
    }

    public void resize( long newLength ) {
        QA.argIsGTZero( newLength, "newLength" );


        long newBaseAddress = Backdoor.alloc( newLength );


        Backdoor.copyBytes( base, newBaseAddress, Math.min(newLength,sizeBytes()) );
        Backdoor.free( base );

        this.base   = newBaseAddress;
        this.maxExc = newBaseAddress+newLength;
    }

}
