package com.mosaic.parser;

import com.mosaic.io.CharPosition;


/**
 * The result of invoking a CharacterParser.
 */
public interface ParseResult<L,R> {
    public static <L,R> ParseResult<L,R> matchSucceeded( L matchType, R parsedValue, CharPosition from, CharPosition toExc ) {
        return new SuccessfulParseResult<>( matchType, parsedValue, from, toExc );
    }

    public static <L,R> ParseResult<L,R> matchFailed( L matchType, String errorMessage, CharPosition pos ) {
        return new FailedParseResult<>( matchType, errorMessage, pos );
    }

    /**
     * Returns true if the parse attempt was successful.
     */
    public boolean wasSuccessful();

    /**
     * Returns an enum describing what in the parsing grammer we were trying to parse.
     */
    public L getMatchedType();

    /**
     * Returns the domain value constructed from the text that was parsed.  This value will be
     * null if nothing was parsed, or if the CharacterParser does not construct anything that it
     * wants to keep.
     */
    public R getParsedValueNbl();

    /**
     * If the parse attempt was not successful then calling this method will return the reason why.
     *
     * @return null if wasSuccessful() returns true
     */
    public String getErrorMessageNbl();

    /**
     * The position in the source text where this parse attempt started parsing from.
     */
    public CharPosition getFrom();

    /**
     * The position in the source text where this parse attempt finished.  When the parse attempt
     * was not successful, and nothing was parsed from the source text then this value will equal
     * the from position.
     */
    public CharPosition getToExc();
}

class SuccessfulParseResult<L,R> implements ParseResult<L,R> {
    private L            matchedType;
    private R            parsedValue;
    private CharPosition from;
    private CharPosition toExc;

    public SuccessfulParseResult( L matchedType, R parsedValue, CharPosition from, CharPosition toExc ) {
        this.matchedType = matchedType;
        this.parsedValue = parsedValue;
        this.from        = from;
        this.toExc       = toExc;
    }

    public boolean wasSuccessful() {
        return true;
    }

    public L getMatchedType() {
        return matchedType;
    }

    public R getParsedValueNbl() {
        return parsedValue;
    }

    public String getErrorMessageNbl() {
        return null;
    }

    public CharPosition getFrom() {
        return from;
    }

    public CharPosition getToExc() {
        return toExc;
    }
}

class FailedParseResult<L,R> implements ParseResult<L,R> {
    private L            matchedType;
    private String       errorMessage;
    private CharPosition pos;

    public FailedParseResult( L matchedType, String errorMessage, CharPosition pos ) {
        this.matchedType  = matchedType;
        this.errorMessage = errorMessage;
        this.pos          = pos;
    }

    public boolean wasSuccessful() {
        return true;
    }

    public L getMatchedType() {
        return matchedType;
    }

    public R getParsedValueNbl() {
        return null;
    }

    public String getErrorMessageNbl() {
        return errorMessage;
    }

    public CharPosition getFrom() {
        return pos;
    }

    public CharPosition getToExc() {
        return pos;
    }
}