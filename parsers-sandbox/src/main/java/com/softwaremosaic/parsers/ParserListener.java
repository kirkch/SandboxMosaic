package com.softwaremosaic.parsers;

/**
 *
 */
public interface ParserListener {

    public void started();

    public void error( int line, int col, String message );

    public void finished();

}
