package com.mosaic.io;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

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
        Bytes bytes2 = bytes1.skipBytes( 1 );

        assertEquals(  1, bytes2.streamOffset() );
    }

    @Test
    public void givenThreeBytes_consumeTwoBytes_expectStreamOffset2() {
        Bytes bytes1 = createBytes( new byte[] {96, 97, 98} );
        Bytes bytes2 = bytes1.skipBytes( 2 );

        assertEquals(  2, bytes2.streamOffset() );
    }

    @Test
    public void givenThreeBytes_consumeOneByteTwice_expectStreamOffset2() {
        Bytes bytes1 = createBytes( new byte[] {96,97,98} );
        Bytes bytes2 = bytes1.skipBytes( 1 );
        Bytes bytes3 = bytes2.skipBytes( 1 );

        assertEquals(  2, bytes3.streamOffset() );
    }

    @Test
    public void givenFiveBytesMadeViaAppendingTwoBuffers_consumeOverByteBufferBoundary_expectStreamOffset() {
        Bytes bytes1 = createBytes( new byte[] {96,97,98} );
        Bytes bytes2 = createBytes( new byte[] {99, 100} );
        Bytes bytes3 = bytes1.appendBytes( bytes2 );
        Bytes bytes4 = bytes3.skipBytes( 4 );

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
        Bytes bytes5 = bytes4.skipBytes( 2 ).skipBytes( 3 );

        assertEquals(   5, bytes5.streamOffset() );
        assertEquals(   1, bytes5.length() );
        assertEquals( 101, bytes5.getByte( 0 ) );
    }

    @Test
    public void givenBytesCreatedByConsumeAppendConsumeAppend_checkContentsIsCorrectAsInternalMarkersMustBeUpdated() {
        Bytes bytes1 = createBytes( new byte[] {96,97} );
        Bytes bytes2 = createBytes( new byte[] {98,99} );
        Bytes bytes3 = createBytes( new byte[] {100, 101} );
        Bytes bytes4 = bytes1.skipBytes( 1 ).appendBytes( bytes2 ).skipBytes( 1 ).appendBytes( bytes3 );

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
    public void givenEmptyBuffer_callToCharacters_expectEmptyCharacters() throws CharConversionException {
        Bytes              bytes  = createBytes( new byte[] {} );
        DecodedBytesResult result = bytes.toCharacters( "ASCII" );

        assertEquals( 0, result.decodedCharacters.length() );
        assertEquals( 0, result.remainingBytes.length() );
    }

    @Test
    public void givenThreeASCIIBytes_callToCharacters_expectThreeCharacters() throws CharConversionException {
        Bytes              bytes  = createBytes( new byte[] {'a','b','c'} );
        DecodedBytesResult result = bytes.toCharacters( "ASCII" );

        assertEquals( 3, result.decodedCharacters.length() );
        assertEquals( 0, result.decodedCharacters.streamOffset() );

        assertEquals( 0, result.remainingBytes.length() );
        assertEquals( 3, result.remainingBytes.streamOffset() );

        assertEquals( 'a', result.decodedCharacters.getChar(0) );
        assertEquals( 'b', result.decodedCharacters.getChar( 1 ) );
        assertEquals( 'c', result.decodedCharacters.getChar( 2 ) );
    }

    @Test
    public void givenThreeUTF8BytesForTwoCharacters_callToCharacters_expectTwoCharacters() throws IOException {
        Bytes              bytes  = createBytes( "£a", "UTF-8" );
        DecodedBytesResult result = bytes.toCharacters( "UTF-8" );

        assertEquals( 2, result.decodedCharacters.length() );
        assertEquals( 0, result.decodedCharacters.streamOffset() );

        assertEquals( 0, result.remainingBytes.length() );
        assertEquals( 3, result.remainingBytes.streamOffset() );

        assertEquals( '£', result.decodedCharacters.getChar(0) );
        assertEquals( 'a', result.decodedCharacters.getChar(1) );
    }

    @Test
    public void givenFirstByteOfMultiByteCharacter_callToCharacters_expectNoDecoding() throws IOException {
        Bytes              bytes1  = createBytes( "£", "UTF-8" );
        Bytes              bytes2  = bytes1.subset( 0, 1 );
        DecodedBytesResult result = bytes2.toCharacters( "UTF-8" );

        assertEquals( 0, result.decodedCharacters.length() );
        assertEquals( 0, result.decodedCharacters.streamOffset() );

        assertEquals( 1, result.remainingBytes.length() );
        assertEquals( 0, result.remainingBytes.streamOffset() );
    }

    @Test
    public void givenFirstByteOfMultiByteCharacter_callToCharactersThenAppendFinishingBytes_expectDecoding() throws IOException {
        Bytes              bytes1 = createBytes( "£", "UTF-8" );
        Bytes              bytes2 = bytes1.subset(0,1);
        Bytes              bytes3 = bytes2.appendBytes( bytes1.subset(1,2) );



//        ByteBuffer buf = ByteBuffer.allocate( 2 );
//        bytes3.writeTo( buf );
//        buf.flip();
//
//        CharBuffer cb = Charset.forName( "UTF-8" ).decode( buf );
//        cb.flip();
//        System.out.println( "cb.toString() = " + cb.toString() );



        DecodedBytesResult result = bytes3.toCharacters( "UTF-8" );

        assertEquals( 1, result.decodedCharacters.length() );
        assertEquals( 0, result.decodedCharacters.streamOffset() );
        assertEquals( '£', result.decodedCharacters.getChar(0) );

        assertEquals( 0, result.remainingBytes.length() );
        assertEquals( 2, result.remainingBytes.streamOffset() );
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

// skipBytes tests

    @Test
    public void givenEmptyBuffer_consumeZeroBytes_expectOriginalBufferBack() {
        Bytes bytes1 = createBytes( new byte[] {} );
        Bytes bytes2 = bytes1.skipBytes( 0 );

        assertTrue( bytes1 == bytes2 );
    }

    @Test
    public void givenEmptyBuffer_consumeOneByte_expectException() {
        Bytes bytes1 = createBytes( new byte[] {} );

        try {
            bytes1.skipBytes( 1 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numBytes' (1) must be <= 0", e.getMessage() );
        }
    }

    @Test
    public void givenThreeBytes_consumeOneByte_expectResultToHoldLastTwoBytes() {
        Bytes bytes1 = createBytes( new byte[] {96,97,98} );
        Bytes bytes2 = bytes1.skipBytes( 1 );

        assertEquals(  2, bytes2.length() );
        assertEquals( 97, bytes2.getByte(0) );
        assertEquals( 98, bytes2.getByte( 1 ) );
    }

    @Test
    public void givenTwoBytes_consumeAllBytes_expectResultToBeEmpty() {
        Bytes bytes1 = createBytes( new byte[] {96,97} );
        Bytes bytes2 = bytes1.skipBytes( 2 );

        assertEquals(  0, bytes2.length() );
    }

    @Test
    public void givenTwoBytes_consumeThreeBytes_expectException() {
        Bytes bytes1 = createBytes( new byte[] {96,97} );

        try {
            bytes1.skipBytes( 3 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numBytes' (3) must be <= 2", e.getMessage() );
        }
    }

// subset tests

    @Test
    public void givenEmptyBytes_subset0To0_expectIdenticalBytesBack() {
        Bytes bytes1 = createBytes( new byte[] {} );

        Bytes bytes2 = bytes1.subset(0,0);

        assertTrue( bytes1 == bytes2 );
    }

    @Test
    public void givenEmptyBytes_subset0To1_expectException() {
        Bytes bytes1 = createBytes( new byte[] {} );

        try {
            bytes1.subset( 0, 1 );
            fail( "Expected IndexOutOfBoundsException" );
        } catch (IndexOutOfBoundsException e) {
            assertEquals( "'fromInc' (0) must be >= 0 and < 0", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBytes_subsetM1To0_expectException() {
        Bytes bytes1 = createBytes( new byte[] {} );

        try {
            bytes1.subset( -1, 0 );
            fail( "Expected IndexOutOfBoundsException" );
        } catch (IndexOutOfBoundsException e) {
            assertEquals( "'fromInc' (-1) must be >= 0 and < 0", e.getMessage() );
        }
    }

    @Test
    public void givenThreeBytes_subsetM1To0_expectException() {
        Bytes bytes1 = createBytes( new byte[] {97,98,99} );

        try {
            bytes1.subset( -1, 0 );
            fail( "Expected IndexOutOfBoundsException" );
        } catch (IndexOutOfBoundsException e) {
            assertEquals( "'fromInc' (-1) must be >= 0 and < 3", e.getMessage() );
        }
    }

    @Test
    public void givenThreeBytes_subsetM1To3_expectException() {
        Bytes bytes1 = createBytes( new byte[] {97,98,99} );

        try {
            bytes1.subset( -1, 3 );
            fail( "Expected IndexOutOfBoundsException" );
        } catch (IndexOutOfBoundsException e) {
            assertEquals( "'fromInc' (-1) must be >= 0 and < 3", e.getMessage() );
        }
    }

    @Test
    public void givenThreeBytes_subset1To2_expectOneByteBack() {
        Bytes bytes1 = createBytes( new byte[] {97,98,99} );

        Bytes bytes2 = bytes1.subset( 1, 2 );

        assertEquals( 1, bytes2.length() );
        assertEquals( 1, bytes2.streamOffset() );
        assertEquals( 98, bytes2.getByte(0) );
    }

    @Test
    public void givenThreeBytes_subset1To3_expectOneByteBack() {
        Bytes bytes1 = createBytes( new byte[] {97,98,99} );

        Bytes bytes2 = bytes1.subset( 1, 3 );

        assertEquals( 2, bytes2.length() );
        assertEquals( 1, bytes2.streamOffset() );
        assertEquals( 98, bytes2.getByte(0) );
        assertEquals( 99, bytes2.getByte(1) );
    }

    // TODO append then subview from first bucket
    // TODO append then subview from cross both buckets
    // TODO append then subview from second bucket

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
        Bytes      bytes2     = bytes1.skipBytes( 1 );
        ByteBuffer destBuffer = ByteBuffer.allocate( 10 );

        bytes2.writeTo( destBuffer, 1 );

        assertEquals( 1, destBuffer.position() );
        assertEquals( 91, destBuffer.get(0) );
    }

// writeTo tests

    @Test
    public void givenTwoBytesSplitAcrossTwoBuckets_writeTo_expectTwoBytes() throws IOException {
        Bytes              bytes1 = createBytes( "£", "UTF-8" );
        Bytes              bytes2 = bytes1.subset(0,1);
        Bytes              bytes3 = bytes2.appendBytes( bytes1.subset(1,2) );

        ByteBuffer destBuf = ByteBuffer.allocate( 2 );
        bytes3.writeTo( destBuf );

        assertEquals( 2, destBuf.position() );
        assertEquals( bytes1.getByte(0), bytes3.getByte(0) );
        assertEquals( bytes1.getByte(1), bytes3.getByte(1) );
    }




// asByteBuffer
// asByteArray
// slice




    private Bytes createBytes( String str, String charset ) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter out = new OutputStreamWriter( byteOutputStream, "UTF-8" );

        out.write( "£a" );
        out.flush();

        return createBytes( byteOutputStream.toByteArray() );
    }

}
