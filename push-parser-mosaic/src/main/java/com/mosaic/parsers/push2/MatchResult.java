package com.mosaic.parsers.push2;

import com.mosaic.io.CharPosition;

/**
 *
 */
public class MatchResult<T> {

    public static <T> MatchResult<T> createHasResultStatus( Matcher<T> nextMatcher, T result ) {
        MatchResult<T> s = new MatchResult( nextMatcher );

        s.result = result;

        return s;
    }

    public static <T> MatchResult<T> createHasFailedStatus( Matcher<T> nextMatcher, CharPosition pos, String description, String...args ) {
        MatchResult<T> s = new MatchResult( nextMatcher );

        s.failedToMatchDescription = String.format(description, args);

        return s;
    }



    private MatchResult( Matcher<T> nextMatcher ) {
        this.nextMatcher = nextMatcher;
    }


    private Matcher<T> nextMatcher;
    private T          result;
    private String     failedToMatchDescription;


    public boolean hasResult() {
        return result != null;
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

    public Matcher<T> getNextMatcher() {
        return nextMatcher;
    }
}
