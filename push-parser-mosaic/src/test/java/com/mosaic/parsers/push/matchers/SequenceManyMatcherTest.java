package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.mosaic.parsers.push.matchers.Matchers.constant;
import static com.mosaic.parsers.push.matchers.Matchers.discard;
import static org.junit.Assert.*;

/**
 *
 */
public class SequenceManyMatcherTest {

    @Test
    public void givenNullNoWrappedMatchers_expectException() {
        try {
            new SequenceManyMatcher();
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatchers.length' (0) must be >= 1", e.getMessage() );
        }
    }

    @Test
    public void givenNullWrappedMatchers_expectException() {
        try {
            new SequenceManyMatcher( null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatchers' is not allowed to be null", e.getMessage() );
        }
    }

    @Test
    public void givenSomeWrappedMatchersAndOneNull_expectException() {
        try {
            new SequenceManyMatcher( constant("a1"), null, constant("a3") );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatchers[1]' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenFirstFailsSecondMatches_expectFailedMatch() {
        CharacterStream       stream  = new CharacterStream( "a2" );
        Matcher<List<String>> matcher = new SequenceManyMatcher( constant("a1"), constant("a2") ).withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'a1'", result.getFailedToMatchDescription() );
    }

    @Test
    public void givenFirstInprogressSecondMatches_expectInprogressMatch() {
        CharacterStream       stream  = new CharacterStream( "a" );
        Matcher<List<String>> matcher = new SequenceManyMatcher( constant("a1"), constant("a2") ).withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
    }

    @Test
    public void givenFirstMatchesSecondFails_expectFailedMatch() {
        CharacterStream       stream  = new CharacterStream( "a1b" );
        Matcher<List<String>> matcher = new SequenceManyMatcher( constant("a1"), constant("a2") ).withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'a2'", result.getFailedToMatchDescription() );
    }

    @Test
    public void givenFirstMatchesSecondInprogress_expectInprogressMatch() {
        CharacterStream       stream  = new CharacterStream( "a1a" );
        Matcher<List<String>> matcher = new SequenceManyMatcher( constant("a1"), constant("a2") ).withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
    }

    @Test
    public void givenAllMatchWithResultValue_expectMatchWithValues() {
        CharacterStream       stream  = new CharacterStream( "a1a2" );
        Matcher<List<String>> matcher = new SequenceManyMatcher( constant("a1"), constant("a2") ).withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList( new String[] {"a1", "a2"} ), result.getResult() );
    }

    @Test
    public void givenFirstHasValueSecondIsNullButAllMatch_expectMatchWithValues() {
        CharacterStream       stream  = new CharacterStream( "a1a2" );
        Matcher<List<String>> matcher = new SequenceManyMatcher( constant("a1"), discard(constant("a2")) ).withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList(new String[] {"a1",null}), result.getResult() );
    }

}
