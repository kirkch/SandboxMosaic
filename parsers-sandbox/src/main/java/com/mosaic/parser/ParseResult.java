package com.mosaic.parser;

import com.mosaic.io.CharPosition;
import com.mosaic.lang.functional.Function1;


/**
 * The result of invoking a CharacterParser.
 */
public interface ParseResult<R> {
    public static <R> ParseResult<R> matchSucceeded( R parsedValue, CharPosition from, CharPosition toExc ) {
        return new SuccessfulParseResult<>( parsedValue, from, toExc );
    }

    public static <R> ParseResult<R> matchFailed( String errorMessage, CharPosition pos ) {
        return new FailedParseResult<>( errorMessage, pos );
    }

    public static <R> ParseResult<R> noMatch( CharPosition pos ) {
        return new NoMatchParseResult<>( pos );
    }

    /**
     * Returns true if the parse attempt did not error.  That is, it matched or reported a no-match.
     * That is, it did not start to match a statement and then failed part way through.
     */
    public boolean successful();

    /**
     * Returns true if the parse attempt was matched a value.
     */
    public boolean matched();

    /**
     * Returns true if the parse attempt did not match a value and did not error.
     */
    public boolean noMatch();

    /**
     * Returns true if the parse attempt errored.
     */
    public boolean errored();

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


    /**
     * Converts a matched value to a new value.  If the result does not contain a matched value, and
     * is an error instead then the error will be left untouched.
     */
    public <B> ParseResult<B> map( Function1<R,B> mappingFunction );

}

class SuccessfulParseResult<R> implements ParseResult<R> {
    private R            parsedValue;
    private CharPosition from;
    private CharPosition toExc;

    public SuccessfulParseResult( R parsedValue, CharPosition from, CharPosition toExc ) {
        this.parsedValue = parsedValue;
        this.from        = from;
        this.toExc       = toExc;

        if ( parsedValue instanceof HasCharPosition ) {
            ((HasCharPosition) parsedValue).setPosition(from, toExc);
        }
    }

    public boolean successful() {
        return true;
    }

    public boolean matched() {
        return true;
    }

    public boolean noMatch() {
        return false;
    }

    public boolean errored() {
        return false;
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

    public String toString() {
        return "Matched";
    }

    public <B> ParseResult<B> map( Function1<R,B> mappingFunction ) {
        B mappedValue = mappingFunction.invoke( parsedValue );

        return new SuccessfulParseResult<>( mappedValue, from, toExc );
    }
}

@SuppressWarnings("unchecked")
class FailedParseResult<R> implements ParseResult<R> {
    private String       errorMessage;
    private CharPosition pos;

    public FailedParseResult( String errorMessage, CharPosition pos ) {
        this.errorMessage = errorMessage;
        this.pos          = pos;
    }

    public boolean successful() {
        return false;
    }

    public boolean matched() {
        return false;
    }

    public boolean noMatch() {
        return false;
    }

    public boolean errored() {
        return true;
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

    public String toString() {
        return "FailedMatch("+pos+": "+errorMessage+")";
    }

    public <B> ParseResult<B> map( Function1<R,B> mappingFunction ) {
        return (ParseResult<B>) this;
    }
}

@SuppressWarnings("unchecked")
class NoMatchParseResult<R> implements ParseResult<R> {
    private CharPosition pos;

    public NoMatchParseResult( CharPosition pos ) {
        this.pos = pos;
    }

    public boolean successful() {
        return true;
    }

    public boolean matched() {
        return false;
    }

    public boolean noMatch() {
        return true;
    }

    public boolean errored() {
        return false;
    }

    public R getParsedValueNbl() {
        return null;
    }

    public String getErrorMessageNbl() {
        return null;
    }

    public CharPosition getFrom() {
        return pos;
    }

    public CharPosition getToExc() {
        return pos;
    }

    public String toString() {
        return "NoMatch";
    }

    public <B> ParseResult<B> map( Function1<R,B> mappingFunction ) {
        return (ParseResult<B>) this;
    }
}