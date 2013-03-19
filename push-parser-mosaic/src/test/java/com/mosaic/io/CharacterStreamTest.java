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
            stream.subSequence(0,1);

            fail( "Expected IndexOutOfBoundsException" );
        } catch (IndexOutOfBoundsException e) {
            assertEquals( "'start' (0) must be >= 0 and < 0", e.getMessage() );
        }
    }




    //
    //
    //
    //
    // givenEmptyStream_append3Characters_expectCharAtToReturnCharacters
    // givenEmptyStream_append3Characters_expectSubsequence0To1ToReturnString
    // givenEmptyStream_append3Characters_expectSubsequence1To2ToReturnString
    // givenEmptyStream_append3Characters_expectSubsequence0To2ToReturnString
    // givenEmptyStream_append3Characters_expectSubsequence0To3ToReturnString
    // givenEmptyStream_append3Characters_expectSubsequence0To4ToError
    // givenEmptyStream_skip1Character_expectError

    // given3CharacterStream_skip1Character_expect2CharactersLeft
    // given3CharacterStream_skip2Character_expect1CharactersLeft
    // given3CharacterStream_skip3Character_expect0CharactersLeft
    // given3CharacterStream_skip4Character_expectError

    // given3CharacterStreamWith1CharacterSkipped_getPosition_expectOffset1
    // given3CharacterStreamWith2CharacterSkipped_getPosition_expectOffset2


    // todo mark/unmark  appendString
}
