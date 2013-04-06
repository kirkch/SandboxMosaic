package com.mosaic.parsers.push.matchers;

import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

/**
 *
 */
public class EOFMatcher<T> extends Matcher<T> {

    @Override
    protected MatchResult<T> _processInput() {
        if ( inputStream.isAtEOS() ) {
            return createHasResultStatus( null );
        } else if ( inputStream.length() == 0 ) {
            return createIncompleteMatch();
        } else {
            return createHasFailedStatus( "expected EOF" );
        }
    }

    public String toString() {
        return String.format("EOF");
    }
}
