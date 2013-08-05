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

    private Matcher     matcher              = new CSVColumnValueMatcher(',');
    private MatchResult matchResultContainer = new MatchResult();


    @Test
    public void givenNoBytes_expectInprogress() {
        CharBuffer buf = CharBuffer.wrap("");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(Matcher.INCOMPLETE, numCharsMatched);
        assertNull( matchResultContainer.parsedValue );
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNoBytesEOS_expectFailedMatch() {
        CharBuffer buf = CharBuffer.wrap("");

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(0, numCharsMatched);
        assertEquals("", matchResultContainer.parsedValue.toString());
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNameOnly_expectIncomplete() {
        CharBuffer buf = CharBuffer.wrap("asset type");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(Matcher.INCOMPLETE, numCharsMatched);
        assertNull(matchResultContainer.parsedValue);
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNameFollowedByComma_expectMatchUptoComma() {
        CharBuffer buf = CharBuffer.wrap("asset type,");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(10, numCharsMatched);
        assertEquals("asset type", matchResultContainer.parsedValue.toString());
        assertEquals(10, buf.position());
    }

    @Test
    public void givenNameFollowedByWhitespaceAndComma_expectMatchUptoComma() {
        CharBuffer buf = CharBuffer.wrap("asset type   ,");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(13, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(13, buf.position());
    }

    @Test
    public void givenNameFollowedByEOS_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("asset type   ");

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(13, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(13, buf.position());
    }

    @Test
    public void givenNameFollowedByEOL_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("asset type\n");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(10, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(10, buf.position());
    }

    @Test
    public void givenNameFollowedBySpacesThenEOL_expectMatchWithEOLLeftBehind() {
        CharBuffer buf = CharBuffer.wrap("asset type   \n");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(13, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(13, buf.position());
    }

    @Test
    public void givenNamePrefixedBySpacesThenEOL_expectMatchWithEOLLeftBehind() {
        CharBuffer buf = CharBuffer.wrap(" asset type   \n");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(14, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(14, buf.position());
    }

    @Test
    public void givenNameFollowedBySpacesThenEOS_expectMatchWithEOLLeftBehind() {
        CharBuffer buf = CharBuffer.wrap("  asset type   \n");

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(15, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(15, buf.position());
    }

    @Test
    public void givenEOLByItself_expectBlankMatch() {
        CharBuffer buf = CharBuffer.wrap("\n");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(0, numCharsMatched);
        assertEquals( "", matchResultContainer.parsedValue.toString() );
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNameContainingCReturnFollowedByLineFeed_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("asset type\r\n");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(10, numCharsMatched);
        assertEquals( "asset type", matchResultContainer.parsedValue.toString() );
        assertEquals(10, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOL_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("foo,bar");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(3, numCharsMatched);
        assertEquals( "foo", matchResultContainer.parsedValue.toString() );
        assertEquals(3, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("foo,bar");

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(3, numCharsMatched);
        assertEquals( "foo", matchResultContainer.parsedValue.toString() );
        assertEquals(3, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_matchFromPlaceBeforeComma_expectEmptyMatch() {
        CharBuffer buf = CharBuffer.wrap("foo,bar");
        buf.position(3);

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(0, numCharsMatched);
        assertEquals( "", matchResultContainer.parsedValue.toString() );
        assertEquals(3, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_matchFromPlaceAfterComma_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("foo,bar");
        buf.position(4);

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(3, numCharsMatched);
        assertEquals( "bar", matchResultContainer.parsedValue.toString() );
        assertEquals(7, buf.position());
    }

    @Test
    public void givenQuotedColumnEOL_expectMatchWithQuotesRemoved() {
        CharBuffer buf = CharBuffer.wrap("\"abc\"");

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(5, numCharsMatched);
        assertEquals( "abc", matchResultContainer.parsedValue.toString() );
        assertEquals(5, buf.position());
    }


//todo skip white space prefix

    @Test
    public void givenQuotedColumnWithSpacesAtEnd_expectMatchWithQuotesRemoved() {
        CharBuffer buf = CharBuffer.wrap("\"abc\"   ");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(5, numCharsMatched);
        assertEquals( "abc", matchResultContainer.parsedValue.toString() );
        assertEquals(5, buf.position());
    }

    @Test
    public void givenColumnWithEscapedCommaEOL_expectSingleColumnMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo, bar\"");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(10, numCharsMatched);
        assertEquals( "foo, bar", matchResultContainer.parsedValue.toString() );
        assertEquals(10, buf.position());
    }

    @Test
    public void givenTwoEscapedColumns_matchFirstColumn() {
        CharBuffer buf = CharBuffer.wrap("\"foo\",\"bar\"");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(5, numCharsMatched);
        assertEquals( "foo", matchResultContainer.parsedValue.toString() );
        assertEquals(5, buf.position());
    }

    @Test
    public void givenTwoEscapedColumns_matchSecondColumn() {
        CharBuffer buf = CharBuffer.wrap("\"foo\",\"bar\"");
        buf.position(6);

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(5, numCharsMatched);
        assertEquals( "bar", matchResultContainer.parsedValue.toString() );
        assertEquals(11, buf.position());
    }

    @Test
    public void givenTwoEscapedColumnsEOS_matchSecondColumn_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo\",\"bar\"");
        buf.position(6);

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(5, numCharsMatched);
        assertEquals( "bar", matchResultContainer.parsedValue.toString() );
        assertEquals(11, buf.position());
    }

    @Test
    public void givenTwoEscapedColumnsWithWhitespace_matchFirstColumn() {
        CharBuffer buf = CharBuffer.wrap("  \" foo \" , \" bar \" ");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(9, numCharsMatched);
        assertEquals( " foo ", matchResultContainer.parsedValue.toString() );
        assertEquals(9, buf.position());
    }

    @Test
    public void givenTwoEscapedColumnsWithWhitespace_matchSecondColumn() {
        CharBuffer buf = CharBuffer.wrap("  \" foo \" , \" bar \" ");
        buf.position(11);

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(8, numCharsMatched);
        assertEquals( " bar ", matchResultContainer.parsedValue.toString() );
        assertEquals(19, buf.position());
    }

//    @Test  todo
    public void givenColumnWithEscapedQuoteEOL_expectSingleColumnMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo\\\"bar\"");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(10, numCharsMatched);
        assertEquals( "foo\"bar", matchResultContainer.parsedValue.toString() );
        assertEquals(10, buf.position());
    }

    @Test
    public void givenColumnWithQuoteMissingClosingQuoteAndEOS_expectFailedMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo, bar");

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(Matcher.NO_MATCH, numCharsMatched);
        assertNull( matchResultContainer.parsedValue );
        assertEquals(0, buf.position());
        assertEquals( "expected csv column value to be closed by a quote", matchResultContainer.errorMessage );
        assertEquals(0, matchResultContainer.matchIndexOnError);
    }

    @Test
    public void givenColumnValueStartingWithWhitespaceAndMissingClosingQuoteAndEOS_expectFailedMatch() {
        CharBuffer buf = CharBuffer.wrap("  \"foo, bar");

        int numCharsMatched = matcher.match(buf, matchResultContainer, true);

        assertEquals(Matcher.NO_MATCH, numCharsMatched);
        assertNull( matchResultContainer.parsedValue );
        assertEquals(0, buf.position());
        assertEquals( "expected csv column value to be closed by a quote", matchResultContainer.errorMessage );
        assertEquals(2, matchResultContainer.matchIndexOnError);
    }

    @Test
    public void givenColumnWithQuoteMissingClosingQuoteButNotEOS_expectIncompleteMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo, bar");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(Matcher.INCOMPLETE, numCharsMatched);
        assertNull( matchResultContainer.parsedValue );
        assertEquals(0, buf.position());
        assertNull( matchResultContainer.errorMessage );
        assertEquals(0, matchResultContainer.matchIndexOnError);
    }

    @Test
    public void givenColumnValueStartingWithWhitespaceAndMissingClosingQuoteButNotEOS_expectIncompleteMatch() {
        CharBuffer buf = CharBuffer.wrap("  \"foo, bar");

        int numCharsMatched = matcher.match(buf, matchResultContainer, false);

        assertEquals(Matcher.INCOMPLETE, numCharsMatched);
        assertNull( matchResultContainer.parsedValue );
        assertEquals(0, buf.position());
        assertNull( matchResultContainer.errorMessage );
        assertEquals(0, matchResultContainer.matchIndexOnError);
    }



}

