package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class SkipWhitespaceMatcherTest {

    @Test
    public void givenNullTargetString_expectException() {
        try {
            Matchers.skipWhitespace( null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBytes_expectFailedToMatch() {
        CharacterStream stream  = new CharacterStream("").appendEOS();
        Matcher<String> matcher = Matchers.skipWhitespace( Matchers.constant("a") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'a'", result.getFailedToMatchDescription() );
    }

    @Test
    public void givenMatchingBytes_expectMatch() {
        CharacterStream stream  = new CharacterStream("a");
        Matcher<String> matcher = Matchers.skipWhitespace( Matchers.constant("a") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertEquals( "a", result.getResult() );
        assertEquals( "", stream.toString() );
    }

    @Test
    public void givenMatchingBytesPrefixedWithWhitespace_expectMatch() {
        CharacterStream stream  = new CharacterStream("  a");
        Matcher<String> matcher = Matchers.skipWhitespace( Matchers.constant( "a" ) ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertEquals( "a", result.getResult() );

        assertTrue( result.hasResult() );
        assertEquals( "", stream.toString() );
    }

    @Test
    public void givenWhitespaceOnlyEOS_expectNoMatch() {
        CharacterStream stream  = new CharacterStream("  ").appendEOS();
        Matcher<String> matcher = Matchers.skipWhitespace( Matchers.constant("a") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'a'", result.getFailedToMatchDescription() );
        assertEquals( null, result.getNextMatcher() );
    }

    @Test
    public void givenWhitespace_expectWhitespaceToBeConsumedByMatcherToStillBeParsing() {
        CharacterStream stream  = new CharacterStream("  ");
        Matcher<String> matcher = Matchers.skipWhitespace( Matchers.constant( "a" ) ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertEquals( null, result.getResult() );
        assertTrue( result.isIncompleteMatch() );
    }

}
