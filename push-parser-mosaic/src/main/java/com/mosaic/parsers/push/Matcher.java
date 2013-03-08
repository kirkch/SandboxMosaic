package com.mosaic.parsers.push;

import com.mosaic.io.CharPosition;
import com.mosaic.io.Characters;

/**
 * An immutable character matcher. Pass characters into the matcher and the matcher will process them and return a new
 * instance of itself containing the state of the world after it has done its parsing. It does not mutate itself.
 */
public abstract class Matcher<T> {

    private final T            value;
    private final Characters   remainingBytes;
    private final CharPosition startingPosition;


    protected Matcher() {
        this(null,null,null);
    }

    protected Matcher( T value, Characters remainingBytes, CharPosition startingPosition ) {
        this.value            = value;
        this.remainingBytes   = remainingBytes;
        this.startingPosition = startingPosition;
    }


    /**
     * Process the supplied characters without mutating this instance of itself. Instead a new instance holding the new
     * state of the world will be created and returned.  Before being processed the input characters are concatenated
     * with the remaining bytes of any previous run. Thus allowing the supply of characters to drip or burst in to the
     * parser at any rate without blocking the thread.
     */
    public abstract Matcher<T> processCharacters( Characters in );

    /**
     * Returns the 'parsed' match from this matcher. Which will be null until a full match is made.
     */
    public T getResult() {
        return value;
    }

    /**
     * The characters left after the ones that were matched being consumed. If nothing was consumed then the input bytes
     * will be appended to any other bytes that were remaining. Will be null if no characters have been seen yet.
     */
    public Characters getRemainingCharacters() {
        return remainingBytes;
    }

    /**
     * Returns the position in the stream where this matcher started from. Will be null if no characters have been seen yet.
     */
    public CharPosition getStartingPosition() {
        return startingPosition;
    }


    public int getLineNumber() {
        CharPosition p = startingPosition;

        return p == null ? 0 : p.getLineNumber();
    }

    public int getColumnNumber() {
        CharPosition p = startingPosition;

        return p == null ? 0 : p.getColumnNumber();
    }

    public long getCharacterOffset() {
        CharPosition p = startingPosition;

        return p == null ? 0 : p.getCharacterOffset();
    }
}
