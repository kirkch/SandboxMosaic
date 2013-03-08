package com.mosaic.io;

import org.junit.Test;

import java.nio.CharBuffer;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public abstract class BaseCharactersTestCases {

    protected abstract Characters createCharacters( char[] chars );

// factory tests

    @Test
    public void givenNullCharactersBuffer_createCharactersWrapper_expectNullPointerException() {
        try {
            createCharacters( null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'src' must not be null", e.getMessage() );
        }
    }


// streamOffset tests

    @Test
    public void givenEmptyCharactersBuffer_callStreamOffset_expectZero() {
        Characters      chars = createCharacters( new char[] {} );

        assertEquals( 0, chars.streamOffset() );
    }

    @Test
    public void givenTwoCharactersBuffer_callStreamOffset_expectZero() {
        Characters      chars = createCharacters( new char[] {97,98} );

        assertEquals( 0, chars.streamOffset() );
    }

    @Test
    public void givenThreeCharacters_consumeOneByte_expectStreamOffset1() {
        Characters chars1 = createCharacters( new char[] {96, 97, 98} );
        Characters chars2 = chars1.skipCharacters( 1 );

        assertEquals(  1, chars2.streamOffset() );
    }

    @Test
    public void givenThreeCharacters_consumeTwoCharacters_expectStreamOffset2() {
        Characters chars1 = createCharacters( new char[] {96, 97, 98} );
        Characters chars2 = chars1.skipCharacters( 2 );

        assertEquals(  2, chars2.streamOffset() );
    }

    @Test
    public void givenThreeCharacters_consumeOneByteTwice_expectStreamOffset2() {
        Characters chars1 = createCharacters( new char[] {96,97,98} );
        Characters chars2 = chars1.skipCharacters( 1 );
        Characters chars3 = chars2.skipCharacters( 1 );

        assertEquals(  2, chars3.streamOffset() );
    }

    @Test
    public void givenFiveCharactersMadeViaAppendingTwoBuffers_consumeOverCharBufferBoundary_expectStreamOffset() {
        Characters chars1 = createCharacters( new char[] {96,97,98} );
        Characters chars2 = createCharacters( new char[] {99, 100} );
        Characters chars3 = chars1.appendCharacters( chars2 );
        Characters chars4 = chars3.skipCharacters( 4 );

        assertEquals(   4, chars4.streamOffset() );
        assertEquals(   1, chars4.length() );
        assertEquals( 100, chars4.getChar( 0 ) );
    }

    @Test
    public void givenSixCharactersMadeViaAppendingThreeBuffers_consumeTwiceUntilOverFirstTwoCharBufferBoundary_expectStreamOffset() {
        Characters chars1 = createCharacters( new char[] {96,97} );
        Characters chars2 = createCharacters( new char[] {98,99} );
        Characters chars3 = createCharacters( new char[] {100,101} );
        Characters chars4 = chars1.appendCharacters( chars2 ).appendCharacters( chars3 );
        Characters chars5 = chars4.skipCharacters( 2 ).skipCharacters( 3 );

        assertEquals(   5, chars5.streamOffset() );
        assertEquals(   1, chars5.length() );
        assertEquals( 101, chars5.getChar( 0 ) );
    }

    @Test
    public void givenCharactersCreatedByConsumeAppendConsumeAppend_checkContentsIsCorrectAsInternalMarkersMustBeUpdated() {
        Characters chars1 = createCharacters( new char[] {96,97} );
        Characters chars2 = createCharacters( new char[] {98,99} );
        Characters chars3 = createCharacters( new char[] {100, 101} );
        Characters chars4 = chars1.skipCharacters( 1 ).appendCharacters( chars2 ).skipCharacters( 1 ).appendCharacters( chars3 );

        assertEquals(   2, chars4.streamOffset() );
        assertEquals(   4, chars4.length() );
        assertEquals(  98, chars4.getChar( 0 ) );
        assertEquals(  99, chars4.getChar( 1 ) );
        assertEquals( 100, chars4.getChar( 2 ) );
        assertEquals( 101, chars4.getChar( 3 ) );
    }

// length tests

    @Test
    public void givenEmptyCharactersBuffer_callLength_expectZero() {
        Characters chars = createCharacters( new char[] {} );

        assertEquals( 0, chars.length() );
    }

    @Test
    public void given3ByteCharactersBuffer_callLength_expect3() {
        Characters chars = createCharacters( new char[] {97, 98, 99} );

        assertEquals( 3, chars.length() );
    }

    @Test
    public void given3ByteCharactersBuffer_callGetByteFollowedByLength_expectGetByteToNotHaveChangedTheLength() {
        Characters chars = createCharacters( new char[] {97, 98, 99} );

        chars.getChar(0);

        assertEquals( 3, chars.length() );
    }


// getChar tests

    @Test
    public void givenEmptyBuffer_getFirstByte_expectException() {
        Characters chars = createCharacters( new char[] {} );

        try {
            chars.getChar( 0 );

            fail( "Expected IndexOutOfBoundsException" );
        } catch (IndexOutOfBoundsException e) {
            assertEquals( null, e.getMessage() );
        }
    }

    @Test
    public void givenNoneEmptyBuffer_getFirstByte_expectByte() {
        Characters chars = createCharacters( new char[] {97,98} );

        assertEquals( 97, chars.getChar(0) );
    }

    @Test
    public void givenNoneEmptyBuffer_getFirstByteTwice_expectSameByteBackEachTime() {
        Characters chars = createCharacters( new char[] {97,98} );

        assertEquals( 97, chars.getChar(0) );
        assertEquals( 97, chars.getChar(0) );
    }

    @Test
    public void givenNoneEmptyBuffer_readEachByteBackMultipleTimesInAndOutOfOrder() {
        Characters chars = createCharacters( new char[] {97,98,99} );

        assertEquals( 97, chars.getChar(0) );
        assertEquals( 98, chars.getChar(1) );
        assertEquals( 99, chars.getChar(2) );
        assertEquals( 98, chars.getChar(1) );
        assertEquals( 99, chars.getChar(2) );
        assertEquals( 97, chars.getChar(0) );
    }


// toCharacters tests

    @Test
    public void givenEmptyBuffer_callToCharacters_expectEmptyCharacters() {
        // TODO
    }

// appendCharacters tests

    @Test
    public void givenEmptyBuffers_appendToNull_expectException() {
        Characters chars1 = createCharacters( new char[] {} );

        try {
            chars1.appendCharacters( null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'other' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenTwoEmptyBuffers_appendTo_expectEmptyCharactersBack() {
        Characters chars1 = createCharacters( new char[] {} );
        Characters chars2 = createCharacters( new char[] {} );

        Characters chars3 = chars1.appendCharacters( chars2 );

        assertEquals( 0, chars3.length() );
    }

    @Test
    public void givenEmptyBufferAndNonEmptyBuffer_appendTo_expectEmptyCharactersBackExactInstanceOfNonEmptyCharacters() {
        Characters chars1 = createCharacters( new char[] {} );
        Characters chars2 = createCharacters( new char[] {97,98} );

        Characters chars3 = chars1.appendCharacters( chars2 );

        assertTrue( chars2 == chars3 );
    }

    @Test
    public void givenNonEmptyBufferAndEmptyBuffer_appendTo_expectEmptyCharactersBackExactInstanceOfNonEmptyCharacters() {
        Characters chars1 = createCharacters( new char[] {97,98} );
        Characters chars2 = createCharacters( new char[] {} );

        Characters chars3 = chars1.appendCharacters( chars2 );

        assertTrue( chars1 == chars3 );
    }

    @Test
    public void givenTwoNonEmptyBuffers_appendTo_expectConcatenatedResult() {
        Characters chars1 = createCharacters( new char[] {97, 98} );
        Characters chars2 = createCharacters( new char[] {99,100,101} );

        Characters chars3 = chars1.appendCharacters( chars2 );

        assertEquals( 97, chars3.getChar(0) );
        assertEquals( 98, chars3.getChar(1) );
        assertEquals( 99, chars3.getChar(2) );
        assertEquals(100, chars3.getChar(3) );
        assertEquals( 101, chars3.getChar( 4 ) );
    }

    @Test
    public void givenTwoNonEmptyBuffers_appendTo_expectStreamOffsetZero() {
        Characters chars1 = createCharacters( new char[] {97, 98} );
        Characters chars2 = createCharacters( new char[] {99,100,101} );

        Characters chars3 = chars1.appendCharacters( chars2 );

        assertEquals( 0, chars3.streamOffset() );
    }

// consume tests

    @Test
    public void givenEmptyBuffer_consumeZeroCharacters_expectOriginalBufferBack() {
        Characters chars1 = createCharacters( new char[] {} );
        Characters chars2 = chars1.skipCharacters( 0 );

        assertTrue( chars1 == chars2 );
    }

    @Test
    public void givenEmptyBuffer_consumeOneByte_expectException() {
        Characters chars1 = createCharacters( new char[] {} );

        try {
            chars1.skipCharacters( 1 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numCharacters' (1) must be <= 0", e.getMessage() );
        }
    }

    @Test
    public void givenThreeCharacters_consumeOneByte_expectResultToHoldLastTwoCharacters() {
        Characters chars1 = createCharacters( new char[] {96,97,98} );
        Characters chars2 = chars1.skipCharacters( 1 );

        assertEquals(  2, chars2.length() );
        assertEquals( 97, chars2.getChar(0) );
        assertEquals( 98, chars2.getChar(1) );
    }

    @Test
    public void givenTwoCharacters_consumeAllCharacters_expectResultToBeEmpty() {
        Characters chars1 = createCharacters( new char[] {96,97} );
        Characters chars2 = chars1.skipCharacters( 2 );

        assertEquals(  0, chars2.length() );
    }

    @Test
    public void givenTwoCharacters_consumeThreeCharacters_expectException() {
        Characters chars1 = createCharacters( new char[] {96,97} );

        try {
            chars1.skipCharacters( 3 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numCharacters' (3) must be <= 2", e.getMessage() );
        }
    }


// writeTo(CharBuffer,int numCharacters) tests

    @Test
    public void givenEmptyCharacters_writeToCharBuffer1Byte_expectException() {
        Characters      chars1     = createCharacters( new char[] {} );
        CharBuffer destBuffer = CharBuffer.allocate( 10 );

        try {
            chars1.writeTo( destBuffer, 1 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numCharacters' (1) must be <= 0", e.getMessage() );
        }
    }

    @Test
    public void givenOneByte_writeToCharBuffer1Byte_expectSuccessfulTransfer() {
        Characters      chars1     = createCharacters( new char[] {90} );
        CharBuffer destBuffer = CharBuffer.allocate( 10 );

        chars1.writeTo( destBuffer, 1 );

        assertEquals( 1, destBuffer.position() );
        assertEquals( 90, destBuffer.get(0) );
    }

    @Test
    public void givenTwoCharacters_writeToCharBuffer2Characters_expectSuccessfulTransfer() {
        Characters      chars1     = createCharacters( new char[] {90,91} );
        CharBuffer destBuffer = CharBuffer.allocate( 10 );

        chars1.writeTo( destBuffer, 2 );

        assertEquals( 2, destBuffer.position() );
        assertEquals( 90, destBuffer.get(0) );
        assertEquals( 91, destBuffer.get(1) );
    }

    @Test
    public void givenTwoCharacters_writeToCharBuffer1Byte_expectSuccessfulTransfer() {
        Characters      chars1     = createCharacters( new char[] {90,91} );
        CharBuffer destBuffer = CharBuffer.allocate( 10 );

        chars1.writeTo( destBuffer, 1 );

        assertEquals( 1, destBuffer.position() );
        assertEquals( 90, destBuffer.get(0) );
    }

    @Test
    public void givenTwoCharacters_writeToCharBuffer3Byte_expectException() {
        Characters      chars1     = createCharacters( new char[] {90,91} );
        CharBuffer destBuffer = CharBuffer.allocate( 10 );

        try {
            chars1.writeTo( destBuffer, 3 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numCharacters' (3) must be <= 2", e.getMessage() );
        }
    }

    @Test
    public void givenTwoCharactersLeftAfterBeingConsumedBuffer_writeToCharBuffer1Byte_expectSuccessfulTransfer() {
        Characters      chars1     = createCharacters( new char[] {90,91,92} );
        Characters      chars2     = chars1.skipCharacters( 1 );
        CharBuffer destBuffer = CharBuffer.allocate( 10 );

        chars2.writeTo( destBuffer, 1 );

        assertEquals( 1, destBuffer.position() );
        assertEquals( 91, destBuffer.get(0) );
    }


// containsAt tests

    @Test
    public void givenEmptyChars_containsABCAt0_expectFalse() {
        Characters chars = createCharacters( new char[] {} );

        assertFalse( chars.containsAt("abc", 0) );
    }

    @Test
    public void givenFirstTwoTargetChars_containsABCAt0_expectFalse() {
        Characters chars = createCharacters( new char[] {'a','b'} );

        assertFalse( chars.containsAt("abc", 0) );
    }

    @Test
    public void givenAllTargetChars_containsABCAt0_expectTrue() {
        Characters chars = createCharacters( new char[] {'a','b','c'} );

        assertTrue( chars.containsAt( "abc", 0 ) );
    }

    @Test
    public void givenAllTargetAndMoreChars_containsABCAt0_expectTrue() {
        Characters chars = createCharacters( new char[] {'a','b','c','d','e'} );

        assertTrue( chars.containsAt( "abc", 0 ) );
    }

    @Test
    public void givenAllTargetCharsInMiddleOfString_containsABCAt0_expectFalse() {
        Characters chars = createCharacters( new char[] {'0','1','a','b','c','d','e'} );

        assertFalse( chars.containsAt( "abc", 0 ) );
    }

    @Test
    public void givenAllTargetCharsInMiddleOfString_containsABCAt1_expectFalse() {
        Characters chars = createCharacters( new char[] {'0','1','a','b','c','d','e'} );

        assertFalse( chars.containsAt( "abc", 1 ) );
    }

    @Test
    public void givenAllTargetCharsInMiddleOfString_containsABCAt2_expectTrue() {
        Characters chars = createCharacters( new char[] {'0','1','a','b','c','d','e'} );

        assertTrue( chars.containsAt( "abc", 2 ) );
    }

    @Test
    public void givenAllTargetCharsInMiddleOfString_containsABCAt3_expectFalse() {
        Characters chars = createCharacters( new char[] {'0','1','a','b','c','d','e'} );

        assertFalse( chars.containsAt( "abc", 3 ) );
    }

    @Test
    public void givenAllTargetCharsInMiddleOfString_containsABCAt5_expectFalse() {
        Characters chars = createCharacters( new char[] {'0','1','a','b','c','d','e'} );

        assertFalse( chars.containsAt( "abc", 5 ) );
    }

    @Test
    public void givenAllTargetCharsInMiddleOfString_containsABCAt8_expectFalse() {
        Characters chars = createCharacters( new char[] {'0','1','a','b','c','d','e'} );

        assertFalse( chars.containsAt( "abc", 8 ) );
    }

    @Test
    public void givenAllTargetCharsInMiddleOfString_containsABCAtM1_expectFalse() {
        Characters chars = createCharacters( new char[] {'0','1','a','b','c','d','e'} );

        try {
            chars.containsAt( "abc", -1 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'fromIndex' (-1) must be >= 0", e.getMessage() );
        }
    }


// asCharBuffer
// asByteArray
// slice


}
