package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import static com.mosaic.parsers.push.matchers.Matchers.constant;
import static com.mosaic.parsers.push.matchers.Matchers.discard;
import static org.junit.Assert.*;

/**
 *
 */
public class SequenceOneMatcherTest {

    @Test
    public void givenNullNoWrappedMatchers_expectException() {
        try {
            new SequenceOneMatcher();
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatchers.length' (0) must be >= 1", e.getMessage() );
        }
    }

    @Test
    public void givenNullWrappedMatchers_expectException() {
        try {
            new SequenceOneMatcher( null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatchers' is not allowed to be null", e.getMessage() );
        }
    }

    @Test
    public void givenSomeWrappedMatchersAndOneNull_expectException() {
        try {
            new SequenceOneMatcher( constant("a1"), null, constant("a3") );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatchers[1]' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenFirstFailsSecondMatches_expectFailedMatch() {
        CharacterStream stream  = new CharacterStream( "a2" );
        Matcher<String> matcher = new SequenceOneMatcher( constant("a1"), constant("a2") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'a1'", result.getFailedToMatchDescription() );
    }

    @Test
    public void givenFirstInprogressSecondMatches_expectInprogressMatch() {
        CharacterStream stream  = new CharacterStream( "a" );
        Matcher<String> matcher = new SequenceOneMatcher( constant("a1"), constant("a2") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
    }

    @Test
    public void givenFirstMatchesSecondFails_expectFailedMatch() {
        CharacterStream stream  = new CharacterStream( "a1b" );
        Matcher<String> matcher = new SequenceOneMatcher( constant("a1"), constant("a2") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'a2'", result.getFailedToMatchDescription() );
    }

    @Test
    public void givenFirstMatchesSecondInprogress_expectInprogressMatch() {
        CharacterStream stream  = new CharacterStream( "a1a" );
        Matcher<String> matcher = new SequenceOneMatcher( constant("a1"), constant("a2") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
    }

    @Test
    public void givenAllMatchWithResultValue_expectFirstMatchReturnedAsOverallResult() {
        CharacterStream stream  = new CharacterStream( "a1a2" );
        Matcher<String> matcher = new SequenceOneMatcher( constant("a1"), constant("a2") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "a1", result.getResult() );
    }

    @Test
    public void givenFirstHasValueSecondIsNullButAllMatch_expectFirstValueReturnedAsResult() {
        CharacterStream stream  = new CharacterStream( "a1a2" );
        Matcher<String> matcher = new SequenceOneMatcher( constant("a1"), discard(constant("a2")) ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "a1", result.getResult() );
    }

    @Test
    public void givenFirstReturnsNullMatchSecondHasValueButAllMatch_expectSecondResultReturnedAsOverallResult() {
        CharacterStream stream  = new CharacterStream( "a1a2" );
        Matcher<String> matcher = new SequenceOneMatcher( discard(constant("a1")), constant("a2") ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "a2", result.getResult() );
    }

    @Test
    public void givenAllReturnNullMatches_expectNullOverallMatch() {
        CharacterStream stream  = new CharacterStream( "a1a2" );
        Matcher<String> matcher = new SequenceOneMatcher( discard(constant("a1")), discard(constant("a2")) ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertNull( result.getResult() );
    }

}
