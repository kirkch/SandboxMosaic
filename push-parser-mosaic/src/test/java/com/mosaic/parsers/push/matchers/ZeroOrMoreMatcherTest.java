package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Matches the wrappedMatcher zero or more times. Returning a List of all matches. The matches are greedy, so they will
 * keep being sucked in until the wrapped matcher reports a failed match.
 */
public class ZeroOrMoreMatcherTest {

    @Test
    public void givenNullTargetString_expectException() {
        try {
            new ZeroOrMoreMatcher( null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBytes_expectIncompleteMatch() {
        CharacterStream stream  = new CharacterStream("");
        Matcher<String> matcher = new ZeroOrMoreMatcher(new ConstantMatcher("a")).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertTrue( result.getNextMatcher() == matcher );
        assertEquals( "", stream.toString() );
    }

    @Test
    public void givenPartialBytesForFirstMatch_expectIncompleteMatch() {
        CharacterStream stream  = new CharacterStream("a");
        Matcher<String> matcher = new ZeroOrMoreMatcher(new ConstantMatcher("a1")).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertTrue( result.getNextMatcher() == matcher );
        assertEquals( "a", stream.toString() );
    }

    @Test
    public void givenPartialBytesForSecondMatch_expectIncompleteMatch() {
        CharacterStream stream  = new CharacterStream("a1a");
        Matcher<String> matcher = new ZeroOrMoreMatcher(new ConstantMatcher("a1")).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertTrue( result.getNextMatcher() == matcher );
        assertEquals( "a1a", stream.toString() );
    }

    @Test
    public void givenBytesForFirstMatch_expectIncompleteMatch() {
        CharacterStream stream  = new CharacterStream("a1");
        Matcher<String> matcher = new ZeroOrMoreMatcher(new ConstantMatcher("a1")).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertTrue( result.getNextMatcher() == matcher );
        assertEquals( "a1", stream.toString() );
    }

    @Test
    public void givenNoMatchingBytes_expectEmptyListResult() {
        CharacterStream stream  = new CharacterStream("z");
        Matcher<String> matcher = new ZeroOrMoreMatcher(new ConstantMatcher("a1")).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList(), result.getResult() );
        assertTrue( result.getNextMatcher() == null );
        assertEquals( "z", stream.toString() );
    }

    @Test
    public void givenBytesForFirstMatchThenNonMatchingBytes_expectSingleElement() {
        CharacterStream stream  = new CharacterStream("a1z");
        Matcher<String> matcher = new ZeroOrMoreMatcher(new ConstantMatcher("a1")).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList("a1"), result.getResult() );
        assertTrue( result.getNextMatcher() == null );
        assertEquals( "z", stream.toString() );
    }

    @Test
    public void givenBytesForFirstTwoMatchesThenNonMatchingBytes_expectTwoElements() {
        CharacterStream stream  = new CharacterStream("a1a1z");
        Matcher<String> matcher = new ZeroOrMoreMatcher(new ConstantMatcher("a1")).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList("a1","a1"), result.getResult() );
        assertTrue( result.getNextMatcher() == null );
        assertEquals( "z", stream.toString() );
    }

    @Test
    public void givenBytesForFirstTwoMatchesThenEOS_expectTwoElements() {
        CharacterStream stream  = new CharacterStream("a1a1").appendEOS();
        Matcher<String> matcher = new ZeroOrMoreMatcher(new ConstantMatcher("a1")).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList("a1","a1"), result.getResult() );
        assertTrue( result.getNextMatcher() == null );
        assertEquals( "", stream.toString() );
    }

    @Test
    public void givenBytesForFirstTwoMatchesThenPatialMatchAndThenEOS_expectTwoElements() {
        CharacterStream stream  = new CharacterStream("a1a1a").appendEOS();
        Matcher<String> matcher = new ZeroOrMoreMatcher(new ConstantMatcher("a1")).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList("a1","a1"), result.getResult() );
        assertTrue( result.getNextMatcher() == null );
        assertEquals( "a", stream.toString() );
    }

}
