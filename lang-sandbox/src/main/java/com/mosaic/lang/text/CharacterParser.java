package com.mosaic.lang.text;

/**
 * Receives text a character at a time.  When complete, the end product
 * may be requested.  For example an int parser may accept '123' and return 123.
 */
public interface CharacterParser<T> extends CharacterSink {

    public T getParsedData();

}
