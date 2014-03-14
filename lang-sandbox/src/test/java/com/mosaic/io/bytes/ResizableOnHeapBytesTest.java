package com.mosaic.io.bytes;

import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class ResizableOnHeapBytesTest extends BaseBytesTest {

    private DebugSystem system = new DebugSystem();


    protected Bytes doCreateBytes( long numBytes ) throws IOException {
        Bytes bytes = Bytes.allocAutoResizingOnHeap( system, numBytes, numBytes + 500 );

        bytes.setName( "testMem" );

        return bytes;
    }


// LOG MESSAGE TESTS
    @Test
    public void writeToBytesWithoutNeedingToResize_expectNoLogMessages() {
        Bytes b = createBytes( 4 );

        b.writeInteger( 42 );

        assertEquals( 42, b.readInteger(0) );

        system.assertNoMessages();
    }

    @Test
    public void writeToBytesTriggeringAResize_expectDebugMessage() {
        Bytes b = createBytes( 3 );

        b.writeInteger( 42 );

        assertEquals( 42, b.readInteger(0) );

        system.assertDebug( "ResizableBytes 'testMem' has been resized from 3 to 6 bytes" );
    }
}
