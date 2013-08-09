package com.mosaic.parsers;

/**
 *
 */
public class MatchResult {

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


    public MatchResult setHasErroredState(int charBufferPosition, String errorMessage) {
        this.status            = STATUS_ERROR;
        this.errorMessage      = errorMessage;
        this.matchIndexOnError = charBufferPosition;

        return this;
    }
    
    public MatchResult setIncompleteMatchState() {
        this.status = STATUS_INCOMPLETE_MATCH;
        
        return this;
    }

    public MatchResult setHasMatchedState( int numCharsMatched, Object parsedValue ) {
        this.status                = STATUS_MATCHED;
        this.numCharactersConsumed = numCharsMatched;
        this.parsedValue           = parsedValue;
        
        return this;
    }


    public void clear() {
        parsedValue       = null;
        errorMessage      = null;
    }

    public boolean wasSuccessfulMatch() {
        return status == STATUS_MATCHED;
    }

    public boolean wasNoMatch() {
        return status == STATUS_NO_MATCH;
    }

    public boolean wasIncompleteMatch() {
        return status == STATUS_INCOMPLETE_MATCH;
    }

    public boolean wasError() {
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
