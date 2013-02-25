package com.mosaic.io;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public abstract class BaseBytesTestCases {

    protected abstract Bytes createBytes( byte[] bytes );
    
// factory tests

    @Test
    public void givenNullBytesBuffer_createBytesWrapper_expectNullPointerException() {
        try {
            createBytes( null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'src' must not be null", e.getMessage() );
        }
    }


// streamOffset tests

    @Test
    public void givenEmptyBytesBuffer_callStreamOffset_expectZero() {
        Bytes      bytes = createBytes( new byte[] {} );

        assertEquals( 0, bytes.streamOffset() );
    }

    @Test
    public void givenTwoBytesBuffer_callStreamOffset_expectZero() {
        Bytes      bytes = createBytes( new byte[] {97,98} );

        assertEquals( 0, bytes.streamOffset() );
    }

    @Test
    public void givenThreeBytes_consumeOneByte_expectStreamOffset1() {
        Bytes bytes1 = createBytes( new byte[] {96, 97, 98} );
        Bytes bytes2 = bytes1.consume( 1 );

        assertEquals(  1, bytes2.streamOffset() );
    }

    @Test
    public void givenThreeBytes_consumeTwoBytes_expectStreamOffset2() {
        Bytes bytes1 = createBytes( new byte[] {96, 97, 98} );
        Bytes bytes2 = bytes1.consume( 2 );

        assertEquals(  2, bytes2.streamOffset() );
    }

    @Test
    public void givenThreeBytes_consumeOneByteTwice_expectStreamOffset2() {
        Bytes bytes1 = createBytes( new byte[] {96,97,98} );
        Bytes bytes2 = bytes1.consume( 1 );
        Bytes bytes3 = bytes2.consume( 1 );

        assertEquals(  2, bytes3.streamOffset() );
    }

    @Test
    public void givenFiveBytesMadeViaAppendingTwoBuffers_consumeOverByteBufferBoundary_expectStreamOffset() {
        Bytes bytes1 = createBytes( new byte[] {96,97,98} );
        Bytes bytes2 = createBytes( new byte[] {99, 100} );
        Bytes bytes3 = bytes1.appendBytes( bytes2 );
        Bytes bytes4 = bytes3.consume(4);

        assertEquals(   4, bytes4.streamOffset() );
        assertEquals(   1, bytes4.length() );
        assertEquals( 100, bytes4.getByte( 0 ) );
    }

    @Test
    public void givenSixBytesMadeViaAppendingThreeBuffers_consumeTwiceUntilOverFirstTwoByteBufferBoundary_expectStreamOffset() {
        Bytes bytes1 = createBytes( new byte[] {96,97} );
        Bytes bytes2 = createBytes( new byte[] {98,99} );
        Bytes bytes3 = createBytes( new byte[] {100,101} );
        Bytes bytes4 = bytes1.appendBytes( bytes2 ).appendBytes( bytes3 );
        Bytes bytes5 = bytes4.consume(2).consume( 3 );

        assertEquals(   5, bytes5.streamOffset() );
        assertEquals(   1, bytes5.length() );
        assertEquals( 101, bytes5.getByte( 0 ) );
    }

    @Test
    public void givenBytesCreatedByConsumeAppendConsumeAppend_checkContentsIsCorrectAsInternalMarkersMustBeUpdated() {
        Bytes bytes1 = createBytes( new byte[] {96,97} );
        Bytes bytes2 = createBytes( new byte[] {98,99} );
        Bytes bytes3 = createBytes( new byte[] {100, 101} );
        Bytes bytes4 = bytes1.consume(1).appendBytes( bytes2 ).consume( 1 ).appendBytes( bytes3 );

        assertEquals(   2, bytes4.streamOffset() );
        assertEquals(   4, bytes4.length() );
        assertEquals(  98, bytes4.getByte( 0 ) );
        assertEquals(  99, bytes4.getByte( 1 ) );
        assertEquals( 100, bytes4.getByte( 2 ) );
        assertEquals( 101, bytes4.getByte( 3 ) );
    }

// length tests

    @Test
    public void givenEmptyBytesBuffer_callLength_expectZero() {
        Bytes bytes = createBytes( new byte[] {} );

        assertEquals( 0, bytes.length() );
    }

    @Test
    public void given3ByteBytesBuffer_callLength_expect3() {
        Bytes bytes = createBytes( new byte[] {97, 98, 99} );

        assertEquals( 3, bytes.length() );
    }

    @Test
    public void given3ByteBytesBuffer_callGetByteFollowedByLength_expectGetByteToNotHaveChangedTheLength() {
        Bytes bytes = createBytes( new byte[] {97, 98, 99} );

        bytes.getByte(0);

        assertEquals( 3, bytes.length() );
    }


// getByte tests

    @Test
    public void givenEmptyBuffer_getFirstByte_expectException() {
        Bytes bytes = createBytes( new byte[] {} );

        try {
            bytes.getByte( 0 );

            fail( "Expected IndexOutOfBoundsException" );
        } catch (IndexOutOfBoundsException e) {
            assertEquals( null, e.getMessage() );
        }
    }

    @Test
    public void givenNoneEmptyBuffer_getFirstByte_expectByte() {
        Bytes bytes = createBytes( new byte[] {97,98} );

        assertEquals( 97, bytes.getByte(0) );
    }

    @Test
    public void givenNoneEmptyBuffer_getFirstByteTwice_expectSameByteBackEachTime() {
        Bytes bytes = createBytes( new byte[] {97,98} );

        assertEquals( 97, bytes.getByte(0) );
        assertEquals( 97, bytes.getByte(0) );
    }

    @Test
    public void givenNoneEmptyBuffer_readEachByteBackMultipleTimesInAndOutOfOrder() {
        Bytes bytes = createBytes( new byte[] {97,98,99} );

        assertEquals( 97, bytes.getByte(0) );
        assertEquals( 98, bytes.getByte(1) );
        assertEquals( 99, bytes.getByte(2) );
        assertEquals( 98, bytes.getByte(1) );
        assertEquals( 99, bytes.getByte(2) );
        assertEquals( 97, bytes.getByte(0) );
    }


// toCharacters tests

    @Test
    public void givenEmptyBuffer_callToCharacters_expectEmptyCharacters() {
        // TODO
    }

// appendBytes tests

    @Test
    public void givenEmptyBuffers_appendToNull_expectException() {
        Bytes bytes1 = createBytes( new byte[] {} );

        try {
            bytes1.appendBytes( null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'other' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenTwoEmptyBuffers_appendTo_expectEmptyBytesBack() {
        Bytes bytes1 = createBytes( new byte[] {} );
        Bytes bytes2 = createBytes( new byte[] {} );

        Bytes bytes3 = bytes1.appendBytes( bytes2 );

        assertEquals( 0, bytes3.length() );
    }

    @Test
    public void givenEmptyBufferAndNonEmptyBuffer_appendTo_expectEmptyBytesBackExactInstanceOfNonEmptyBytes() {
        Bytes bytes1 = createBytes( new byte[] {} );
        Bytes bytes2 = createBytes( new byte[] {97,98} );

        Bytes bytes3 = bytes1.appendBytes( bytes2 );

        assertTrue( bytes2 == bytes3 );
    }

    @Test
    public void givenNonEmptyBufferAndEmptyBuffer_appendTo_expectEmptyBytesBackExactInstanceOfNonEmptyBytes() {
        Bytes bytes1 = createBytes( new byte[] {97,98} );
        Bytes bytes2 = createBytes( new byte[] {} );

        Bytes bytes3 = bytes1.appendBytes( bytes2 );

        assertTrue( bytes1 == bytes3 );
    }

    @Test
    public void givenTwoNonEmptyBuffers_appendTo_expectConcatenatedResult() {
        Bytes bytes1 = createBytes( new byte[] {97, 98} );
        Bytes bytes2 = createBytes( new byte[] {99,100,101} );

        Bytes bytes3 = bytes1.appendBytes( bytes2 );

        assertEquals( 97, bytes3.getByte(0) );
        assertEquals( 98, bytes3.getByte(1) );
        assertEquals( 99, bytes3.getByte(2) );
        assertEquals(100, bytes3.getByte(3) );
        assertEquals( 101, bytes3.getByte( 4 ) );
    }

    @Test
    public void givenTwoNonEmptyBuffers_appendTo_expectStreamOffsetZero() {
        Bytes bytes1 = createBytes( new byte[] {97, 98} );
        Bytes bytes2 = createBytes( new byte[] {99,100,101} );

        Bytes bytes3 = bytes1.appendBytes( bytes2 );

        assertEquals( 0, bytes3.streamOffset() );
    }

// consume tests

    @Test
    public void givenEmptyBuffer_consumeZeroBytes_expectOriginalBufferBack() {
        Bytes bytes1 = createBytes( new byte[] {} );
        Bytes bytes2 = bytes1.consume(0);

        assertTrue( bytes1 == bytes2 );
    }

    @Test
    public void givenEmptyBuffer_consumeOneByte_expectException() {
        Bytes bytes1 = createBytes( new byte[] {} );

        try {
            bytes1.consume( 1 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numBytes' (1) must be <= 0", e.getMessage() );
        }
    }

    @Test
    public void givenThreeBytes_consumeOneByte_expectResultToHoldLastTwoBytes() {
        Bytes bytes1 = createBytes( new byte[] {96,97,98} );
        Bytes bytes2 = bytes1.consume(1);

        assertEquals(  2, bytes2.length() );
        assertEquals( 97, bytes2.getByte(0) );
        assertEquals( 98, bytes2.getByte(1) );
    }

    @Test
    public void givenTwoBytes_consumeAllBytes_expectResultToBeEmpty() {
        Bytes bytes1 = createBytes( new byte[] {96,97} );
        Bytes bytes2 = bytes1.consume(2);

        assertEquals(  0, bytes2.length() );
    }

    @Test
    public void givenTwoBytes_consumeThreeBytes_expectException() {
        Bytes bytes1 = createBytes( new byte[] {96,97} );

        try {
            bytes1.consume( 3 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numBytes' (3) must be <= 2", e.getMessage() );
        }
    }


// writeTo(ByteBuffer,int numBytes) tests

    @Test
    public void givenEmptyBytes_writeToByteBuffer1Byte_expectException() {
        Bytes      bytes1     = createBytes( new byte[] {} );
        ByteBuffer destBuffer = ByteBuffer.allocate( 10 );

        try {
            bytes1.writeTo( destBuffer, 1 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numBytes' (1) must be <= 0", e.getMessage() );
        }
    }

    @Test
    public void givenOneByte_writeToByteBuffer1Byte_expectSuccessfulTransfer() {
        Bytes      bytes1     = createBytes( new byte[] {90} );
        ByteBuffer destBuffer = ByteBuffer.allocate( 10 );

        bytes1.writeTo( destBuffer, 1 );

        assertEquals( 1, destBuffer.position() );
        assertEquals( 90, destBuffer.get(0) );
    }

    @Test
    public void givenTwoBytes_writeToByteBuffer2Bytes_expectSuccessfulTransfer() {
        Bytes      bytes1     = createBytes( new byte[] {90,91} );
        ByteBuffer destBuffer = ByteBuffer.allocate( 10 );

        bytes1.writeTo( destBuffer, 2 );

        assertEquals( 2, destBuffer.position() );
        assertEquals( 90, destBuffer.get(0) );
        assertEquals( 91, destBuffer.get(1) );
    }

    @Test
    public void givenTwoBytes_writeToByteBuffer1Byte_expectSuccessfulTransfer() {
        Bytes      bytes1     = createBytes( new byte[] {90,91} );
        ByteBuffer destBuffer = ByteBuffer.allocate( 10 );

        bytes1.writeTo( destBuffer, 1 );

        assertEquals( 1, destBuffer.position() );
        assertEquals( 90, destBuffer.get(0) );
    }

    @Test
    public void givenTwoBytes_writeToByteBuffer3Byte_expectException() {
        Bytes      bytes1     = createBytes( new byte[] {90,91} );
        ByteBuffer destBuffer = ByteBuffer.allocate( 10 );

        try {
            bytes1.writeTo( destBuffer, 3 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numBytes' (3) must be <= 2", e.getMessage() );
        }
    }

    @Test
    public void givenTwoBytesLeftAfterBeingConsumedBuffer_writeToByteBuffer1Byte_expectSuccessfulTransfer() {
        Bytes      bytes1     = createBytes( new byte[] {90,91,92} );
        Bytes      bytes2     = bytes1.consume(1);
        ByteBuffer destBuffer = ByteBuffer.allocate( 10 );

        bytes2.writeTo( destBuffer, 1 );

        assertEquals( 1, destBuffer.position() );
        assertEquals( 91, destBuffer.get(0) );
    }

// asByteBuffer
// asByteArray
// slice
// from

}
