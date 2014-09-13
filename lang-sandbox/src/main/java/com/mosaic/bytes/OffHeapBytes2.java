package com.mosaic.bytes;

import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;


/**
 *
 */
public class OffHeapBytes2 extends NativeBytes {

    public static Bytes2 alloc( long numBytes ) {
        long baseAddress = Backdoor.alloc( numBytes );

        return new OffHeapBytes2( baseAddress, baseAddress+numBytes );
    }


    private OffHeapBytes2( long base, long maxExc ) {
        super( base, maxExc );
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
