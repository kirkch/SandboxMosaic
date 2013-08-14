package com.mosaic.parsers.csv;


import java.nio.CharBuffer;

import com.mosaic.hammer.junit.Hammer;
import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static com.mosaic.parsers.matchers.MatcherAsserts.*;


/**
 *
 */
@RunWith(Hammer.class)
public class CSVColumnValueMatcherTest {

    private Matcher matcher = new CSVColumnValueMatcher(',');


    @Test
    public void givenNoBytes_expectInprogress() {
        CharBuffer buf = CharBuffer.wrap("");

        MatchResult result = matcher.match(buf, false);

        assertIncompleteMatch(result);
    }

    @Test
    public void givenNoBytesEOS_expectNoMatch() {
        CharBuffer buf = CharBuffer.wrap("");

        MatchResult result = matcher.match(buf, true);

        assertNoMatch(result);
    }

    @Test
    public void givenNameOnly_expectIncomplete() {
        CharBuffer buf = CharBuffer.wrap("asset type");

        MatchResult result = matcher.match(buf, false);

        assertIncompleteMatch(result);
    }

    @Test
    public void givenNameFollowedByComma_expectMatchUptoComma() {
        CharBuffer buf = CharBuffer.wrap("asset type,");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 10, "asset type");
        assertEquals(10, buf.position());
    }

    @Test
    public void givenNameFollowedByWhitespaceAndComma_expectMatchUptoComma() {
        CharBuffer buf = CharBuffer.wrap("asset type   ,");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 13, "asset type");
        assertEquals(13, buf.position());
    }

    @Test
    public void f() {
        CharBuffer buf = CharBuffer.wrap("h1,h2\n");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 2, "h1");
        assertEquals(2, buf.position());
    }

    @Test
    public void givenNameFollowedByEOS_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("asset type   ");

        MatchResult result = matcher.match(buf, true);

        assertMatch(result, 13, "asset type");
        assertEquals(13, buf.position());
    }

    @Test
    public void givenNameFollowedByEOL_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("asset type\n");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 10, "asset type");
        assertEquals(10, buf.position());
    }

    @Test
    public void givenNameFollowedBySpacesThenEOL_expectMatchWithEOLLeftBehind() {
        CharBuffer buf = CharBuffer.wrap("asset type   \n");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 13, "asset type");
        assertEquals(13, buf.position());
    }

    @Test
    public void givenNamePrefixedBySpacesThenEOL_expectMatchWithEOLLeftBehind() {
        CharBuffer buf = CharBuffer.wrap(" asset type   \n");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 14, "asset type");
        assertEquals(14, buf.position());
    }

    @Test
    public void givenNameFollowedBySpacesThenEOS_expectMatchWithEOLLeftBehind() {
        CharBuffer buf = CharBuffer.wrap("  asset type   \n");

        MatchResult result = matcher.match(buf, true);

        assertMatch(result, 15, "asset type");
        assertEquals(15, buf.position());
    }

    @Test
    public void givenEOLByItself_expectBlankMatch() {
        CharBuffer buf = CharBuffer.wrap("\n");

        MatchResult result = matcher.match(buf, false);
// todo review -- this is the one that causes repeatmatcher to loop
        assertMatch(result, 0, "");
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNameContainingCReturnFollowedByLineFeed_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("asset type\r\n");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 10, "asset type");
        assertEquals(10, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOL_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("foo,bar");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 3, "foo");
        assertEquals(3, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("foo,bar");

        MatchResult result = matcher.match(buf, true);

        assertMatch(result, 3, "foo");
        assertEquals(3, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_matchFromPlaceBeforeComma_expectEmptyMatch() {
        CharBuffer buf = CharBuffer.wrap("foo,bar");
        buf.position(3);

        MatchResult result = matcher.match(buf, true);

        assertMatch(result, 0, "");
        assertEquals(3, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_matchFromPlaceAfterComma_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("foo,bar");
        buf.position(4);

        MatchResult result = matcher.match(buf, true);

        assertMatch(result, 3, "bar");
        assertEquals(7, buf.position());
    }

    @Test
    public void givenQuotedColumnEOL_expectMatchWithQuotesRemoved() {
        CharBuffer buf = CharBuffer.wrap("\"abc\"");

        MatchResult result = matcher.match(buf, true);

        assertMatch(result, 5, "abc");
        assertEquals(5, buf.position());
    }

    @Test
    public void givenQuotedColumnEOLPrefixedWithWhitespace_expectMatchWithQuotesRemoved() {
        CharBuffer buf = CharBuffer.wrap("   \"abc\"");

        MatchResult result = matcher.match(buf, true);

        assertMatch(result, 8, "abc");
        assertEquals(8, buf.position());
    }

    @Test
    public void givenQuotedColumnWithSpacesAtEnd_expectMatchWithQuotesRemoved() {
        CharBuffer buf = CharBuffer.wrap("\"abc\"   ");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 5, "abc");
        assertEquals(5, buf.position());
    }

    @Test
    public void givenColumnWithEscapedCommaEOL_expectSingleColumnMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo, bar\"");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 10, "foo, bar");
        assertEquals(10, buf.position());
    }

    @Test
    public void givenTwoEscapedColumns_matchFirstColumn() {
        CharBuffer buf = CharBuffer.wrap("\"foo\",\"bar\"");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 5, "foo");
        assertEquals(5, buf.position());
    }

    @Test
    public void givenTwoEscapedColumns_matchSecondColumn() {
        CharBuffer buf = CharBuffer.wrap("\"foo\",\"bar\"");
        buf.position(6);

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 5, "bar");
        assertEquals(11, buf.position());
    }

    @Test
    public void givenTwoEscapedColumnsEOS_matchSecondColumn_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo\",\"bar\"");
        buf.position(6);

        MatchResult result = matcher.match(buf, true);

        assertMatch(result, 5, "bar");
        assertEquals(11, buf.position());
    }

    @Test
    public void givenTwoEscapedColumnsWithWhitespace_matchFirstColumn() {
        CharBuffer buf = CharBuffer.wrap("  \" foo \" , \" bar \" ");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 9, " foo ");
        assertEquals(9, buf.position());
    }

    @Test
    public void givenTwoEscapedColumnsWithWhitespace_matchSecondColumn() {
        CharBuffer buf = CharBuffer.wrap("  \" foo \" , \" bar \" ");
        buf.position(11);

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 8, " bar ");
        assertEquals(19, buf.position());
    }

    @Test
    public void givenColumnWithEscapedQuoteEOL_expectSingleColumnMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo\"\"bar\"");

        MatchResult result = matcher.match(buf, false);

        assertMatch(result, 10, "foo\"bar");
        assertEquals(10, buf.position());
    }

    @Test
    public void givenColumnWithQuoteMissingClosingQuoteAndEOS_expectFailedMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo, bar");

        MatchResult result = matcher.match(buf, true);

        assertError(result, 0, "expected csv column value to be closed by a quote");
        assertEquals(0, buf.position());
    }

    @Test
    public void givenColumnValueStartingWithWhitespaceAndMissingClosingQuoteAndEOS_expectFailedMatch() {
        CharBuffer buf = CharBuffer.wrap("  \"foo, bar");

        MatchResult result = matcher.match(buf, true);

        assertError(result, 2, "expected csv column value to be closed by a quote");
        assertEquals(0, buf.position());
    }

    @Test
    public void givenColumnWithQuoteMissingClosingQuoteButNotEOS_expectIncompleteMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo, bar");

        MatchResult result = matcher.match(buf, false);

        assertIncompleteMatch(result);
        assertEquals(0, buf.position());
    }

    @Test
    public void givenColumnValueStartingWithWhitespaceAndMissingClosingQuoteButNotEOS_expectIncompleteMatch() {
        CharBuffer buf = CharBuffer.wrap("  \"foo, bar");

        MatchResult result = matcher.match(buf, false);

        assertIncompleteMatch(result);
        assertEquals(0, buf.position());
    }

}

