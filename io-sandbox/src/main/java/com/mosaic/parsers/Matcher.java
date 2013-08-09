package com.mosaic.parsers;

import java.nio.CharBuffer;

/**
 * Matches characters supplied in a ByteBuffer.
 */
public interface Matcher {

    public static int NO_MATCH   = -2;
    public static int INCOMPLETE = -1;

    /**
     * Consume as many characters as necessary to match a specified criteria.
     * Will return -2 when there is no match, -1 when the match is incomplete
     * due to lack of characters and >= 0 when the match was successful.
     *
     * @param buf the input buffer, characters are consumed
     * @return number of characters consumed
     */
    public MatchResult match( CharBuffer buf, boolean isEOS );


    /**
     * Name of the value that this matcher matches.  Named matchers provide
     * better error and debug reports.  If a matcher is named then it is also
     * a hint to the push parser to hold onto the result of this matcher rather
     * than to skip it.
     */
    public Matcher withName( String name );

    /**
     * Name of the method to call when this matcher has matched a value. Note
     * that it is not the job of the matcher to make this call, this method only
     * stores the method name.
     */
    public Matcher withCallback( String callbackMethodName );


    public String getName();

    public String getCallback();

}
