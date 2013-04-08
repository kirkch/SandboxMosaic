package com.mosaic.io.csv;

import com.mosaic.io.Characters;
import com.mosaic.parsers.push.Matcher;
import com.mosaic.utils.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class CSVPushParserTest {

    CSVPushParserDelegateFake delegate = new CSVPushParserDelegateFake();

    @Test
    public void nullDelegate_expectException() {
        try {
            new CSVPushParser( null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'delegate' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyString_expectStartMatchingDelegateCall() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount = parser.appendCharacters( Characters.wrapString("") );

        assertEquals( Arrays.asList(new String[] {"parsingStarted"}), delegate.audit );
    }

    @Test
    public void givenEmptyString_expectZeroRowsMatched() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount = parser.appendCharacters( Characters.wrapString("") );

        assertEquals( 0, rowCount );
    }

    @Test
    public void givenBlankLine_expectNoRowMatch() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount = parser.appendCharacters( Characters.wrapString("\n") );

        assertEquals( 0, rowCount );
        assertEquals( Arrays.asList(new String[] {"parsingStarted"}), delegate.audit );
    }

    @Test
    public void givenEmptyFewColumns_expectNoRowMatchYet() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount = parser.appendCharacters( Characters.wrapString("a,b") );

        assertEquals( 0, rowCount );
        assertEquals( Arrays.asList(new String[] {"parsingStarted"}), delegate.audit );
    }

    @Test
    public void givenSingleColumnOnLine_expectRowMatch() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount = parser.appendCharacters( Characters.wrapString("header1\n") );


        assertEquals( Arrays.asList( new String[] {"parsingStarted", "headerRead(0,[header1])"} ), delegate.audit );
        assertEquals( 1, rowCount );
    }

    @Test
    public void givenFewColumnsEOL_expectRowCallOnDelegate() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount = parser.appendCharacters( Characters.wrapString("h1,h2,h3\n") );


        assertEquals( Arrays.asList( new String[] {"parsingStarted", "headerRead(0,[h1,h2,h3])"} ), delegate.audit );
        assertEquals( 1, rowCount );
    }

    @Test
    public void givenFewColumnsWithWhitespaceEOL_expectRowCallOnDelegate() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount = parser.appendCharacters( Characters.wrapString("  h1 ,  h2   , \th3   \n") );


        assertEquals( Arrays.asList( new String[] {"parsingStarted", "headerRead(0,[h1,h2,h3])"} ), delegate.audit );
        assertEquals( 1, rowCount );
    }

    @Test
    public void givenFewColumnsEOL_expect1MatchCountReturnedFromParserCall() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount = parser.appendCharacters( Characters.wrapString("  h1 ,  h2   , \th3   \n") );


        assertEquals( Arrays.asList( new String[] {"parsingStarted", "headerRead(0,[h1,h2,h3])"} ), delegate.audit );
        assertEquals( 1, rowCount );
    }

    @Test
    public void givenFewColumnsOverTwoCallsWithEOLInSecondCall_expectRowMatch() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount1 = parser.appendCharacters( Characters.wrapString("  h1 ,  h2  ") );
        int rowCount2 = parser.appendCharacters( Characters.wrapString(" , \th3   \n") );


        assertEquals( 0, rowCount1 );
        assertEquals( Arrays.asList( new String[] {"parsingStarted", "headerRead(0,[h1,h2,h3])"} ), delegate.audit );
        assertEquals( 1, rowCount2 );
    }

    @Test
    public void givenFewColumnsEOLTwice_expectTwoRowCallOnDelegate() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount = parser.appendCharacters( Characters.wrapString("  h1 ,  h2,h3\nc1,c2,c3\n  ") );


        assertEquals( 2, rowCount );
        assertEquals( Arrays.asList( new String[] {"parsingStarted", "headerRead(0,[h1,h2,h3])", "rowRead(1,[c1,c2,c3])"} ), delegate.audit );
    }

    @Test
    public void givenFewColumnsThenEOS_expectEndOfParsingCallOnDelegate() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount1 = parser.appendCharacters( Characters.wrapString("  h1 ,  h2,h3   ") );
        assertEquals( 0, rowCount1 );

        int rowCount2 = parser.appendEOS();

        assertEquals( 1, rowCount2 );
        assertEquals( Arrays.asList( new String[] {"parsingStarted", "headerRead(0,[h1,h2,h3])", "parsingEnded"} ), delegate.audit );
    }

    @Test
    public void givenFewColumnsThenEOSThenPassInSomeMoreCharacters_expectException() {
        CSVPushParser parser = new CSVPushParser( delegate );

        parser.appendCharacters( Characters.wrapString("  h1 ,  h2,h3   ") );
        parser.appendEOS();


        try {
            parser.appendCharacters( Characters.wrapString( "  \nc1,c2, c3 \n" ) );
            fail( "Expected IllegalStateException" );
        } catch (IllegalStateException e) {
            assertEquals( "cannot append to closed stream", e.getMessage() );
        }
    }

    @Test
    public void givenFewColumnsThenEOS_expectRowMatch() {
Matcher.setDebugEnabled(true);
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount1 = parser.appendCharacters( Characters.wrapString("  h1 ,  h2,h3   ") );
        assertEquals( 0, rowCount1 );

        int rowCount2 = parser.appendCharacters( Characters.wrapString( "  \nc1,c2, c3 \nc4,c5,c6\n  c7, c8 , \t c9\t " ) );
        assertEquals( 3, rowCount2 );

        int rowCount3 = parser.appendEOS();
        assertEquals( 1, rowCount3 );

        assertEquals( Arrays.asList( new String[] {"parsingStarted", "headerRead(0,[h1,h2,h3])", "rowRead(0,[c1,c2,c3])", "rowRead(0,[c4,c5,c6])", "rowRead(0,[c7,c8,c9])", "parsingEnded"} ), delegate.audit );
    }


    //
    //
    //
    //
    //
    //
    //
    // givenSeveralLinesInOneGo_expectHeaderAndTwoRowDelegateCalls
    // givenPartialFragmentFromYahooCSV_expectSuccessfulParse


    private class CSVPushParserDelegateFake implements CSVPushParserDelegate {
        public List<String> audit = new ArrayList();

        @Override
        public void parsingStarted() {
            audit.add( "parsingStarted" );
        }

        @Override
        public void headerRead( int lineNumber, List<String> headers ) {
            audit.add( "headerRead("+lineNumber+","+ StringUtils.concat( "[", headers.toArray(), ",", "]" ) + ")" );
        }

        @Override
        public void rowRead( int lineNumber, List<String> columns ) {
            audit.add( "rowRead("+lineNumber+","+ StringUtils.concat( "[", columns.toArray(), ",", "]" ) + ")" );
        }

        @Override
        public void parsingEnded() {
            audit.add( "parsingEnded" );
        }
    }
}
