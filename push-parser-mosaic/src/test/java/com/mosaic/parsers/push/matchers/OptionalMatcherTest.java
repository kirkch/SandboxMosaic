package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import static com.mosaic.parsers.push.matchers.Matchers.constant;
import static org.junit.Assert.*;

/**
 *
 */
public class OptionalMatcherTest {

    @Test
    public void givenNullWrappedMatcher_expectException() {
        try {
            new OptionalMatcher( null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenNoMatch_expectMatchWithNullResult() {
        CharacterStream stream  = new CharacterStream( "b" );
        Matcher<String> matcher = new OptionalMatcher( constant("a1") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertNull( result.getResult() );
    }

    @Test
    public void givenMatch_expectMatchWithValue() {
        CharacterStream stream  = new CharacterStream( "a1" );
        Matcher<String> matcher = new OptionalMatcher( constant("a1") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "a1", result.getResult() );
    }

    @Test
    public void givenInprogress_expectInprogress() {
        CharacterStream stream  = new CharacterStream( "a" );
        Matcher<String> matcher = new OptionalMatcher( constant("a1") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
    }

}
