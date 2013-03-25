package com.mosaic.parsers.push2.matchers;

import com.mosaic.io.CharPosition;
import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push2.MatchResult;
import com.mosaic.parsers.push2.Matcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class ListMatcherTest {

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
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( new CharacterStream("") );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertEquals( null, result.getFailedToMatchDescription() );
    }

    @Test
    public void givenBytesThatErrorOnFirstElement_expectFailedMatch() {
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( new CharacterStream("z") );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'e'", result.getFailedToMatchDescription() );
    }

    @Test
    public void givenBytesThatPartiallyMatchFirstElement_expectPartialMatch() {
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( new CharacterStream("e") );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
    }

    @Test
    public void givenBytesThatPartiallyMatchFirstElement_thenEndStream_expectFailedMatch() {
        CharacterStream stream = new CharacterStream( "e" ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'e1'", result.getFailedToMatchDescription() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
    }

    @Test
    public void givenBytesThatFullyMatchesFirstElementButDoesNotTerminateList_expectInProgress() {
        CharacterStream stream = new CharacterStream( "e1" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
    }

    @Test
    public void givenBytesThatFullyMatchesFirstElement_thenEndOfStream_expectNoMatchAsEndOfListDidNotMatch() {
        CharacterStream stream = new CharacterStream( "e1" ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "end of stream reached", result.getFailedToMatchDescription() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
    }

    @Test
    public void givenBytesThatFullyMatchesFirstElement_thenEndOfListMatch_expectMatch() {
        CharacterStream stream = new CharacterStream( "e1;" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList("e1"), result.getResult() );
        assertEquals( new CharPosition(0,3,3), stream.getPosition() );
    }

    @Test
    public void givenBytesThatOneElementAndSeperator_expectInProgress() {
        CharacterStream stream = new CharacterStream( "e1," );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
    }


    @Test
    public void givenBytesThatOneElementAndPartOfSeperator_expectInProgress() {
        CharacterStream stream = new CharacterStream( "e1a" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher("ab"), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
    }

    @Test
    public void givenBytesThatOneElementAndSeperator_thenEndStream_expectFailedMatch() {
        CharacterStream stream = new CharacterStream( "e1," ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'e1'", result.getFailedToMatchDescription() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
    }

    @Test
    public void givenBytesThatOneElementAndSeperatorThenElement_expectInProgress() {
        CharacterStream stream = new CharacterStream( "e1,e1" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
    }

    @Test
    public void givenBytesThatOneElementAndSeperatorThenElement_thenEndStream_expectFailedMatch() {
        CharacterStream stream = new CharacterStream( "e1,e1" ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "end of stream reached", result.getFailedToMatchDescription() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
    }

    @Test
    public void givenBytesThatOneElementAndSeperatorThenElement_thenEndOfListMatch_expectTwoElementResult() {
        CharacterStream stream = new CharacterStream( "e1,e1;" ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList("e1","e1"), result.getResult() );
        assertEquals( new CharPosition(0,6,6), stream.getPosition() );
    }

    @Test
    public void givenBytesThreeSeperatedElements_expectInProgress() {
        CharacterStream stream = new CharacterStream( "e1,e1,e1" );
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
    }

    @Test
    public void givenBytesThreeSeperatedElements_thenEndStream_expectFailedMatch() {
        CharacterStream stream = new CharacterStream( "e1,e1,e1" ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "end of stream reached", result.getFailedToMatchDescription() );
        assertEquals( new CharPosition(0,0,0), stream.getPosition() );
    }

    @Test
    public void givenBytesThreeSeperatedElements_thenEndOfListMatch_expectThreeElementResult() {
        CharacterStream stream = new CharacterStream( "e1,e1,e1;" ).appendEOS();
        Matcher<List<String>> matcher = new ListMatcher( new ConstantMatcher("+"), new ConstantMatcher("e1"), new ConstantMatcher(","), new ConstantMatcher(";") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList("e1","e1","e1"), result.getResult() );
        assertEquals( new CharPosition(0,9,9), stream.getPosition() );
    }

}
