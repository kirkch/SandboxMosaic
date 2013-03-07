package com.mosaic.io.csv;

/**
 *
 */
public interface CSVPushParserDelegate {

    public void parsingStarted();


    public void headersRead( int lineNumber, String[] headers );

    public void rowRead( int lineNumber, String[] columns );


    public void parsingEnded();

}
