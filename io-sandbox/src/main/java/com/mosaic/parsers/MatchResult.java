package com.mosaic.parsers;

/**
 *
 */
public class MatchResult {

    public static MatchResult errored(int charBufferPosition, String errorMessage) {
        return new MatchResult(charBufferPosition,errorMessage);
    }

    public static MatchResult incompleteMatch() {
        return new MatchResult();
    }

    public static MatchResult matched(int numCharsMatched, Object parsedValue) {
        return new MatchResult(numCharsMatched,parsedValue);
    }



    private static int STATUS_NO_MATCH         = 0;
    private static int STATUS_INCOMPLETE_MATCH = 1;
    private static int STATUS_MATCHED          = 2;
    private static int STATUS_ERROR            = 3;


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
     * Extra information regarding the failed match.
     */
    private String errorMessage;

    /**
     * Offset from buf.position() for where the matcher had gotten up to when
     * the error was detected.
     */
    private int matchIndexOnError;





    private MatchResult(int charBufferPosition, String errorMessage) {
        this.status            = STATUS_ERROR;
        this.errorMessage      = errorMessage;
        this.matchIndexOnError = charBufferPosition;
    }
    

    private MatchResult() {
        this.status = STATUS_INCOMPLETE_MATCH;
    }

    private MatchResult(int numCharsMatched, Object parsedValue) {
        this.status                = STATUS_MATCHED;
        this.numCharactersConsumed = numCharsMatched;
        this.parsedValue           = parsedValue;
    }



    public boolean isSuccessfulMatch() {
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


    public int getMatchIndexOnError() {
        return matchIndexOnError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Object getParsedValue() {
        return parsedValue;
    }

    public int getNumCharactersConsumed() {
        return numCharactersConsumed;
    }

}
