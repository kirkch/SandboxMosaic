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
    private PushParser            parser   = new CSVPushParser( "UTF-8", delegate );


    @Test
    public void pushSOF_expectOpenCloseCallsOnCallback() {
        parser.pushSOF();

        assertEquals( Arrays.asList("start"), delegate.audit );
    }

    @Test
    public void pushSOFEOF_expectOpenCloseCallsOnCallback() {
        parser.pushSOF();
        parser.pushEOF();

        assertEquals( Arrays.asList("start", "end"), delegate.audit );
    }

    @Test
    public void pushOneSingleLetterValue_expectNoResponse() {
        parser.push( "a" );

        assertEquals( 0, delegate.audit.size() );
    }

//    @Test TODO
    public void pushOneSingleLetterValueEOF_expectSingleHeaderSingleColumn() {
        parser.push( "a" );
        parser.pushEOF();

        assertEquals( Arrays.asList("headers(1,[a])", "end"), delegate.audit );
    }

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