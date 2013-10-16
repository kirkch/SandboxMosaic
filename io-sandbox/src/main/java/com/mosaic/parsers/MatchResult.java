package com.mosaic.parsers;

import com.mosaic.lang.functional.Function1;

/**
 *
 */
public class MatchResult {

    public static MatchResult errored(int charBufferPosition, String errorMessage) {
        return new MatchResult(charBufferPosition,errorMessage);
    }

    public static MatchResult incompleteMatch() {
        return new MatchResult(STATUS_INCOMPLETE_MATCH);
    }

    public static MatchResult noMatch() {
        return new MatchResult(STATUS_NO_MATCH);
    }

    /**
     * A successful match where the parsed value has been kept.
     */
    public static MatchResult matched(int numCharsMatched, Object parsedValue) {
        return new MatchResult(numCharsMatched,parsedValue,true);
    }

    /**
     * A successful match where the parsed value has been skipped.
     */
    public static MatchResult matched(int numCharsMatched) {
        return new MatchResult(numCharsMatched,null,false);
    }

    /**
     * Instructs the Push Parser that this matcher wants to hand off control to
     * a child matcher.  Once the child is complete the specified continuation
     * (aka callback) will be invoked which gives the original matcher a chance
     * to get the results and continue.
     */
    public static MatchResult continuation(Matcher child, Function1<MatchResult, MatchResult> continuation) {
        return new MatchResult(child,continuation);
    }



    private static final int STATUS_NO_MATCH         = 0;
    private static final int STATUS_INCOMPLETE_MATCH = 1;
    private static final int STATUS_MATCHED          = 2;
    private static final int STATUS_ERROR            = 3;
    private static final int STATUS_CONTINUATION = 4;


    private int status;

    /**
     * The number of characters consumed by the Matcher.  No need to set this directly
     * as BasePushParser will set it for you (using the value returned from the Matcher).
     */
    private int numCharactersConsumed;


    /**
     * Null means that the matched values are to be skipped over.  Usualy the
     * matched value will be a string wrapping the input byte buffer (and thus
     * have a limited life span as the buffer will probably get reused).  However
     * some matchers will aggregate together multiple strings, as well as convert
     * them.  Thus lists, integers, dates, and data beans will not be uncommon.
     */
    private Object parsedValue;

    /**
     * Has the parsedValue been included, and is to be kept.  Used to signal that a
     * null or non-null parsedValue has value to the parent matcher.
     */
    private boolean hasParsedValue;

    /**
     * Extra information regarding the failed match.
     */
    private String errorMessage;

    /**
     * Offset from buf.position() for where the matcher had gotten up to when
     * the error was detected.
     */
    private int matchIndexOnError;


    private Matcher child;
    private Function1<MatchResult, MatchResult> continuation;




    private MatchResult(int charBufferPosition, String errorMessage) {
        this.status            = STATUS_ERROR;
        this.errorMessage      = errorMessage;
        this.matchIndexOnError = charBufferPosition;
    }
    

    private MatchResult( int status ) {
        this.status = status;
    }

    /**
     * hasParsedValue is required because a parsedValue of null may be valid as the result
     * from a matcher that is not to be ignored.
     */
    private MatchResult(int numCharsMatched, Object parsedValue, boolean hasParsedValue) {
        this.status                = STATUS_MATCHED;
        this.numCharactersConsumed = numCharsMatched;
        this.parsedValue           = parsedValue;
        this.hasParsedValue        = hasParsedValue;
    }

    private MatchResult( Matcher child, Function1<MatchResult,MatchResult> continuation ) {
        this.status       = STATUS_CONTINUATION;
        this.child        = child;
        this.continuation = continuation;
    }


    public boolean isMatch() {
        return status == STATUS_MATCHED;
    }

    public boolean isNoMatch() {
        return status == STATUS_NO_MATCH;
    }

    public boolean isIncompleteMatch() {
        return status == STATUS_INCOMPLETE_MATCH;
    }

    public boolean isError() {
        return status == STATUS_ERROR;
    }

    public boolean isContinuation() {
        return status == STATUS_CONTINUATION;
    }


    public MatchResult skipParsedValue() {
        return hasParsedValue ? matched(numCharactersConsumed) : this;
    }



    public int getMatchIndexOnError() {
        return matchIndexOnError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Object getParsedValue() {
        return parsedValue;
    }

    public boolean hasParsedValue() {
        return hasParsedValue;
    }

    public int getNumCharactersConsumed() {
        return numCharactersConsumed;
    }

    public Matcher getNextMatcher() {
        return child;
    }

    public Function1<MatchResult, MatchResult> getContinuation() {
        return continuation;
    }


    public String toString() {
        switch (status) {
            case STATUS_NO_MATCH:
                return "noMatch";
            case STATUS_INCOMPLETE_MATCH:
                return "incompleteMatch";
            case STATUS_MATCHED:
                return "match("+numCharactersConsumed+","+parsedValue+")";
            case STATUS_ERROR :
                return "error("+matchIndexOnError+","+errorMessage+")";
            case STATUS_CONTINUATION:
                return "continuation("+child+","+continuation+")";
            default:
                throw new UnsupportedOperationException("unknown status code: " + status);
        }
    }

}
