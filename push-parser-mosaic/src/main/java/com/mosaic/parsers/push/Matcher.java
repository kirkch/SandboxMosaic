package com.mosaic.parsers.push;

import com.mosaic.io.CharPosition;
import com.mosaic.io.Characters;
import com.mosaic.lang.Validate;

/**
 * An immutable character matcher. Pass characters into the matcher and the matcher will process them and return a new
 * instance of itself containing the state of the world after it has done its parsing. It does not mutate itself.
 */
public abstract class Matcher<T> {

    private final MatcherStatus<T> status;
    private final Characters       remainingBytes;
    private final CharPosition     startingPosition;


    protected Matcher() {
        this(null,null,null);
    }

    protected Matcher( MatcherStatus<T> status, Characters remainingBytes, CharPosition startingPosition ) {
        Validate.notNull( status, "status" );

        this.status           = status;
        this.remainingBytes   = remainingBytes;
        this.startingPosition = startingPosition;
    }


    /**
     * Process the supplied characters without mutating this instance of itself. Instead a new instance holding the new
     * state of the world will be created and returned.  Before being processed the input characters are concatenated
     * with the remaining bytes of any previous run. Thus allowing the supply of characters to drip or burst in to the
     * parser at any rate without blocking the thread.
     */
    public final Matcher<T> processCharacters( Characters in ) {
        if ( in.length() == 0 ) {
            return this;
        }

        assert this.status.hasCompleted() : "Result already generated, this matcher should not be receiving any more input";

        Characters characters = this.getRemainingCharacters();
        characters = characters == null ? in : characters.appendCharacters( in );

        return _processCharacters( characters );
    }

    protected abstract Matcher<T> _processCharacters( Characters in );

    /**
     * No more characters will be received. Used when the Matcher is greedily matching as much as it can and need to
     * accept what it already has is its full match. By default this is a no-op, and returns itself.
     */
    public Matcher<T> processEndOfStream() {
        return this;
    }

    /**
     * Returns true if this matcher has finished parsing. Being complete includes both a successful match, and failing
     * to match anything at all.
     */
    public boolean hasCompleted() {
        return status.hasCompleted();
    }

    /**
     * Returns true if this matcher is ready to receive more input and is thus not ready to return a conclusion yet.
     */
    public boolean isAwaitingInput() {
        return status.isAwaitingInput();
    }

    /**
     * Returns true if parsing has reached a conclusion and getResult will return a non-null value.
     */
    public boolean hasResult() {
        return status.hasResult();
    }

    /**
     * Returns true if parsing has reached the conclusion that no result will ever be returned and is thus an indication
     * to jump back to a previous decision point and to true a different parse option.
     */
    public boolean hasFailedToMatch() {
        return status.hasErrored();
    }

    /**
     * A human readable description as to why the parsing cannot continue with this parser. Will return null if
     * hasFailedToMatch returns false.
     */
    public String getFailedToMatchDescription() {
        return status.getFailedToMatchDescription();
    }

    /**
     * Returns the 'parsed' match from this matcher. Which will be null until a full match is made.
     */
    public T getResult() {
        return status.getResult();
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
