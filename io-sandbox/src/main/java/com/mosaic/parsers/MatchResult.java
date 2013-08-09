package com.mosaic.parsers;

/**
 *
 */
public class MatchResult {

    /**
     * The number of characters consumed by the Matcher.  No need to set this directly
     * as BasePushParser will set it for you (using the value returned from the Matcher).
     */
    public int numCharactersConsumed;


    /**
     * Null means that the matched values are to be skipped over.  Usualy the
     * matched value will be a string wrapping the input byte buffer (and thus
     * have a limited life span as the buffer will probably get reused).  However
     * some matchers will aggregate together multiple strings, as well as convert
     * them.  Thus lists, integers, dates, and data beans will not be uncommon.
     */
    public Object parsedValue;

    /**
     * Extra information regarding the failed match.
     */
    public String errorMessage;

    /**
     * Offset from buf.position() for where the matcher had gotten up to when
     * the error was detected.
     */
    public int matchIndexOnError;


    public void reportError( int offset, String msg ) {
        this.errorMessage      = msg;
        this.matchIndexOnError = offset;
    }

    public void clear() {
        parsedValue       = null;
        errorMessage      = null;
        matchIndexOnError = 0;
    }


    public boolean wasSuccessfulMatch() {
        return numCharactersConsumed >= 0;
    }

    public boolean wasNoMatch() {
        return numCharactersConsumed == Matcher.NO_MATCH;
    }

    public boolean wasPartialMatch() {
        return numCharactersConsumed == Matcher.INCOMPLETE;
    }

    public boolean wasError() {
        return errorMessage != null;
    }
}
