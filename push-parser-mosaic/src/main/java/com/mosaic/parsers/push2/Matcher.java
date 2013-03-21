package com.mosaic.parsers.push2;

import com.mosaic.io.CharacterStream;

/**
 * Matches a region of characters. Supports being composited into a graph of Matchers; think finite state machine. This
 * composition is designed to support parsing large bodies of text that arrive in batches without blocking the calling
 * thread. This non-blocking behaviour is the primary reason for using this matcher
 */
public abstract class Matcher<T> {

    private   Matcher         parentMatcher;
    protected CharacterStream inputStream;


    public Matcher() {}

    public Matcher( Matcher parent ) {
        withParent( parent );
    }

    public Matcher<T> withParent( Matcher parentMatcher ) {
        this.parentMatcher = parentMatcher;

        return this;
    }

    public Matcher<T> withInputStream( CharacterStream in ) {
        inputStream = in;

        return this;
    }

    public abstract MatchResult<T> processInput();



    protected MatchResult<T> createHasResultStatus( T result ) {
        return MatchResult.createHasResultStatus( this.parentMatcher, result );
    }

    protected MatchResult<T> createHasFailedStatus( String description, String...args ) {
        return MatchResult.createHasFailedStatus( this.parentMatcher, description, args );
    }

}

