package com.mosaic.parsers.push;

import com.mosaic.io.CharacterStream;

import java.util.ArrayList;
import java.util.List;

/**
 * Matches a region of characters. Supports being composited into a graph of Matchers; think finite state machine. This
 * composition is designed to support parsing large bodies of text that arrive in batches without blocking the calling
 * thread. This non-blocking behaviour is the primary reason for using this matcher
 */
public abstract class Matcher<T> {

    private   Matcher         parentMatcher;
    protected CharacterStream inputStream;

    private List<Matcher> children = new ArrayList(1);

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

        for ( Matcher child : children ) {
            child.withInputStream( in );
        }

        return this;
    }

    public final MatchResult<T> processInput() {
        inputStream.pushMark();

        MatchResult<T> r = _processInput();
        if ( r.hasResult() ) {
            inputStream.popMark();
        } else {
            inputStream.returnToMark();
        }

        return r;
    }

    protected abstract MatchResult<T> _processInput();


    protected <X> Matcher<X> appendChild( Matcher<X> child ) {
        children.add( child );

        return child.withInputStream( inputStream ).withParent( this );
    }

    protected MatchResult<T> createHasResultStatus( T result ) {
        return MatchResult.createHasResultStatus( this.parentMatcher, result );
    }

    protected MatchResult<T> createHasFailedStatus( MatchResult childResult ) {
        assert childResult.getNextMatcher() == this;

        return MatchResult.createHasFailedStatus( this.parentMatcher, childResult.getFailedToMatchDescription() );
    }

    protected MatchResult<T> createHasFailedStatus( String description, String...args ) {
        return MatchResult.createHasFailedStatus( this.parentMatcher, description, args );
    }

    protected MatchResult<T> createIncompleteMatch() {
        return MatchResult.createIncompleteMatch( this );
    }

    protected MatchResult<T> createResultFrom( MatchResult<T> result ) {
        if ( result.hasResult() ) {
            return createHasResultStatus( result.getResult() );
        } else if ( result.hasFailedToMatch() ) {
            return createHasFailedStatus( result );
        } else {
            assert result.isIncompleteMatch();

            return createIncompleteMatch();
        }
    }
}

