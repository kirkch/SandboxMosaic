package com.mosaic.parsers.matchers;

import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;
import org.junit.Test;

import java.nio.CharBuffer;

import static com.mosaic.parsers.matchers.MatcherAsserts.assertIncompleteMatch;
import static com.mosaic.parsers.matchers.MatcherAsserts.assertMatch;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class WhitespaceAllMatcherTests {

    private Matcher spaceMatcher = WhitespaceMatcher.whitespaceMatcher();


    @Test
    public void giveEmptyString_expectPartialMatch() {
        MatchResult result = spaceMatcher.match("", false);

        assertIncompleteMatch(result);
    }

    @Test
    public void giveEmptyStringEOS_expectMatchedZeroCharacters() {
        MatchResult result = spaceMatcher.match("", true);

        assertMatch(result, 0, null);
    }

    @Test
    public void givenBlankString_expectPartialMatch() {
        MatchResult result = spaceMatcher.match("  \t ", false);

        assertIncompleteMatch(result);
    }

    @Test
    public void givenBlankStringEOS_expectMatchZeroCharacters() {
        MatchResult result = spaceMatcher.match("  \t ", true);

        assertMatch(result, 4, null);
    }

    @Test
    public void givenNoneBlankString_expectZeroCharactersMatched() {
        MatchResult result = spaceMatcher.match("Hello", true);

        assertMatch(result, 0, null);
    }

    @Test
    public void givenWordBlankWord_matchFromBeginning_expectZeroCharactersMatched() {
        MatchResult result = spaceMatcher.match("Hello  \t  World", true);

        assertMatch(result, 0, null);
    }

    @Test
    public void givenWordBlankWord_matchFromMidpointOfBlank_expectCharactersMatched() {
        CharBuffer buf = CharBuffer.wrap("Hello  \t  World");
        buf.position(7);

        MatchResult result = spaceMatcher.match(buf, true);

        assertMatch(result, 3, null);
        assertEquals( 10, buf.position() );
    }

    @Test
    public void givenWordBlankWord_matchLastCharacter_incompleteMatch() {
        CharBuffer buf = CharBuffer.wrap("Hello  \t  World");
        buf.position(15);

        MatchResult result = spaceMatcher.match(buf, false);

        assertIncompleteMatch(result);
        assertEquals( 15, buf.position() );
    }

    @Test
    public void givenWordBlankWord_matchLastCharacterEOS_incompleteMatch() {
        CharBuffer buf = CharBuffer.wrap("Hello  \t  World");
        buf.position(15);

        MatchResult result = spaceMatcher.match(buf, true);

        assertMatch(result, 0, null);
        assertEquals( 15, buf.position() );
    }

    @Test
    public void givenWordBlankWordWithNewLines_matchFromMidpointOfBlank_expectCharactersMatched() {
        CharBuffer buf = CharBuffer.wrap("Hello  \t\n\r  World");
        buf.position(7);

        MatchResult result = spaceMatcher.match(buf, true);

        assertMatch(result, 5, null);
        assertEquals( 12, buf.position() );
    }

}
