package com.mosaic.parsers.matchers;

import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;
import org.junit.Test;

import java.nio.CharBuffer;

import static org.junit.Assert.*;

/**
 *
 */
public class WhitespaceMatcherTest {

    private Matcher spaceMatcher = WhitespaceMatcher.tabOrSpaceMatcher();


    @Test
    public void giveEmptyString_expectPartialMatch() {
        MatchResult result = spaceMatcher.match("", false);

        assertTrue( result.isIncompleteMatch() );
        assertNull( result.getParsedValue() );
        assertEquals( 0, result.getNumCharactersConsumed() );
    }

    @Test
    public void giveEmptyStringEOS_expectMatchedZeroCharacters() {
        MatchResult result = spaceMatcher.match("", true);

        assertTrue( result.isSuccessfulMatch() );
        assertNull( result.getParsedValue() );
        assertEquals( 0, result.getNumCharactersConsumed() );
    }

    @Test
    public void givenBlankString_expectPartialMatch() {
        MatchResult result = spaceMatcher.match("  \t ", false);

        assertTrue( result.isIncompleteMatch() );
        assertNull( result.getParsedValue() );
        assertEquals( 0, result.getNumCharactersConsumed() );
    }

    @Test
    public void givenBlankStringEOS_expectMatchZeroCharacters() {
        MatchResult result = spaceMatcher.match("  \t ", true);

        assertTrue( result.isSuccessfulMatch() );
        assertNull( result.getParsedValue() );
        assertEquals( 4, result.getNumCharactersConsumed() );
    }

    @Test
    public void givenNoneBlankString_expectZeroCharactersMatched() {
        MatchResult result = spaceMatcher.match("Hello", true);

        assertTrue( result.isSuccessfulMatch() );
        assertNull( result.getParsedValue() );
        assertEquals( 0, result.getNumCharactersConsumed() );
    }

    @Test
    public void givenWordBlankWord_matchFromBeginning_expectZeroCharactersMatched() {
        MatchResult result = spaceMatcher.match("Hello  \t  World", true);

        assertTrue( result.isSuccessfulMatch() );
        assertNull( result.getParsedValue() );
        assertEquals( 0, result.getNumCharactersConsumed() );
    }

    @Test
    public void givenWordBlankWord_matchFromMidpointOfBlank_expectCharactersMatched() {
        CharBuffer buf = CharBuffer.wrap("Hello  \t  World");
        buf.position(7);

        MatchResult result = spaceMatcher.match(buf, true);

        assertTrue( result.isSuccessfulMatch() );
        assertNull( result.getParsedValue() );
        assertEquals(  3, result.getNumCharactersConsumed() );
        assertEquals( 10, buf.position() );
    }

    @Test
    public void givenWordBlankWord_matchLastCharacter_incompleteMatch() {
        CharBuffer buf = CharBuffer.wrap("Hello  \t  World");
        buf.position(15);

        MatchResult result = spaceMatcher.match(buf, false);

        assertTrue( result.isIncompleteMatch() );
        assertNull( result.getParsedValue() );
        assertEquals(  0, result.getNumCharactersConsumed() );
        assertEquals( 15, buf.position() );
    }

    @Test
    public void givenWordBlankWord_matchLastCharacterEOS_incompleteMatch() {
        CharBuffer buf = CharBuffer.wrap("Hello  \t  World");
        buf.position(15);

        MatchResult result = spaceMatcher.match(buf, true);

        assertTrue( result.isSuccessfulMatch() );
        assertNull( result.getParsedValue() );
        assertEquals(  0, result.getNumCharactersConsumed() );
        assertEquals( 15, buf.position() );
    }

}
