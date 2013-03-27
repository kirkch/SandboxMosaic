package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import static com.mosaic.parsers.push.matchers.Matchers.constant;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class OrMatcherTest {

    @Test
    public void givenNullMatchersToConstructor_expectError() {
        try {
            new OrMatcher( null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'candidateMatchers' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenNoMatchersToConstructor_expectError() {
        try {
            new OrMatcher( new Matcher[] {} );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'candidateMatchers.length' (0) must be >= 1", e.getMessage() );
        }
    }

    @Test
    public void givenSingleMatcher_supplyMatch_expectMatch() {
        CharacterStream stream  = new CharacterStream( "a" );
        Matcher<String> matcher = new OrMatcher( constant("a") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertEquals( "a", result.getResult() );
    }

    @Test
    public void givenSingleMatcher_supplyIncomplete_expectIncomplete() {
        CharacterStream stream  = new CharacterStream( "a" );
        Matcher<String> matcher = new OrMatcher( constant("a1") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
    }

    @Test
    public void givenSingleMatcher_supplyFail_expectFail() {
        CharacterStream stream  = new CharacterStream( "z" );
        Matcher<String> matcher = new OrMatcher( constant("a1") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected to match: a1", result.getFailedToMatchDescription() );
    }

    @Test
    public void givenTwoMatchers_supplyStreamThatMatchesBoth_expectFirstMatcherToMatch() {
        CharacterStream stream  = new CharacterStream( "a1b" );
        Matcher<String> matcher = new OrMatcher( constant("a1"), constant("a1b") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "a1", result.getResult() );
        assertEquals( "b", stream.toString() );
    }

    @Test
    public void givenTwoMatchers_supplyStreamThatFailsFirstMatcherAndMatchesSecond_expectSecondMatcherToMatch() {
        CharacterStream stream  = new CharacterStream( "a2" );
        Matcher<String> matcher = new OrMatcher( constant("a1"), constant("a2") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "a2", result.getResult() );
        assertEquals( "", stream.toString() );
    }

    @Test
    public void givenTwoMatchers_supplyStreamThatIsIncompleteForFirstMatcherAndMatchesSecond_expectIncomplete() {
        CharacterStream stream  = new CharacterStream( "a1" );
        Matcher<String> matcher = new OrMatcher( constant("a1b"), constant("a1") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
    }

}
