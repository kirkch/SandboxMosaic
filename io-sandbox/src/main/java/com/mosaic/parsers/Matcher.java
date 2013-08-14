package com.mosaic.parsers;

import java.lang.reflect.Method;
import java.nio.CharBuffer;

/**
 * Matches characters supplied in a ByteBuffer.
 *
 * Matcher's are stateful, and thus must not be shared.
 */
public interface Matcher {

    /**
     * Consume as many characters as necessary to match a specified criteria.
     * Will return -2 when there is no match, -1 when the match is incomplete
     * due to lack of characters and >= 0 when the match was successful.
     *
     * @param buf the input buffer, characters are consumed
     * @return number of characters consumed
     */
    public MatchResult match( CharBuffer buf, boolean isEOS );


    public MatchResult match( String str, boolean isEOS );


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

    public String getCallbackMethodName();


    /**
     * Given the target instance for the callback, return the Method on the
     * targetInstance that should be invoked.
     *
     * This method is a little dirty being here; however it makes caching the
     * result simple.  A good performance benefit.
     */
    public Method resolveCallbackMethod( Object targetInstance );


    /**
     * The position of the input buffer from the last time that this matcher
     * was used.  Used to detect (and avoid) the infinite loops in the
     * matcher graph.
     */
    public int getBufferIndexFromPreviousCall();
    public void setBufferIndexFromPreviousCall( int pos );
}
