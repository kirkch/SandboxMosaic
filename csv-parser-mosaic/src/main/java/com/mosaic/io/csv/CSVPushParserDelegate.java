package com.mosaic.io.csv;

import java.util.List;

/**
 *
 */
public interface CSVPushParserDelegate {

    public void parsingStarted();


    public void headerRead( int lineNumber, List<String> headers );

    public void rowRead( int lineNumber, List<String> columns );


    public void parsingEnded();

}
