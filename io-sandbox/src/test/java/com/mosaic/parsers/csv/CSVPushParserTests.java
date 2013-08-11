package com.mosaic.parsers.csv;

import com.mosaic.parsers.PushParser;
import org.junit.Test;

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
        assertEquals( Arrays.asList(), delegate.audit );
    }

    @Test
    public void pushOneSingleLetterValue_expectStartEventOnly() {
        parser.push( "a", false );

        assertEquals( Arrays.asList("start"), delegate.audit );
    }

    @Test
    public void pushOneSingleLetterValueEOF_expectSingleHeaderSingleColumn() {
        parser.push( "a", true );

        assertEquals( Arrays.asList("start","headers(1,[a])", "end"), delegate.audit );
    }

    @Test
    public void pushTwoColumnsEOF_expectTwoHeadersColumn() {
        long numCharactersConsumed = parser.push( "a,b", true );

        assertEquals( Arrays.asList("start","headers(1,[a, b])", "end"), delegate.audit );
        assertEquals( 3, numCharactersConsumed );
    }

    @Test
    public void pushTwoColumnsEOFWithWhiteSpace_expectTwoHeadersColumnAndNoWhitespace() {
        long numCharactersConsumed = parser.push( "  \t  a  \t  ,  \t  b  \t  ", true );

        assertEquals( Arrays.asList("start","headers(1,[a, b])", "end"), delegate.audit );
        assertEquals( 23, numCharactersConsumed );
    }

    @Test
    public void pushTwoColumnsEOFWithWhiteSpaceOverTwoCalls_expectTwoHeadersColumnAndNoWhitespace() {
        long numCharactersConsumed1 = parser.push( "a  ", false );
        long numCharactersConsumed2 = parser.push( "a  ,b  ", true );

        assertEquals( Arrays.asList("start","headers(1,[a, b])", "end"), delegate.audit );
        assertEquals( 0, numCharactersConsumed1 );
        assertEquals( 7, numCharactersConsumed2 );
    }

    // todo multi line tests
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