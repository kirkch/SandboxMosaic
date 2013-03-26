package com.mosaic.io;

import com.mosaic.jtunit.TestTools;
import org.junit.Test;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import static org.junit.Assert.*;

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
    public void givenStreamWithCharacters_hasReceivedEOS_expectFalse() {
        CharacterStream stream = new CharacterStream("abc");

        assertFalse( stream.hasReceivedEOS() );
    }

    @Test
    public void givenEOS_append3Characters_expectError() {
        CharacterStream stream = new CharacterStream("abc");
        stream.appendEOS();

        assertTrue( stream.hasReceivedEOS() );

        try {
            stream.appendCharacters( "abc" );

            fail( "Expected IllegalStateException" );
        } catch (IllegalStateException e) {
            assertEquals( "cannot append to closed stream", e.getMessage() );
        }
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

    @Test
    public void given3CharacterStream_pushMarkSkip2Characters_expectStreamToBeAsSkippingOf2Chars() {
        CharacterStream stream = new CharacterStream("abc");

        stream.pushMark();
        stream.skipCharacters( 2 );

        assertEquals( 1, stream.length() );
        assertEquals( new CharPosition(0,2,2), stream.getPosition() );
        assertEquals( "c", stream.toString() );
    }

    @Test
    public void given3CharacterStream_pushMarkSkip2CharactersPopMark_expectStreamToBeUnchanged() {
        CharacterStream stream = new CharacterStream("abc");

        stream.pushMark();
        stream.skipCharacters( 2 );
        stream.returnToMark();

        assertEquals( 3, stream.length() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
        assertEquals( "abc", stream.toString() );
    }

    @Test
    public void given3CharacterStream_popMark_expectError() {
        CharacterStream stream = new CharacterStream("abc");

        try {
            stream.returnToMark();
            fail( "Expected IllegalStateException" );
        } catch (IllegalStateException e) {
            assertEquals( "cannot pop from an empty stack", e.getMessage() );
        }
    }

    @Test
    public void given3CharacterStream_skip1PushSkip1Pop_expectTobeReturnedToMarkedPoint() {
        CharacterStream stream = new CharacterStream("abc");

        stream.skipCharacters(1);
        stream.pushMark();
        stream.skipCharacters(1);
        stream.returnToMark();

        assertEquals( 2, stream.length() );
        assertEquals( new CharPosition(0,1,1), stream.getPosition() );
        assertEquals( "bc", stream.toString() );
    }

    @Test
    public void given3CharacterStream_PushSkip1PushSkip1Pop_expectTobeReturnedToFirstMarkedPoint() {
        CharacterStream stream = new CharacterStream("abc");

        stream.pushMark();
        stream.skipCharacters(1);
        stream.pushMark();
        stream.skipCharacters(1);
        stream.returnToMark();

        assertEquals( 2, stream.length() );
        assertEquals( new CharPosition(0,1,1), stream.getPosition() );
        assertEquals( "bc", stream.toString() );
    }

    @Test
    public void given3CharacterStream_PushSkip1PushSkip1PopPop_expectTobeReturnedToFirstMarkedPoint() {
        CharacterStream stream = new CharacterStream("abc");

        stream.pushMark();
        stream.skipCharacters( 1 );
        stream.pushMark();
        stream.skipCharacters( 1 );
        stream.returnToMark();
        stream.returnToMark();

        assertEquals( 3, stream.length() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
        assertEquals( "abc", stream.toString() );
    }

    @Test
    public void given3CharacterStream_PushSkip1PushSkip1PopPopPop_expectError() {
        CharacterStream stream = new CharacterStream("abc");

        stream.pushMark();
        stream.skipCharacters( 1 );
        stream.pushMark();
        stream.skipCharacters( 1 );
        stream.returnToMark();
        stream.returnToMark();


        try {
            stream.returnToMark();
            fail( "Expected IllegalStateException" );
        } catch (IllegalStateException e) {
            assertEquals( "cannot pop from an empty stack", e.getMessage() );
        }
    }

    @Test
    public void givenGivenCharacterStreamMadeFromTwoBlocksOfCharacters_skipFirstBlockPushMarkSkip2CharactersThenPop_expectFirstBlockToBeGCd() {
        CharacterStream stream = new CharacterStream();

        Characters block1 = Characters.wrapString("abc");
        Characters block2 = Characters.wrapString("def");

        Reference<Characters> block1Ref = new WeakReference( block1, null);


        stream.appendCharacters( block1Ref.get() );
        stream.skipCharacters( 3 );
        stream.pushMark();
        stream.appendCharacters( block2 );
        stream.skipCharacters( 2 );
        stream.returnToMark();

        assertEquals( 3, stream.length() );
        assertEquals( new CharPosition(0,3,3), stream.getPosition() );
        assertEquals( "def", stream.toString() );

        // release block1 so that only the ref within the stream prevents it from being GC'd
        //noinspection UnusedAssignment
        block1 = null;

        TestTools.spinUntilReleased( block1Ref );
    }

    @Test
    public void given3CharacterStream_startsWithFirstCharcter_expectTrue() {
        CharacterStream stream = new CharacterStream("abc");

        assertTrue( stream.startsWith("a") );
    }

    @Test
    public void given3CharacterStream_startsWithFirstTwoCharcters_expectTrue() {
        CharacterStream stream = new CharacterStream("abc");

        assertTrue( stream.startsWith("ab") );
    }

    @Test
    public void given3CharacterStream_startsWithLastChar_expectFalse() {
        CharacterStream stream = new CharacterStream("abc");

        assertFalse( stream.startsWith( "c" ) );
    }

    @Test
    public void given3CharacterStream_skipTwoCharsStartsWithLastChar_expectFalse() {
        CharacterStream stream = new CharacterStream("abc");

        stream.skipCharacters( 2 );

        assertTrue( stream.startsWith( "c" ) );
    }

    @Test
    public void given3CharacterStream_consumeOneCharacter_expectA() {
        CharacterStream stream = new CharacterStream("abc");

        assertEquals( "a", stream.consumeCharacters( 1 ) );
        assertEquals( "bc", stream.toString() );
    }

    @Test
    public void given3CharacterStream_consumeTwoCharacter_expectAB() {
        CharacterStream stream = new CharacterStream("abc");

        assertEquals( "ab", stream.consumeCharacters( 2 ) );
        assertEquals( "c", stream.toString() );
    }

    @Test
    public void given3CharacterStream_consumeThreeCharacter_expectABC() {
        CharacterStream stream = new CharacterStream("abc");

        assertEquals( "abc", stream.consumeCharacters( 3 ) );
        assertEquals( "", stream.toString() );
    }

    @Test
    public void given3CharacterStream_consumeFourharacter_expectError() {
        CharacterStream stream = new CharacterStream("abc");

        try {
            stream.consumeCharacters( 4 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numCharacters' (4) must be <= 3", e.getMessage() );
        }
    }

    @Test
    public void given3CharacterStream_skipOneConsumeOneCharacter_expectB() {
        CharacterStream stream = new CharacterStream("abc");

        stream.skipCharacters( 1 );
        assertEquals( "b", stream.consumeCharacters(1) );
        assertEquals( "c", stream.toString() );
    }

    @Test
    public void given3CharacterStream_skipOneConsumeTwoCharacter_expectBC() {
        CharacterStream stream = new CharacterStream("abc");

        stream.skipCharacters( 1 );
        assertEquals( "bc", stream.consumeCharacters(2) );
        assertEquals( "", stream.toString() );
    }

    @Test
    public void given3CharacterStream_skipOneConsumeThreeharacter_expectError() {
        CharacterStream stream = new CharacterStream("abc");

        stream.skipCharacters( 1 );

        try {
            stream.consumeCharacters( 3 );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'numCharacters' (3) must be <= 2", e.getMessage() );
        }
    }


}
