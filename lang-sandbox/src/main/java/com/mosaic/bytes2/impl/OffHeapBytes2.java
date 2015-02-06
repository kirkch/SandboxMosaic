package com.mosaic.bytes2.impl;

import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;


/**
 *
 */
public class OffHeapBytes2 extends NativeBytes2 {

    public OffHeapBytes2( long numBytes ) {
        this( Backdoor.alloc(numBytes), numBytes );
    }

    private OffHeapBytes2( long base, long length ) {
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
