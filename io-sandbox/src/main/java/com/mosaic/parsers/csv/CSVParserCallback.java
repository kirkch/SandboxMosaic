package com.mosaic.parsers.csv;

import java.util.List;

/**
 *
 */
public interface CSVParserCallback {

    public void start();

    public void headers( int line, List<String> headers );

    public void row( int line, List<String> columns );

    public void end();


    public void malformedRow( int line, int column, String message );

}
