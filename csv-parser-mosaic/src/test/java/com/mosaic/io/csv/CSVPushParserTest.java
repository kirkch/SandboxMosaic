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

        assertEquals( Arrays.asList(new String[] {}), delegate.audit );
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
        assertEquals( Arrays.asList(new String[] {}), delegate.audit );
    }

    @Test
    public void givenEmptyFewColumns_expectNoRowMatchYet() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount = parser.appendCharacters( Characters.wrapString("a,b") );

        assertEquals( 0, rowCount );
        assertEquals( Arrays.asList(new String[] {}), delegate.audit );
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
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount1 = parser.appendCharacters( Characters.wrapString("  h1 ,  h2,h3   ") );
        assertEquals( 0, rowCount1 );

        int rowCount2 = parser.appendCharacters( Characters.wrapString( "  \nc1,c2, c3 \nc4,c5,c6\n  c7, c8 , \t c9\t " ) );
        assertEquals( 3, rowCount2 );

        int rowCount3 = parser.appendEOS();
        assertEquals( 1, rowCount3 );

        assertEquals( Arrays.asList( new String[] {"parsingStarted", "headerRead(0,[h1,h2,h3])", "rowRead(1,[c1,c2,c3])", "rowRead(2,[c4,c5,c6])", "rowRead(3,[c7,c8,c9])", "parsingEnded"} ), delegate.audit );
    }

    @Test
    public void givenSeveralLinesInOneGo_expectHeaderAndTwoRowDelegateCalls() {
        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount1 = parser.appendCharacters( Characters.wrapString("  h1 ,  h2,h3   \nc1,c2, c3 \nc4,c5,c6\n  c7, c8 , \t c9\t\n") );
        assertEquals( 4, rowCount1 );

        assertEquals( Arrays.asList( new String[] {"parsingStarted", "headerRead(0,[h1,h2,h3])", "rowRead(1,[c1,c2,c3])", "rowRead(2,[c4,c5,c6])", "rowRead(3,[c7,c8,c9])"} ), delegate.audit );
    }

    @Test
    public void givenPartialFragmentFromYahooCSV_expectSuccessfulParse() {
        String csv = "name, symbol, stock exchange, ask, ask size, bid, bid size, open, days low, days high, previous close, last trade time, last trade date, market capitalization, p/e ratio, holdings value, volume, divident yield, average daily volume, divident per share, earnings per share, divdent pay date, notes\n" +
            "\"ANGLO AMERICAN\",\"AAL.L\",\"London\",1672.50,1,513,1671.9999,599,1645.9999,1644.50,1678.0001,1650.00,\"6:42am\",\"4/4/2013\",21.318B,N/A,-,1152415,38.11,3031098,628.87,-1.191,\"N/A\",\"-\"\n" +
            "\"ASSOCIAT BRIT FOO\",\"ABF.L\",\"London\",1920.9999,899,1920.0001,1,569,1931.00,1898.00,1931.00,1920.9999,\"6:44am\",\"4/4/2013\",15.155B,2732.57,-,292145,N/A,777768,0.00,0.703,\"N/A\",\"-\"\n" +
            "\"AGGREKO\",\"AGK.L\",\"London\",1787.0001,1,755,1786.00,463,1800.00,1783.00,1800.00,1787.9999,\"6:44am\",\"4/4/2013\",4.754B,1720.89,-,179757,N/A,899662,0.00,1.039,\"N/A\",\"-\"\n";


        CSVPushParser parser = new CSVPushParser( delegate );

        int rowCount1 = parser.appendCharacters( Characters.wrapString(csv) );
        assertEquals( 4, rowCount1 );

        int rowCount2 = parser.appendEOS();
        assertEquals( 0, rowCount2 );

        assertEquals( Arrays.asList(
            new String[] {
                "parsingStarted", "headerRead(0,[name,symbol,stock exchange,ask,ask size,bid,bid size,open,days low,days high,previous close,last trade time,last trade date,market capitalization,p/e ratio,holdings value,volume,divident yield,average daily volume,divident per share,earnings per share,divdent pay date,notes])",
                 "rowRead(1,[ANGLO AMERICAN,AAL.L,London,1672.50,1,513,1671.9999,599,1645.9999,1644.50,1678.0001,1650.00,6:42am,4/4/2013,21.318B,N/A,-,1152415,38.11,3031098,628.87,-1.191,N/A,-])",
                 "rowRead(2,[ASSOCIAT BRIT FOO,ABF.L,London,1920.9999,899,1920.0001,1,569,1931.00,1898.00,1931.00,1920.9999,6:44am,4/4/2013,15.155B,2732.57,-,292145,N/A,777768,0.00,0.703,N/A,-])",
                 "rowRead(3,[AGGREKO,AGK.L,London,1787.0001,1,755,1786.00,463,1800.00,1783.00,1800.00,1787.9999,6:44am,4/4/2013,4.754B,1720.89,-,179757,N/A,899662,0.00,1.039,N/A,-])",
                 "parsingEnded"
            } ), delegate.audit );
    }



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
