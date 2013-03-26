package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharacterStream;
import com.mosaic.lang.function.VoidFunction1;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class IssueCallbackMatcherTest {

    private static class CallbackMock implements VoidFunction1<String> {
        public int callbackCount;

        public void invoke( String arg ) {
            callbackCount++;
        }
    }

    private CallbackMock callbackMock = new CallbackMock();


    @Test
    public void givenNullWrappedMatcher_expectException() {
        try {
            new IssueCallbackMatcher( null, callbackMock );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenNullCallback_expectException() {
        try {
            new IssueCallbackMatcher( new ConstantMatcher("c"), null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'callback' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBytesEOS_expectFailedToMatchNoCallback() {
        CharacterStream stream  = new CharacterStream("").appendEOS();
        Matcher<String> matcher = new IssueCallbackMatcher( new ConstantMatcher("a"), callbackMock ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'a'", result.getFailedToMatchDescription() );
        assertEquals( 0, callbackMock.callbackCount );
        assertTrue( result.getNextMatcher() == null );
    }

    @Test
    public void givenEmptyBytes_expectInprogressNoCallback() {
        CharacterStream stream  = new CharacterStream("");
        Matcher<String> matcher = new IssueCallbackMatcher( new ConstantMatcher("a"), callbackMock ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertEquals( null, result.getFailedToMatchDescription() );
        assertEquals( 0, callbackMock.callbackCount );
        assertTrue( result.getNextMatcher() == matcher );
    }

    @Test
    public void givenMatch_expectCallback() {
        CharacterStream stream  = new CharacterStream("a");
        Matcher<String> matcher = new IssueCallbackMatcher( new ConstantMatcher("a"), callbackMock ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "a", result.getResult() );
        assertEquals( "", stream.toString() );
        assertEquals( null, result.getFailedToMatchDescription() );
        assertEquals( 1, callbackMock.callbackCount );
        assertTrue( result.getNextMatcher() == null );
    }

}
