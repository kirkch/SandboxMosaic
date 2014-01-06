package com.softwaremosaic.parsers.automata.csv;

import com.softwaremosaic.parsers.ParserListener;

import java.util.List;

/**
 *
 */
public interface CSVListener extends ParserListener {

    public void headers( int line, int col, List<String> headers );

    public void row( int line, int col, List<String> columns );

}
