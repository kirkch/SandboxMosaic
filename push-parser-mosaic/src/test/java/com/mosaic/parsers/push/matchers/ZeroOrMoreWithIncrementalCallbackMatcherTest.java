package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mosaic.parsers.push.matchers.Matchers.constant;
import static com.mosaic.parsers.push.matchers.Matchers.skipWhitespace;
import static org.junit.Assert.*;

/**
 * Matches the wrappedMatcher zero or more times. Returning a List of all matches. The matches are greedy, so they will
 * keep being sucked in until the wrapped matcher reports a failed match.
 */
public class ZeroOrMoreWithIncrementalCallbackMatcherTest {

    private ZeroOrMoreCallbackFake callback = new ZeroOrMoreCallbackFake();

    @Test
    public void givenNullTargetString_expectException() {
        try {
            new ZeroOrMoreWithIncrementalCallbackMatcher( null, callback );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenNullCallback_expectException() {
        try {
            new ZeroOrMoreWithIncrementalCallbackMatcher( constant("a"), null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'callback' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBytes_expectIncompleteMatch() {
        CharacterStream stream  = new CharacterStream("");
        Matcher<String> matcher = new ZeroOrMoreWithIncrementalCallbackMatcher(new ConstantMatcher("a"), callback).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertTrue( result.getNextMatcher() == matcher );
        assertEquals( "", stream.toString() );

        assertEquals( Arrays.<String>asList(), callback.audit );
    }

    @Test
    public void givenPartialBytesForFirstMatch_expectIncompleteMatch() {
        CharacterStream stream  = new CharacterStream("a");
        Matcher<String> matcher = new ZeroOrMoreWithIncrementalCallbackMatcher(new ConstantMatcher("a1"), callback).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertTrue( result.getNextMatcher() == matcher );
        assertEquals( "a", stream.toString() );

        assertEquals( Arrays.<String>asList(), callback.audit );
    }

    @Test
    public void givenPartialBytesForSecondMatch_expectIncompleteMatch() {
        CharacterStream stream  = new CharacterStream("a1a");
        Matcher<String> matcher = new ZeroOrMoreWithIncrementalCallbackMatcher(new ConstantMatcher("a1"), callback).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertTrue( result.getNextMatcher() == matcher );
        assertEquals( "a", stream.toString() );

        assertEquals( Arrays.asList("start(0)", "value(0,'a1')"), callback.audit );
    }

    @Test
    public void givenPartialBytesForSecondMatchAcrossMultipleLines_expectIncompleteMatch() {
        CharacterStream stream  = new CharacterStream("\na1\na1a");
        Matcher<String> matcher = new ZeroOrMoreWithIncrementalCallbackMatcher(skipWhitespace(new ConstantMatcher("a1")), callback).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertTrue( result.getNextMatcher() == matcher );
        assertEquals( "a", stream.toString() );

        assertEquals( Arrays.asList("start(0)", "value(0,'a1')", "value(1,'a1')"), callback.audit );
    }

    @Test
    public void givenBytesForFirstMatch_expectIncompleteMatch() {
        CharacterStream stream  = new CharacterStream("a1");
        Matcher<String> matcher = new ZeroOrMoreWithIncrementalCallbackMatcher(new ConstantMatcher("a1"), callback).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertTrue( result.getNextMatcher() == matcher );
        assertEquals( "", stream.toString() );

        assertEquals( Arrays.asList("start(0)", "value(0,'a1')"), callback.audit );
    }

    @Test
    public void givenNoMatchingBytes_expectEmptyListResult() {
        CharacterStream stream  = new CharacterStream("z");
        Matcher<String> matcher = new ZeroOrMoreWithIncrementalCallbackMatcher(new ConstantMatcher("a1"), callback).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( null, result.getResult() );
        assertTrue( result.getNextMatcher() == null );
        assertEquals( "z", stream.toString() );

        assertEquals( Arrays.<String>asList(), callback.audit );
    }

    @Test
    public void givenBytesForFirstMatchThenNonMatchingBytes_expectSingleElement() {
        CharacterStream stream  = new CharacterStream("a1z");
        Matcher<String> matcher = new ZeroOrMoreWithIncrementalCallbackMatcher(new ConstantMatcher("a1"), callback).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( null, result.getResult() );
        assertTrue( result.getNextMatcher() == null );
        assertEquals( "z", stream.toString() );

        assertEquals( Arrays.asList("start(0)", "value(0,'a1')", "end(0)"), callback.audit );
    }

    @Test
    public void givenBytesForFirstTwoMatchesThenNonMatchingBytes_expectTwoElements() {
        CharacterStream stream  = new CharacterStream("a1a1z");
        Matcher<String> matcher = new ZeroOrMoreWithIncrementalCallbackMatcher(new ConstantMatcher("a1"), callback).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertNull( result.getResult() );
        assertTrue( result.getNextMatcher() == null );
        assertEquals( "z", stream.toString() );

        assertEquals( Arrays.asList("start(0)", "value(0,'a1')", "value(0,'a1')", "end(0)"), callback.audit );
    }

    @Test
    public void givenBytesForFirstTwoMatchesThenEOS_expectTwoElements() {
        CharacterStream stream  = new CharacterStream("a1a1").appendEOS();
        Matcher<String> matcher = new ZeroOrMoreWithIncrementalCallbackMatcher(new ConstantMatcher("a1"), callback).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( null, result.getResult() );
        assertTrue( result.getNextMatcher() == null );
        assertEquals( "", stream.toString() );

        assertEquals( Arrays.asList("start(0)", "value(0,'a1')", "value(0,'a1')", "end(0)"), callback.audit );
    }

    @Test
    public void givenBytesForFirstTwoMatchesThenPatialMatchAndThenEOS_expectTwoElements() {
        CharacterStream stream  = new CharacterStream("a1a1a").appendEOS();
        Matcher<String> matcher = new ZeroOrMoreWithIncrementalCallbackMatcher(new ConstantMatcher("a1"), callback).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( null, result.getResult() );
        assertTrue( result.getNextMatcher() == null );
        assertEquals( "a", stream.toString() );

        assertEquals( Arrays.asList("start(0)", "value(0,'a1')", "value(0,'a1')", "end(0)"), callback.audit );
    }


    private class ZeroOrMoreCallbackFake implements ZeroOrMoreCallback<String> {
        public List<String> audit = new ArrayList(8);

        @Override
        public void startOfBlockReceived( int lineNumber ) {
            audit.add("start("+lineNumber+")");
        }

        @Override
        public void valueReceived( int lineNumber, String value ) {
            audit.add("value("+lineNumber+",'"+value+"')");
        }

        @Override
        public void endOfBlockReceived( int lineNumber ) {
            audit.add("end("+lineNumber+")");
        }

    }
}
