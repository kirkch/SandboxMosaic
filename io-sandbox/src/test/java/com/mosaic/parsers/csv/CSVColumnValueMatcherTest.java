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

    private Matcher matcher = new CSVColumnValueMatcher(',');


    @Test
    public void givenNoBytes_expectInprogress() {
        CharBuffer buf = CharBuffer.wrap("");

        MatchResult result = matcher.match(buf, false);

        assertEquals(0, result.getNumCharactersConsumed());
        assertTrue(result.isIncompleteMatch());
        assertNull( result.getParsedValue() );
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNoBytesEOS_expectFailedMatch() {
        CharBuffer buf = CharBuffer.wrap("");

        MatchResult result = matcher.match(buf, true);

        assertEquals(0, result.getNumCharactersConsumed());
        assertEquals("", result.getParsedValue().toString());
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNameOnly_expectIncomplete() {
        CharBuffer buf = CharBuffer.wrap("asset type");

        MatchResult result = matcher.match(buf, false);

        assertEquals(0, result.getNumCharactersConsumed());
        assertTrue(result.isIncompleteMatch());
        assertNull(result.getParsedValue());
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNameFollowedByComma_expectMatchUptoComma() {
        CharBuffer buf = CharBuffer.wrap("asset type,");

        MatchResult result = matcher.match(buf, false);

        assertEquals(10, result.getNumCharactersConsumed());
        assertEquals("asset type", result.getParsedValue().toString());
        assertEquals(10, buf.position());
    }

    @Test
    public void givenNameFollowedByWhitespaceAndComma_expectMatchUptoComma() {
        CharBuffer buf = CharBuffer.wrap("asset type   ,");

        MatchResult result = matcher.match(buf, false);

        assertEquals(13, result.getNumCharactersConsumed());
        assertEquals("asset type", result.getParsedValue().toString());
        assertEquals(13, buf.position());
    }

    @Test
    public void givenNameFollowedByEOS_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("asset type   ");

        MatchResult result = matcher.match(buf, true);

        assertEquals(13, result.getNumCharactersConsumed());
        assertEquals("asset type", result.getParsedValue().toString());
        assertEquals(13, buf.position());
    }

    @Test
    public void givenNameFollowedByEOL_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("asset type\n");

        MatchResult result = matcher.match(buf, false);

        assertEquals(10, result.getNumCharactersConsumed());
        assertEquals("asset type", result.getParsedValue().toString());
        assertEquals(10, buf.position());
    }

    @Test
    public void givenNameFollowedBySpacesThenEOL_expectMatchWithEOLLeftBehind() {
        CharBuffer buf = CharBuffer.wrap("asset type   \n");

        MatchResult result = matcher.match(buf, false);

        assertEquals(13, result.getNumCharactersConsumed());
        assertEquals("asset type", result.getParsedValue().toString());
        assertEquals(13, buf.position());
    }

    @Test
    public void givenNamePrefixedBySpacesThenEOL_expectMatchWithEOLLeftBehind() {
        CharBuffer buf = CharBuffer.wrap(" asset type   \n");

        MatchResult result = matcher.match(buf, false);

        assertEquals(14, result.getNumCharactersConsumed());
        assertEquals("asset type", result.getParsedValue().toString());
        assertEquals(14, buf.position());
    }

    @Test
    public void givenNameFollowedBySpacesThenEOS_expectMatchWithEOLLeftBehind() {
        CharBuffer buf = CharBuffer.wrap("  asset type   \n");

        MatchResult result = matcher.match(buf, true);

        assertEquals(15, result.getNumCharactersConsumed());
        assertEquals("asset type", result.getParsedValue().toString());
        assertEquals(15, buf.position());
    }

    @Test
    public void givenEOLByItself_expectBlankMatch() {
        CharBuffer buf = CharBuffer.wrap("\n");

        MatchResult result = matcher.match(buf, false);

        assertEquals(0, result.getNumCharactersConsumed());
        assertEquals("", result.getParsedValue().toString());
        assertEquals(0, buf.position());
    }

    @Test
    public void givenNameContainingCReturnFollowedByLineFeed_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("asset type\r\n");

        MatchResult result = matcher.match(buf, false);

        assertEquals(10, result.getNumCharactersConsumed());
        assertEquals("asset type", result.getParsedValue().toString());
        assertEquals(10, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOL_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("foo,bar");

        MatchResult result = matcher.match(buf, false);

        assertEquals(3, result.getNumCharactersConsumed());
        assertEquals("foo", result.getParsedValue().toString());
        assertEquals(3, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("foo,bar");

        MatchResult result = matcher.match(buf, true);

        assertEquals(3, result.getNumCharactersConsumed());
        assertEquals("foo", result.getParsedValue().toString());
        assertEquals(3, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_matchFromPlaceBeforeComma_expectEmptyMatch() {
        CharBuffer buf = CharBuffer.wrap("foo,bar");
        buf.position(3);

        MatchResult result = matcher.match(buf, true);

        assertEquals(0, result.getNumCharactersConsumed());
        assertEquals("", result.getParsedValue().toString());
        assertEquals(3, buf.position());
    }

    @Test
    public void givenTwoColumnsFollowedByEOS_matchFromPlaceAfterComma_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("foo,bar");
        buf.position(4);

        MatchResult result = matcher.match(buf, true);

        assertEquals(3, result.getNumCharactersConsumed());
        assertEquals("bar", result.getParsedValue().toString());
        assertEquals(7, buf.position());
    }

    @Test
    public void givenQuotedColumnEOL_expectMatchWithQuotesRemoved() {
        CharBuffer buf = CharBuffer.wrap("\"abc\"");

        MatchResult result = matcher.match(buf, true);

        assertEquals(5, result.getNumCharactersConsumed());
        assertEquals("abc", result.getParsedValue().toString());
        assertEquals(5, buf.position());
    }

    @Test
    public void givenQuotedColumnEOLPrefixedWithWhitespace_expectMatchWithQuotesRemoved() {
        CharBuffer buf = CharBuffer.wrap("   \"abc\"");

        MatchResult result = matcher.match(buf, true);

        assertEquals(8, result.getNumCharactersConsumed());
        assertEquals("abc", result.getParsedValue().toString());
        assertEquals(8, buf.position());
    }

    @Test
    public void givenQuotedColumnWithSpacesAtEnd_expectMatchWithQuotesRemoved() {
        CharBuffer buf = CharBuffer.wrap("\"abc\"   ");

        MatchResult result = matcher.match(buf, false);

        assertEquals(5, result.getNumCharactersConsumed());
        assertEquals("abc", result.getParsedValue().toString());
        assertEquals(5, buf.position());
    }

    @Test
    public void givenColumnWithEscapedCommaEOL_expectSingleColumnMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo, bar\"");

        MatchResult result = matcher.match(buf, false);

        assertEquals(10, result.getNumCharactersConsumed());
        assertEquals("foo, bar", result.getParsedValue().toString());
        assertEquals(10, buf.position());
    }

    @Test
    public void givenTwoEscapedColumns_matchFirstColumn() {
        CharBuffer buf = CharBuffer.wrap("\"foo\",\"bar\"");

        MatchResult result = matcher.match(buf, false);

        assertEquals(5, result.getNumCharactersConsumed());
        assertEquals("foo", result.getParsedValue().toString());
        assertEquals(5, buf.position());
    }

    @Test
    public void givenTwoEscapedColumns_matchSecondColumn() {
        CharBuffer buf = CharBuffer.wrap("\"foo\",\"bar\"");
        buf.position(6);

        MatchResult result = matcher.match(buf, false);

        assertEquals(5, result.getNumCharactersConsumed());
        assertEquals("bar", result.getParsedValue().toString());
        assertEquals(11, buf.position());
    }

    @Test
    public void givenTwoEscapedColumnsEOS_matchSecondColumn_expectMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo\",\"bar\"");
        buf.position(6);

        MatchResult result = matcher.match(buf, true);

        assertEquals(5, result.getNumCharactersConsumed());
        assertEquals("bar", result.getParsedValue().toString());
        assertEquals(11, buf.position());
    }

    @Test
    public void givenTwoEscapedColumnsWithWhitespace_matchFirstColumn() {
        CharBuffer buf = CharBuffer.wrap("  \" foo \" , \" bar \" ");

        MatchResult result = matcher.match(buf, false);

        assertEquals(9, result.getNumCharactersConsumed());
        assertEquals(" foo ", result.getParsedValue().toString());
        assertEquals(9, buf.position());
    }

    @Test
    public void givenTwoEscapedColumnsWithWhitespace_matchSecondColumn() {
        CharBuffer buf = CharBuffer.wrap("  \" foo \" , \" bar \" ");
        buf.position(11);

        MatchResult result = matcher.match(buf, false);

        assertEquals(8, result.getNumCharactersConsumed());
        assertEquals(" bar ", result.getParsedValue().toString());
        assertEquals(19, buf.position());
    }

    @Test
    public void givenColumnWithEscapedQuoteEOL_expectSingleColumnMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo\"\"bar\"");

        MatchResult result = matcher.match(buf, false);

        assertEquals(10, result.getNumCharactersConsumed());
        assertEquals("foo\"bar", result.getParsedValue().toString());
        assertEquals(10, buf.position());
    }

    @Test
    public void givenColumnWithQuoteMissingClosingQuoteAndEOS_expectFailedMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo, bar");

        MatchResult result = matcher.match(buf, true);

        assertEquals(0, result.getNumCharactersConsumed());
        assertTrue(result.isError());
        assertNull( result.getParsedValue() );
        assertEquals(0, buf.position());
        assertEquals( "expected csv column value to be closed by a quote", result.getErrorMessage() );
        assertEquals(0, result.getMatchIndexOnError());
    }

    @Test
    public void givenColumnValueStartingWithWhitespaceAndMissingClosingQuoteAndEOS_expectFailedMatch() {
        CharBuffer buf = CharBuffer.wrap("  \"foo, bar");

        MatchResult result = matcher.match(buf, true);

        assertEquals(0, result.getNumCharactersConsumed());
        assertTrue(result.isError());
        assertNull( result.getParsedValue() );
        assertEquals(0, buf.position());
        assertEquals( "expected csv column value to be closed by a quote", result.getErrorMessage() );
        assertEquals(2, result.getMatchIndexOnError());
    }

    @Test
    public void givenColumnWithQuoteMissingClosingQuoteButNotEOS_expectIncompleteMatch() {
        CharBuffer buf = CharBuffer.wrap("\"foo, bar");

        MatchResult result = matcher.match(buf, false);

        assertEquals(0, result.getNumCharactersConsumed());
        assertTrue(result.isIncompleteMatch());
        assertNull( result.getParsedValue() );
        assertEquals(0, buf.position());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
    }

    @Test
    public void givenColumnValueStartingWithWhitespaceAndMissingClosingQuoteButNotEOS_expectIncompleteMatch() {
        CharBuffer buf = CharBuffer.wrap("  \"foo, bar");

        MatchResult result = matcher.match(buf, false);

        assertEquals(0, result.getNumCharactersConsumed());
        assertTrue(result.isIncompleteMatch());
        assertNull( result.getParsedValue() );
        assertEquals(0, buf.position());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
    }



}

