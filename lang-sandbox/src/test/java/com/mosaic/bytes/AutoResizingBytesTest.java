package com.mosaic.bytes;

import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;


public class AutoResizingBytesTest extends BaseBytesTest {
    private DebugSystem system = new DebugSystem();

    protected Bytes _createBytes( long numBytes ) throws IOException {
        return new AutoResizingBytes( system, new ArrayBytes(numBytes), "junit", 1024 );
    }


    @Test
    public void resizeOnWrite() {
        Bytes bytes = createBytes( 5 );

        assertEquals( 5, bytes.sizeBytes() );

        bytes.writeInt( 30, 40, 42 );

        assertEquals( 40, bytes.sizeBytes() );

        system.assertDevAuditContains( "ResizableBytes 'junit' has been resized from 5 to 40 bytes" );
        system.assertNoWarnings();
    }

    @Test
    public void resizeOnWriteToLargerThanDesignExpectation_expectWarning() {
        Bytes bytes = createBytes( 5 );

        assertEquals( 5, bytes.sizeBytes() );

        bytes.writeInt( 30, 2000, 42 );

        assertEquals( 2000, bytes.sizeBytes() );


        system.assertDevAuditContains( "ResizableBytes 'junit' has been resized from 5 to 2000 bytes" );
        system.assertWarnContains( "ResizableBytes 'junit' has grown beyond the max expected size of '1024' bytes to '2000'" );
    }

}