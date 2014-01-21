package com.mosaic.parser.csv;

import com.mosaic.parser.graph.ParserListener;

import java.util.List;

/**
 *
 */
public interface CSVListener extends ParserListener {

    public void headers( int line, int col, List<String> headers );

    public void row( int line, int col, List<String> columns );

}
