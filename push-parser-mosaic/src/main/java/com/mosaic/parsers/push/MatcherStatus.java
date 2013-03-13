package com.mosaic.parsers.push;

import com.mosaic.io.CharPosition;

/**
 * Internal status of a Matcher. Used by implementations of the Matcher abstract class. It is created via static
 * factory methods on MatcherStatus and comes in three flavours: waiting for more input, has enough input but
 * errored and has successfully parsed a value.
 */
public class MatcherStatus<T> {

    private static MatcherStatus IS_PARSING = new MatcherStatus( false );

    public static <T> MatcherStatus<T> createIsParsingStatus() {
        return IS_PARSING;
    }

    public static <T> MatcherStatus<T> createHasResultStatus( T result ) {
        MatcherStatus<T> s = new MatcherStatus( true );

        s.result = result;

        return s;
    }

    public static <T> MatcherStatus<T> createHasFailedStatus( CharPosition pos, String description, String...args ) {
        MatcherStatus<T> s = new MatcherStatus( true );

        s.failedToMatchDescription = String.format(description, args);

        return s;
    }





    private MatcherStatus( boolean hasCompletedFlag ) {
        this.hasCompletedFlag = hasCompletedFlag;
    }



    private boolean hasCompletedFlag;
    private T       result;
    private String  failedToMatchDescription;


    public boolean hasCompleted() {
        return hasCompletedFlag;
    }

    public boolean isAwaitingInput() {
        return !hasCompletedFlag;
    }

    public boolean hasResult() {
        return hasCompletedFlag && result != null;
    }

    public boolean hasErrored() {
        return failedToMatchDescription != null;
    }

    public T getResult() {
        return result;
    }

    public String getFailedToMatchDescription() {
        return failedToMatchDescription;
    }
}
