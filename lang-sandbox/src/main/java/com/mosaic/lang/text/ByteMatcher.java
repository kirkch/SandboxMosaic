package com.mosaic.lang.text;

import com.mosaic.io.bytes.InputBytes;


/**
 * Receives text a character at a time.  When complete, the end product
 * may be requested.  For example an int parser may accept '123' and return 123.<p/>
 *
 *
 */
public interface ByteMatcher<T> {

    /**
     * Parse the specified bytes.<p/>
     *
     * Due to their nature, parsers tend to loop over the same code over and over
     * and over again.  Maybe millions of times, or more.  As such small costs
     * become significant.  For this reason ParserResult is passed in as an OUT parameter,
     * thus supporting zero copy parsing.
     */
    public void parse( InputBytes source, long fromInc, long toExc, ParserResult<T> result );

}