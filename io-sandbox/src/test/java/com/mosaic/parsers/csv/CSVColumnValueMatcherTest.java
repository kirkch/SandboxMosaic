package com.mosaic.parsers.csv;


import java.nio.CharBuffer;

import com.mosaic.hammer.junit.Hammer;
import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;



/**
 *
 */
@RunWith(Hammer.class)
public class CSVColumnValueMatcherTest {

    private MatchResult matchResultContainer = new MatchResult();


    @Test
    public void givenNoBytes_expectInprogress() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(Matcher.INCOMPLETE, numCharsMatched);
        assertNull( matchResultContainer.parsedValue );
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNoBytesEOS_expectFailedMatch() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("");

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(0, numCharsMatched);
        assertEquals("", matchResultContainer.parsedValue.toString());
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNameOnly_expectIncomplete() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("asset type");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(Matcher.INCOMPLETE, numCharsMatched);
        assertNull(matchResultContainer.parsedValue);
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNameFollowedByComma_expectMatchUptoComma() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("asset type,");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(10, numCharsMatched);
        assertEquals("asset type", matchResultContainer.parsedValue.toString());
        assertEquals(10, buf.position());
    }

    @Test
    public void givenNameFollowedByWhitespaceAndComma_expectMatchUptoComma() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("asset type   ,");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(13, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(13, buf.position());
    }

    @Test
    public void givenNameFollowedByEOS_expectMatch() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("asset type   ");

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(13, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(13, buf.position());
    }

    @Test
    public void givenNameFollowedByEOL_expectMatch() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("asset type\n");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(10, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(10, buf.position());
    }

    @Test
    public void givenNameFollowedBySpacesThenEOL_expectMatchWithEOLLeftBehind() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("asset type   \n");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(13, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(13, buf.position());
    }

    @Test
    public void givenNamePrefixedBySpacesThenEOL_expectMatchWithEOLLeftBehind() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap(" asset type   \n");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(14, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(14, buf.position());
    }

    @Test
    public void givenNameFollowedBySpacesThenEOS_expectMatchWithEOLLeftBehind() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("  asset type   \n");

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(15, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(15, buf.position());
    }

    @Test
    public void givenEOLByItself_expectBlankMatch() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("\n");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(0, numCharsMatched);
        assertEquals( "", matchResultContainer.parsedValue.toString() );
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNameContainingCReturnFollowedByLineFeed_expectMatch() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("asset type\r\n");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(10, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(10, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOL_expectMatch() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("foo,bar");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(3, numCharsMatched);
        assertEquals( "foo", matchResultContainer.parsedValue.toString() );
        assertEquals(3, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_expectMatch() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("foo,bar");

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(3, numCharsMatched);
        assertEquals( "foo", matchResultContainer.parsedValue.toString() );
        assertEquals(3, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_matchFromPlaceBeforeComma_expectEmptyMatch() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("foo,bar");
        buf.position(3);

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(0, numCharsMatched);
        assertEquals( "", matchResultContainer.parsedValue.toString() );
        assertEquals(3, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_matchFromPlaceAfterComma_expectMatch() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("foo,bar");
        buf.position(4);

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(3, numCharsMatched);
        assertEquals( "bar", matchResultContainer.parsedValue.toString() );
        assertEquals(7, buf.position());
    }

    @Test
    public void givenQuotedColumnEOL_expectMatchWithQuotesRemoved() {
        Matcher matcher = new CSVColumnValueMatcher(',');

        CharBuffer buf = CharBuffer.wrap("\"abc\"");

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(5, numCharsMatched);
        assertEquals( "abc", matchResultContainer.parsedValue.toString() );
        assertEquals(5, buf.position());
    }


//skip white space prefix
//    @Test
//    public void givenQuotedColumnWithSpacesAtEnd_expectMatchWithQuotesRemoved() {
//        CharacterStream stream  = new CharacterStream( "\"abc\"   " );
//        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );
//
//        MatchResult<String> result = matcher.processInput();
//
//        assertTrue( result.hasResult() );
//        assertEquals( "abc", result.getResult() );
//        assertEquals( "   ", stream.toString() );
//    }
//
//    @Test
//    public void givenColumnWithEscapedCommaEOL_expectSingleColumnMatch() {
//        CharacterStream stream  = new CharacterStream( "\"foo, bar\"" );
//        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );
//
//        MatchResult<String> result = matcher.processInput();
//
//        assertTrue( result.hasResult() );
//        assertEquals( "foo, bar", result.getResult() );
//        assertEquals( "", stream.toString() );
//    }
//
//    @Test
//    public void givenColumnWithEscapedQuoteEOL_expectSingleColumnMatch() {
//        CharacterStream stream  = new CharacterStream( "\"foo\"\"bar\"" );
//        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );
//
//        MatchResult<String> result = matcher.processInput();
//
//        assertTrue( result.hasResult() );
//        assertEquals( "foo\"bar", result.getResult() );
//        assertEquals( "", stream.toString() );
//    }
//
//    @Test
//    public void givenColumnWithEscapedQuoteMissingClosingQuoteAndEOS_expectFailedMatch() {
//        CharacterStream stream  = new CharacterStream( "\"foo bar" ).appendEOS();
//        Matcher<String> matcher = new CSVColumnValueMatcher().withInputStream( stream );
//
//        MatchResult<String> result = matcher.processInput();
//
//        assertTrue( result.hasFailedToMatch() );
//        assertEquals( "escaped column missing closing quote", result.getFailedToMatchDescription() );
//    }



}

