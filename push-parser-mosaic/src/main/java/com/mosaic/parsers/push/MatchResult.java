package com.mosaic.parsers.push;

/**
 *
 */
public class MatchResult<T> {

    public static <T> MatchResult<T> createHasResultStatus( Matcher<T> nextMatcher, T result ) {
        MatchResult<T> s = new MatchResult( nextMatcher );

        s.result = result;

        return s;
    }

    public static <T> MatchResult<T> createIncompleteMatch( Matcher<T> nextMatcher ) {
        return new MatchResult( nextMatcher );
    }

    public static <T> MatchResult<T> createHasFailedStatus( Matcher<T> nextMatcher, String description, String...args ) {
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


    /**
     * Successful match.
     */
    public boolean hasResult() {
        return result != null;
    }

    /**
     * Inconclusive. Match started but ran out of characters and is not at the end of the stream, so more characters
     * may come in which will change the result.
     */
    public boolean isIncompleteMatch() {
        return result == null && failedToMatchDescription == null;
    }

    /**
     * Not the match that we are looking for.
     */
    public boolean hasFailedToMatch() {
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

    @Override
    public String toString() {
        if ( hasResult() ) {
            return String.format("MatchResult(Success,'%s')".format(result.toString()));
        } else if ( isIncompleteMatch() ) {
            return "MatchResult(Incomplete)";
        } else {
            return String.format("MatchResult(Error,'%s')".format(failedToMatchDescription));
        }
    }
}
