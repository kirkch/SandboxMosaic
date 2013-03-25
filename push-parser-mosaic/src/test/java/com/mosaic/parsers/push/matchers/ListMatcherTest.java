package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharPosition;
import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ListMatcherTest {

    @Test
    public void givenNullPrefixMatcher_expectException() {
        try {
            new ListMatcher( null, new ConstantMatcher("e"), new ConstantMatcher(","), new ConstantMatcher(";") );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'prefixMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenNullElementMatcher_expectException() {
        try {
            new ListMatcher( new ConstantMatcher("+"), null, new ConstantMatcher(","), new ConstantMatcher(";") );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'elementMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenNullSeperatingMatcher_expectException() {
        try {
            new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e"), null, new ConstantMatcher(";") );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'seperatingMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenNullEndOfListMatcher_expectException() {
        try {
            new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e"), new ConstantMatcher(","), null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'postfixMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBytes_expectIsIncompleteMatch() {
        CharacterStream stream = new CharacterStream( "" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertIncompleteMatch( matcher, stream, result );
    }

    @Test
    public void givenBytesThatErrorOnPrefix_expectFailedMatch() {
        CharacterStream stream = new CharacterStream( "z" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertFailedMatch( stream, result, "expected '+'" );
    }

    @Test
    public void givenBytesThatPartiallyPrefixElement_expectPartialMatch() {
        CharacterStream stream = new CharacterStream( "+" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("++"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertIncompleteMatch( matcher, stream, result );
    }

    @Test
    public void givenBytesThatErrorOnFirstElement_expectFailedMatch() {
        CharacterStream stream = new CharacterStream( "+z" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertFailedMatch( stream, result, "expected 'e'" );
    }

    @Test
    public void givenBytesThatPartiallyMatchFirstElement_expectPartialMatch() {
        CharacterStream stream = new CharacterStream( "+e" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertIncompleteMatch( matcher, stream, result );
    }

    @Test
    public void givenBytesThatPartiallyMatchFirstElement_thenEndStream_expectFailedMatch() {
        CharacterStream stream = new CharacterStream( "+e" ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertFailedMatch( stream, result, "expected 'e1'" );
    }

    @Test
    public void givenBytesThatFullyMatchesFirstElementButDoesNotTerminateList_expectInProgress() {
        CharacterStream stream = new CharacterStream( "+e1" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertIncompleteMatch( matcher, stream, result );
    }

    @Test
    public void givenBytesThatFullyMatchesFirstElement_thenEndOfStream_expectNoMatchAsEndOfListDidNotMatch() {
        CharacterStream stream = new CharacterStream( "+e1" ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertFailedMatch( stream, result, "end of stream reached" );
    }

    @Test
    public void givenBytesThatFullyMatchesFirstElement_thenEndOfListMatch_expectMatch() {
        CharacterStream stream = new CharacterStream( "+e1;" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList("e1"), result.getResult() );
        assertEquals( new CharPosition(0,4,4), stream.getPosition() );
    }

    @Test
    public void givenBytesThatOneElementAndSeperator_expectInProgress() {
        CharacterStream stream = new CharacterStream( "+e1," );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertIncompleteMatch( matcher, stream, result );
    }


    @Test
    public void givenBytesThatOneElementAndPartOfSeperator_expectInProgress() {
        CharacterStream stream = new CharacterStream( "+e1a" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher("ab"), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertIncompleteMatch( matcher, stream, result );
    }

    @Test
    public void givenBytesThatOneElementAndSeperator_thenEndStream_expectFailedMatch() {
        CharacterStream stream = new CharacterStream( "+e1," ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertFailedMatch( stream, result, "expected 'e1'" );
    }

    @Test
    public void givenBytesThatOneElementAndSeperatorThenElement_expectInProgress() {
        CharacterStream stream = new CharacterStream( "+e1,e1" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertIncompleteMatch( matcher, stream, result );
    }

    @Test
    public void givenBytesThatOneElementAndSeperatorThenElement_thenEndStream_expectFailedMatch() {
        CharacterStream stream = new CharacterStream( "+e1,e1" ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertFailedMatch( stream, result, "end of stream reached" );
    }

    @Test
    public void givenBytesThatOneElementAndSeperatorThenElement_thenEndOfListMatch_expectTwoElementResult() {
        CharacterStream stream = new CharacterStream( "+e1,e1;" ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList("e1","e1"), result.getResult() );
        assertEquals( new CharPosition( 0, 7, 7 ), stream.getPosition() );
    }

    @Test
    public void givenBytesThreeSeperatedElements_expectInProgress() {
        CharacterStream stream = new CharacterStream( "+e1,e1,e1" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertIncompleteMatch( matcher, stream, result );
    }

    @Test
    public void givenBytesThreeSeperatedElements_thenEndStream_expectFailedMatch() {
        CharacterStream stream = new CharacterStream( "+e1,e1,e1" ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertFailedMatch( stream, result, "end of stream reached" );
    }

    @Test
    public void givenBytesThreeSeperatedElements_thenEndOfListMatch_expectThreeElementResult() {
        CharacterStream stream = new CharacterStream( "+e1,e1,e1;" ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList("e1","e1","e1"), result.getResult() );
        assertEquals( new CharPosition( 0, 10, 10 ), stream.getPosition() );
    }

    private void assertFailedMatch( CharacterStream stream, MatchResult<List<String>> result, String expectedMessage ) {
        assertTrue( result.hasFailedToMatch() );
        assertEquals( expectedMessage, result.getFailedToMatchDescription() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
        assertTrue( result.getNextMatcher() == null );
    }

    private void assertIncompleteMatch( Matcher<List<String>> listMatcher, CharacterStream stream, MatchResult<List<String>> result ) {
        assertTrue( result.isIncompleteMatch() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
        assertTrue( result.getNextMatcher() == listMatcher );
    }

}
