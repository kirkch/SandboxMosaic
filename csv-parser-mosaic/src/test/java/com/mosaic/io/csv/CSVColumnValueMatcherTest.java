package com.mosaic.io.csv;

import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class CSVColumnValueMatcherTest {

    @Test
    public void givenNoBytes_expectInprogress() {
        CharacterStream stream  = new CharacterStream( "" );
        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
    }

    @Test
    public void givenNameOnly_expectIncomplete() {
        CharacterStream stream  = new CharacterStream( "foo" );
        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.isIncompleteMatch() );
    }

    @Test
    public void givenNameFollowedByComma_expectMatchUptoComma() {
        CharacterStream stream  = new CharacterStream( "foo," );
        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "foo", result.getResult() );
        assertEquals( ",", stream.toString() );
    }

    @Test
    public void givenNameFollowedByWhitespaceAndComma_expectMatchUptoComma() {
        CharacterStream stream  = new CharacterStream( "foo," );
        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "foo", result.getResult() );
        assertEquals( ",", stream.toString() );
    }

    @Test
    public void givenNameFollowedByEOS_expectMatch() {
        CharacterStream stream  = new CharacterStream( "foo" ).appendEOS();
        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "foo", result.getResult() );
        assertEquals( "", stream.toString() );
    }

    @Test
    public void givenNameFollowedByEOL_expectMatch() {
        CharacterStream stream  = new CharacterStream( "foo\n" );
        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "foo", result.getResult() );
        assertEquals( "\n", stream.toString() );
    }

    @Test
    public void givenNameContainingCReturnFollowedByLineFeed_expectMatch() {
        CharacterStream stream  = new CharacterStream( "foo\r\n" );
        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "foo", result.getResult() );
        assertEquals( "\r\n", stream.toString() );
    }

    @Test
    public void givenTwoWordsSeperatedBySpaceFollowedByEOL_expectMatch() {
        CharacterStream stream  = new CharacterStream( "foo bar\n" );
        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "foo bar", result.getResult() );
        assertEquals( "\n", stream.toString() );
    }

    @Test
    public void givenTwoColumnsFollowedByEOL_expectMatch() {
        CharacterStream stream  = new CharacterStream( "foo,bar\n" );
        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "foo", result.getResult() );
        assertEquals( ",bar\n", stream.toString() );
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_expectMatch() {
        CharacterStream stream  = new CharacterStream( "foo,bar" ).appendEOS();
        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "foo", result.getResult() );
        assertEquals( ",bar", stream.toString() );
    }

    @Test
    public void givenQuotedColumnEOL_expectMatchWithQuotesRemoved() {
        CharacterStream stream  = new CharacterStream( "\"abc\"" );
        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( "abc", result.getResult() );
        assertEquals( "", stream.toString() );
    }


    //
    // givenColumnWithEscapedCommaEOL_expectSingleColumnMatch
    // givenColumnWithEscapedQuoteEOL_expectSingleColumnMatch
    // givenColumnWithEscapedQuoteMissingClosingQuoteAndEOS_expectFailedMatch


}
