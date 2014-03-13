package com.mosaic.parsers.csv;

import com.mosaic.io.Characters;
import com.mosaic.parsers.PushParser;
import org.junit.Test;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class CSVPushParserTests {

    private CSVParserCallbackFake delegate = new CSVParserCallbackFake();
    private PushParser            parser   = new CSVPushParser( delegate );


    @Test
    public void givenNoEvents_expectAuditToBeEmpty() {
        assertEquals(Arrays.asList(), delegate.audit);
    }

    @Test
    public void pushOneSingleLetterValue_expectStartEventOnly() {
        push( "a", false );

        assertEquals(Arrays.asList("start"), delegate.audit);
    }

    @Test
    public void pushOneSingleLetterValueEOF_expectSingleHeaderSingleColumn() {
        push( "a", true );

        assertEquals(Arrays.asList("start", "headers(1,[a])", "end"), delegate.audit);
    }

    @Test
    public void pushTwoColumnsEOF_expectTwoHeadersColumn() {
        long numCharactersConsumed = push( "a,b", true );

        assertEquals( Arrays.asList("start","headers(1,[a, b])", "end"), delegate.audit );
        assertEquals( 3, numCharactersConsumed );
    }

    @Test
    public void pushTwoColumnsEOFWithWhiteSpace_expectTwoHeadersColumnAndNoWhitespace() {
        long numCharactersConsumed = push( "  \t  a  \t  ,  \t  b  \t  ", true );

        assertEquals( Arrays.asList("start","headers(1,[a, b])", "end"), delegate.audit );
        assertEquals( 23, numCharactersConsumed );
    }

    @Test
    public void pushTwoColumnsEOFWithWhiteSpaceOverTwoCalls_expectTwoHeadersColumnAndNoWhitespace() {
        long numCharactersConsumed1 = push( "a  ", false );
        long numCharactersConsumed2 = push( "a  ,b  ", true );

        assertEquals( Arrays.asList("start","headers(1,[a, b])", "end"), delegate.audit );
        assertEquals( 0, numCharactersConsumed1 );
        assertEquals( 7, numCharactersConsumed2 );
    }

    @Test
    public void pushThreeRowsAsOneString_expectHeaderAndTwoRows() {
        long count = push( "h1,h2\nr1a,r1b\nr2a,r2b\n", true );

        assertEquals( Arrays.asList("start","headers(1,[h1, h2])", "row(2,[r1a, r1b])", "row(3,[r2a, r2b])", "end"), delegate.audit );
        assertEquals( 22, count );
    }

    @Test
    public void givenPartialFragmentFromYahooCSV_expectSuccessfulParse() {
        String csv = "name, symbol, stock exchange, ask, ask sizeInBytes, bid, bid sizeInBytes, open, days low, days high, previous close, last trade time, last trade date, market capitalization, p/e ratio, holdings value, volume, divident yield, average daily volume, divident per share, earnings per share, divdent pay date, notes\n" +
                "\"ANGLO AMERICAN\",\"AAL.L\",\"London\",1672.50,1,513,1671.9999,599,1645.9999,1644.50,1678.0001,1650.00,\"6:42am\",\"4/4/2013\",21.318B,N/A,-,1152415,38.11,3031098,628.87,-1.191,\"N/A\",\"-\"\n" +
                "\"ASSOCIAT BRIT FOO\",\"ABF.L\",\"London\",1920.9999,899,1920.0001,1,569,1931.00,1898.00,1931.00,1920.9999,\"6:44am\",\"4/4/2013\",15.155B,2732.57,-,292145,N/A,777768,0.00,0.703,\"N/A\",\"-\"\n" +
                "\"AGGREKO\",\"AGK.L\",\"London\",1787.0001,1,755,1786.00,463,1800.00,1783.00,1800.00,1787.9999,\"6:44am\",\"4/4/2013\",4.754B,1720.89,-,179757,N/A,899662,0.00,1.039,\"N/A\",\"-\"\n";


        push(csv, true);

        assertEquals( Arrays.asList(
                new String[] {
                        "start",  "headers(1,[name, symbol, stock exchange, ask, ask sizeInBytes, bid, bid sizeInBytes, open, days low, days high, previous close, last trade time, last trade date, market capitalization, p/e ratio, holdings value, volume, divident yield, average daily volume, divident per share, earnings per share, divdent pay date, notes])",
                        "row(2,[ANGLO AMERICAN, AAL.L, London, 1672.50, 1, 513, 1671.9999, 599, 1645.9999, 1644.50, 1678.0001, 1650.00, 6:42am, 4/4/2013, 21.318B, N/A, -, 1152415, 38.11, 3031098, 628.87, -1.191, N/A, -])",
                        "row(3,[ASSOCIAT BRIT FOO, ABF.L, London, 1920.9999, 899, 1920.0001, 1, 569, 1931.00, 1898.00, 1931.00, 1920.9999, 6:44am, 4/4/2013, 15.155B, 2732.57, -, 292145, N/A, 777768, 0.00, 0.703, N/A, -])",
                        "row(4,[AGGREKO, AGK.L, London, 1787.0001, 1, 755, 1786.00, 463, 1800.00, 1783.00, 1800.00, 1787.9999, 6:44am, 4/4/2013, 4.754B, 1720.89, -, 179757, N/A, 899662, 0.00, 1.039, N/A, -])",
                        "end"
                } ), delegate.audit );
    }



    private long push(String string, boolean isEOF) {
        return parser.push(CharBuffer.wrap(string), isEOF);
    }
    // todo reader tests

}


class CSVParserCallbackFake implements CSVParserCallback {
    public List<String> audit = new ArrayList<String>();


    public void start() {
        audit.add( "start" );
    }

    public void headers(int line, List<String> headers) {
        audit.add( String.format("headers(%s,%s)",line,headers) );
        //StringUtils.concat( "[", headers.toArray(), ",", "]" )
    }

    public void row(int line, List<String> columns) {
        audit.add( String.format("row(%s,%s)",line,columns) );
    }

    public void malformedRow(int line, int column, String message) {
        audit.add( String.format("malformedRow(%s,%s,%s)",line,column,message) );
    }

    public void end() {
        audit.add( "end" );
    }
}