package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class DiscardMatcherTest {

    @Test
    public void givenNullWrappedMatcher_expectException() {
        try {
            new DiscardMatcher( null );
            fail( "Expected NullPointerException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenWrappedMatcherMatches_expectMatchToBeDiscarded() {
        CharacterStream stream  = new CharacterStream("const123");
        Matcher<String> matcher = new DiscardMatcher(new ConstantMatcher("const")).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( null, result.getResult() );
        assertEquals( "123", stream.toString() );
        assertEquals( null, result.getNextMatcher() );
    }

    @Test
    public void givenWrappedMatcherPartial_expectResultToBePartial() {
        CharacterStream stream  = new CharacterStream("cons");
        Matcher<String> matcher = new DiscardMatcher(new ConstantMatcher("const")).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
        assertEquals( "cons", stream.toString() );
        assertTrue( result.getNextMatcher() == matcher );
    }

    @Test
    public void givenWrappedMatcherFails_expectResultToAlsoFail() {
        CharacterStream stream  = new CharacterStream("consz");
        Matcher<String> matcher = new DiscardMatcher(new ConstantMatcher("const")).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "consz", stream.toString() );
        assertEquals( null, result.getNextMatcher() );
    }

}
