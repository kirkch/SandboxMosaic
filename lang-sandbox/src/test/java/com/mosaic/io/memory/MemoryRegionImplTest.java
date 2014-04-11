package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8;
import com.mosaic.lang.text.UTF8Tools;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 *
 */
public class MemoryRegionImplTest {

    private MemoryRegion region = MemoryRegionImpl.allocOnHeap( 100 );

// MALLOC/RETAIN


    @Test
    public void mallocMemory_getRetainCount_expectOne() {
        int recordAddress = region.malloc( 8 );

        assertEquals( 1, region.getCurrentRetainCountFor(recordAddress) );
    }

// AS BYTES

    @Test
    public void asBytes() {
        String text = "hello";

        int recordAddress = region.malloc( UTF8Tools.countBytesFor("hello")+2 );

        region.writeUTF8String( recordAddress, 0, "hello" );

        Bytes bytes = region.asBytes( recordAddress );
        UTF8 utf8 = new UTF8( bytes, 2, bytes.bufferLength() );

        assertEquals( text, utf8.toString() );
    }

// BOOLEAN

    @Test
    public void writeReadBoolean() {
        int recordAddress = region.malloc( 8 );

        region.writeBoolean( recordAddress, 1, true );

        assertEquals( false, region.readBoolean(recordAddress,0) );
        assertEquals( true, region.readBoolean(recordAddress,1) );
        assertEquals( 0, region.readByte(recordAddress,0) );
        assertEquals( 0, region.readByte(recordAddress,2) );
    }

// BYTE

    @Test
    public void writeReadByte() {
        int recordAddress = region.malloc( 8 );

        region.writeByte( recordAddress, 1, Byte.MIN_VALUE );

        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( Byte.MIN_VALUE, region.readByte( recordAddress, 1 ) );
        assertEquals( 0, region.readByte(recordAddress,0) );
        assertEquals( 0, region.readByte(recordAddress,2) );
    }

// CHAR

    @Test
    public void writeReadCharacter() {
        int recordAddress = region.malloc( 8 );

        region.writeCharacter( recordAddress, 1, Character.MAX_VALUE );

        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( Character.MAX_VALUE, region.readCharacter( recordAddress, 1 ) );
        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( 0, region.readByte( recordAddress, 3 ) );
    }

// UTF8 CHAR

    @Test
    public void writeReadUTF8Character() {
        int recordAddress = region.malloc( 8 );

        region.writeUTF8Character( recordAddress, 1, Character.MAX_VALUE );

        assertEquals( 0, region.readByte( recordAddress, 0 ) );

        DecodedCharacter buf = new DecodedCharacter();
        region.readUTF8Character( recordAddress, 1, buf );

        assertEquals( Character.MAX_VALUE, buf.c );
        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( 0, region.readByte( recordAddress, 4 ) );
    }

// SHORT

    @Test
    public void writeReadShort() {
        int recordAddress = region.malloc( 8 );

        region.writeShort( recordAddress, 1, Short.MIN_VALUE );

        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( Short.MIN_VALUE, region.readShort( recordAddress, 1 ) );
        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( 0, region.readByte( recordAddress, 3 ) );
    }

// INTEGER

    @Test
    public void writeReadInt() {
        int recordAddress = region.malloc( 8 );

        region.writeInt( recordAddress, 0, Integer.MAX_VALUE );
        assertEquals( Integer.MAX_VALUE, region.readInt(recordAddress,0) );
        assertEquals( 0, region.readInt(recordAddress,4) );
    }

    @Test
    public void writeReadInt_offsetByOne() {
        int recordAddress = region.malloc( 8 );

        region.writeInt( recordAddress, 1, Integer.MAX_VALUE );
        assertEquals( Integer.MAX_VALUE, region.readInt( recordAddress, 1 ) );
        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( 0, region.readByte( recordAddress, 5 ) );
    }

    @Test
    public void tryToWriteBeforeRecordBoundary_expectException() {
        if ( SystemX.isDebugRun() ) {
            int recordAddress = region.malloc( 8 );

            try {
                region.writeInt( recordAddress-1, 1, Integer.MAX_VALUE );
                fail( "Expected IllegalArgumentException" );
            } catch ( IllegalArgumentException ex ) {
                assertEquals( "Address 0x-1 undershot the memory regions index", ex.getMessage() );
            }
        }
    }

    @Test
    public void tryToWriteAfterRecordBoundary_expectException() {
        if ( SystemX.isDebugRun() ) {
            int recordAddress = region.malloc( 8 );

            try {
                region.writeInt( recordAddress, 5, Integer.MAX_VALUE );
                fail( "Expected IllegalArgumentException" );
            } catch ( IllegalArgumentException ex ) {
                assertEquals( "Access has gone outside of the allocated record", ex.getMessage() );
            }
        }
    }

    @Test
    public void allocateMultipleRecords_writeReadIntToEach() {
        int recordAddress1 = region.malloc( 8 );
        int recordAddress2 = region.malloc( 4 );
        int recordAddress3 = region.malloc( 12 );

        region.writeInt( recordAddress1, 3, -1 );
        region.writeInt( recordAddress2, 0, 100 );
        region.writeInt( recordAddress3, 7, Integer.MAX_VALUE );

        assertEquals( -1, region.readInt(recordAddress1,3) );
        assertEquals( 100, region.readInt(recordAddress2,0) );
        assertEquals( Integer.MAX_VALUE, region.readInt(recordAddress3,7) );
    }

    @Test
    public void tryToWriteToInvalidRecordAddress_expectException() {
        if ( SystemX.isDebugRun() ) {
            region.malloc( 8 );

            int recordAddress2 = region.malloc( 4 );

            try {
                region.writeInt( recordAddress2+1, 5, Integer.MAX_VALUE );
                fail( "Expected IllegalArgumentException" );
            } catch ( IllegalArgumentException ex ) {
                assertEquals( "Address 0x2 is an invalid address", ex.getMessage() );
            }
        }
    }

    @Test
    public void tryToReadFromInvalidRecordAddress_expectException() {
        if ( SystemX.isDebugRun() ) {
            region.malloc( 8 );

            int recordAddress2 = region.malloc( 4 );

            try {
                region.readInt( recordAddress2 + 1, 5 );
                fail( "Expected IllegalArgumentException" );
            } catch ( IllegalArgumentException ex ) {
                assertEquals( "Address 0x2 is an invalid address", ex.getMessage() );
            }
        }
    }

    @Test
    public void tryToWriteToReleasedRecordAddress_expectException() {
        if ( SystemX.isDebugRun() ) {
            region.malloc( 8 );

            int recordAddress = region.malloc( 4 );

            region.free( recordAddress );

            try {
                region.writeInt( recordAddress, 0, 42 );
                fail( "Expected IllegalArgumentException" );
            } catch ( IllegalArgumentException ex ) {
                assertEquals( "Address 0x1 is an invalid address", ex.getMessage() );
            }
        }
    }

    @Test
    public void tryToReadFromReleasedRecordAddress_expectException() {
        if ( SystemX.isDebugRun() ) {
            region.malloc( 8 );

            int recordAddress = region.malloc( 4 );

            region.free( recordAddress );

            try {
                region.readInt( recordAddress, 0 );
                fail( "Expected IllegalArgumentException" );
            } catch ( IllegalArgumentException ex ) {
                assertEquals( "Address 0x1 is an invalid address", ex.getMessage() );
            }
        }
    }

// LONG

    @Test
    public void writeReadLong() {
        int recordAddress = region.malloc( 10 );

        region.writeLong( recordAddress, 1, Long.MIN_VALUE );

        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( Long.MIN_VALUE, region.readLong( recordAddress, 1 ) );
        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( 0, region.readByte( recordAddress, 9 ) );
    }

// FLOAT

    @Test
    public void writeReadFloat() {
        int recordAddress = region.malloc( 10 );

        region.writeFloat( recordAddress, 1, Float.MIN_VALUE );

        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( Float.MIN_VALUE, region.readFloat(recordAddress, 1), 0.00001 );
        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( 0, region.readByte( recordAddress, 5 ) );
    }

// DOUBLE

    @Test
    public void writeReadDouble() {
        int recordAddress = region.malloc( 10 );

        region.writeDouble( recordAddress, 1, Double.MIN_VALUE );

        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( Double.MIN_VALUE, region.readDouble(recordAddress, 1), 0.00001 );
        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( 0, region.readByte( recordAddress, 9 ) );
    }

// UNSIGNED BYTE

    @Test
    public void writeReadUnsignedByte() {
        int recordAddress = region.malloc( 10 );

        region.writeUnsignedByte( recordAddress, 1, (byte) 0xFF );

        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( 0xFF, region.readUnsignedByte(recordAddress, 1) );
        assertEquals( 0, region.readByte( recordAddress, 2 ) );
    }

// UNSIGNED SHORT

    @Test
    public void writeReadUnsignedShort() {
        int recordAddress = region.malloc( 10 );

        region.writeUnsignedShort( recordAddress, 1, (byte) 0xFFFF );

        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( 0xFFFF, region.readUnsignedShort(recordAddress, 1) );
        assertEquals( 0xFF, region.readUnsignedByte( recordAddress, 1 ) );
        assertEquals( 0xFF, region.readUnsignedByte( recordAddress, 2 ) );
        assertEquals( 0, region.readByte( recordAddress, 3 ) );
    }

// UNSIGNED INT

    @Test
    public void writeReadUnsignedInt() {
        int recordAddress = region.malloc( 10 );

        region.writeUnsignedInt( recordAddress, 1, (byte) 0xFFFFFFFF );

        assertEquals( 0, region.readByte( recordAddress, 0 ) );
        assertEquals( 0xFFFFFFFF, region.readUnsignedInt(recordAddress, 1) );
        assertEquals( 0xFF, region.readUnsignedByte( recordAddress, 1 ) );
        assertEquals( 0xFF, region.readUnsignedByte( recordAddress, 2 ) );
        assertEquals( 0xFF, region.readUnsignedByte( recordAddress, 3 ) );
        assertEquals( 0xFF, region.readUnsignedByte( recordAddress, 4 ) );
        assertEquals( 0, region.readByte( recordAddress, 5 ) );
    }

// UTF8 STRING

    @Test
    public void writeReadUTF8String() {
        int recordAddress = region.malloc( 10 );

        region.writeUTF8String( recordAddress, 1, "abc" );

        assertEquals( 0, region.readByte( recordAddress, 0 ) );

        StringBuilder buf = new StringBuilder();
        region.readUTF8String(recordAddress, 1, buf);

        assertEquals( "abc", buf.toString() );
    }

}
