package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class EOFMatcherTest {

    @Test
    public void givenEmptyBytesWithEOS_expectMatch() {
        CharacterStream stream  = new CharacterStream("").appendEOS();
        Matcher<String> matcher = new EOFMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( null, result.getResult() );
    }

    @Test
    public void givenEmptyBytesWithoutEOS_expectIncomplete() {
        CharacterStream stream  = new CharacterStream("");
        Matcher<String> matcher = new EOFMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
    }

    @Test
    public void givenBytesWithoutEOS_expectNoMatch() {
        CharacterStream stream  = new CharacterStream("abc");
        Matcher<String> matcher = new EOFMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
    }

    @Test
    public void givenBytesWithEOS_expectNoMatch() {
        CharacterStream stream  = new CharacterStream("abc").appendEOS();
        Matcher<String> matcher = new EOFMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
    }

}
