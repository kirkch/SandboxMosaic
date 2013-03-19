package com.mosaic.io;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class CharacterStreamTest {

    @Test
    public void givenEmptyStream_getLength_expect0() {
        CharacterStream stream = new CharacterStream();

        assertEquals( 0, stream.length() );
    }

    @Test
    public void givenEmptyStream_append3Characters_expectLength3() {
        CharacterStream stream = new CharacterStream();

        stream.appendCharacters( Characters.wrapString("abc") );

        assertEquals( 3, stream.length() );
    }

    @Test
    public void givenEmptyStream_getPosition_expectZeroPosition() {
        CharacterStream stream = new CharacterStream();

        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
    }

    @Test
    public void givenEmptyStream_charAt0_expectError() {
        CharacterStream stream = new CharacterStream();

        try {
            stream.charAt(0);

            fail( "Expected IndexOutOfBoundsException" );
        } catch (IndexOutOfBoundsException e) {
            assertEquals( null, e.getMessage() );
        }
    }

    @Test
    public void givenEmptyStream_subsequence0To1_expectError() {
        CharacterStream stream = new CharacterStream();

        try {
            stream.subSequence( 0, 1 );

            fail( "Expected IndexOutOfBoundsException" );
        } catch (IndexOutOfBoundsException e) {
            assertEquals( "'start' (0) must be >= 0 and < 0", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyStream_append3Characters_expectCharAtToReturnCharacters() {
        CharacterStream stream = new CharacterStream();

        stream.appendCharacters( Characters.wrapString("abc") );

        assertEquals( 'a', stream.charAt(0) );
        assertEquals( 'b', stream.charAt(1) );
        assertEquals( 'c', stream.charAt(2) );

        try {
            stream.charAt( 3 );

            fail( "Expected IndexOutOfBoundsException" );
        } catch (IndexOutOfBoundsException e) {
            assertEquals( null, e.getMessage() );
        }
    }

    @Test
    public void givenEmptyStream_append3Characters_expectSubsequence0To1ToReturnString() {
        CharacterStream stream = new CharacterStream("abc");

        assertEquals( "a", stream.subSequence( 0, 1 ) );
    }

    @Test
    public void givenEmptyStream_append3Characters_expectSubsequence1To2ToReturnString() {
        CharacterStream stream = new CharacterStream("abc");

        assertEquals( "b", stream.subSequence( 1, 2 ) );
    }

    @Test
    public void givenEmptyStream_append3Characters_expectSubsequence0To2ToReturnString() {
        CharacterStream stream = new CharacterStream("abc");

        assertEquals( "ab", stream.subSequence( 0, 2 ) );
    }

    @Test
    public void givenEmptyStream_append3Characters_expectSubsequence0To3ToReturnString() {
        CharacterStream stream = new CharacterStream("abc");

        assertEquals( "abc", stream.subSequence( 0, 3 ) );
    }

    @Test
    public void givenEmptyStream_append3Characters_expectSubsequence0To4ToError() {
        CharacterStream stream = new CharacterStream("abc");

        try {
            stream.subSequence( 0, 4 );
            fail( "Expected IndexOutOfBoundsException" );
        } catch (IndexOutOfBoundsException e) {
            assertEquals( "'end' (4) must be >= 0 and < 4", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyStream_skip1Character_expectError() {
        CharacterStream stream = new CharacterStream();

        try {
            stream.skipCharacters( 1 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numCharacters' (1) must be <= 0", e.getMessage() );
        }
    }

    @Test
    public void given3CharacterStream_skip1Character_expect2CharactersLeft() {
        CharacterStream stream = new CharacterStream("abc");

        stream.skipCharacters( 1 );

        assertEquals( 2, stream.length() );
        assertEquals( 'b', stream.charAt(0) );
        assertEquals( 'c', stream.charAt(1) );
    }

    @Test
    public void given3CharacterStream_skip2Character_expect1CharactersLeft() {
        CharacterStream stream = new CharacterStream("abc");

        stream.skipCharacters( 2 );

        assertEquals( 1, stream.length() );
        assertEquals( 'c', stream.charAt(0) );
    }

    @Test
    public void given3CharacterStream_skip3Character_expect0CharactersLeft() {
        CharacterStream stream = new CharacterStream("abc");

        stream.skipCharacters( 3 );

        assertEquals( 0, stream.length() );
    }

    @Test
    public void given3CharacterStream_skip4Character_expectError() {
        CharacterStream stream = new CharacterStream("abc");

        try {
            stream.skipCharacters( 4 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numCharacters' (4) must be <= 3", e.getMessage() );
        }
    }

    @Test
    public void given3CharacterStreamWith1CharacterSkipped_getPosition_expectOffset1() {
        CharacterStream stream = new CharacterStream("abc");

        stream.skipCharacters( 1 );

        assertEquals( new CharPosition(0,1,1), stream.getPosition() );
    }

    @Test
    public void given3CharacterStreamWith2CharacterSkipped_getPosition_expectOffset2() {
        CharacterStream stream = new CharacterStream("abc");

        stream.skipCharacters( 2 );

        assertEquals( new CharPosition(0,2,2), stream.getPosition() );
    }


    // todo mark/unmark  appendString
}
