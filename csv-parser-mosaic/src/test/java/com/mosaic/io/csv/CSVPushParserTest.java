package com.mosaic.io.csv;

import com.mosaic.io.Characters;
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

    //
    //
    //
    // givenFewColumnsEOL_expectRowCallOnDelegate
    // givenFewColumnsEOL_expect1MatchCountReturnedFromParserCall
    // givenFewColumnsOverTwoCallsWithEOLInSecondCall_expectRowMatch
    // givenFewColumnsEOLTwice_expectTwoRowCallOnDelegate
    // givenFewColumnsEOLTwice_expectTwoCountReturned
    // givenFewColumnsThenEOS_expectRowMatch
    // givenFewColumnsThenEOS_expectEndOfParsingCallOnDelegate
    // givenFewColumnsThenEOSThenPassInSomeMoreCharacters_expectException
    // givenFewColumnsThenEOS_expectRowMatch
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
