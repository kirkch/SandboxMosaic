package com.mosaic.io.bytes;

import com.mosaic.lang.SystemX;
import com.mosaic.lang.reflect.ReflectionUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 *
 */
public class NativeBytesTest extends BaseBytesTest {

    protected Bytes doCreateBytes( long numBytes ) {
        return NativeBytes.allocOffHeap( numBytes );
    }


    @Test
    public void allocateABufferAndExpectItToStartOnACacheLineBoundary() {
        NativeBytes b = (NativeBytes) createBytes( 7 );

        assertTrue( "The base address must be > 0", b.getBaseAddress() > 0 );

        assertTrue(
            "The bytes have not been cache aligned (addr="+b.getBaseAddress()+", %cacheLineWidth="+(b.getBaseAddress() % SystemX.getCacheLineLengthBytes())+")",
            b.getBaseAddress() % SystemX.getCacheLineLengthBytes() == 0
        );

        long baseAddress    = ReflectionUtils.<Long>getPrivateField( b, "baseAddress" );
        long alignedAddress = ReflectionUtils.<Long>getPrivateField( b, "cacheAlignedBaseAddress" );
        long padding        = alignedAddress - baseAddress;

        assertTrue( "The initial padding is greater than the length of a cache line", padding < SystemX.getCacheLineLengthBytes() );
    }

}
