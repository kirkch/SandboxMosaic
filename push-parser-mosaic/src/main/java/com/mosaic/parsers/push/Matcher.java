package com.mosaic.parsers.push;

import com.mosaic.io.CharacterStream;
import com.mosaic.lang.Debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Matches a region of characters. Supports being composited into a graph of Matchers; think finite state machine. This
 * composition is designed to support parsing large bodies of text that arrive in batches without blocking the calling
 * thread. This non-blocking behaviour is the primary reason for using this matcher
 */
public abstract class Matcher<T> {

    private static final Debug DEBUG = new Debug();

    /**
     * When a tree of matchers is miss behaving then information on the match attempt can be printed to stdout by
     * settting this debug flag to true. Best done from an isolated test case.
     */
    public static void setDebugEnabled( boolean flag ) {
        DEBUG.setEnabled( flag );
    }



    private   Matcher         parentMatcher;
    private   String          matcherName;

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

    /**
     * Gives this matcher a descriptive name. Usually this would be the BNF token name (RHS) and it is used when printing out
     * information about this matcher.
     */
    public Matcher<T> withName( String name ) {
        this.matcherName = name;

        return this;
    }

    public MatchResult<T> processInput() {
        inputStream.pushMark();



        MatchResult<T> r = _processInput();

        if ( DEBUG.isEnabled() ) {
            String namePP = matcherName == null ? "" : matcherName;
            String resultPP;
            String resultValuePP;

            if ( r.hasResult() ) {
                resultPP = "MATCHED";
                resultValuePP = r.getResult() == null ? "null" : r.getResult().toString();

                DEBUG.setColumnWidths( 30, 9, 60);
                DEBUG.logPP( namePP, resultPP, resultValuePP, this );
            } else {
                resultPP = r.hasFailedToMatch() ? "TRIED" : "PARTIAL";
                resultValuePP = this+" on '"+inputStream.toString()+(inputStream.hasReceivedEOS() ? "[EOS]" : "")+"'";

                DEBUG.setColumnWidths( 32, 7);
                DEBUG.logPP( namePP, resultPP, resultValuePP );
            }


        }

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

