package com.mosaic.parsers.pull;

/**
 * Register with a BNFExpression to 'hear' and even alter a match as it passes up the expression tree.
 */
public abstract class ParserCallback<L,T> {

    /**
     *
     * @param l a contextual object that is passed through a expression tree, the type is up to the consuming parser
     * @param m the value of the match as it propagates up from the leaf to the root
     *
     * @return the value of the match to pass on from this expression, usually the same value that was passed in to
     *   signify no change
     */
    public Match<T> onResult( L l, Match<T> m ) {
        return m;
    }

    public Match<T> onError( L l, Match<T> m ) {
        return m;
    }

    public Match<T> onNone( L l, Match<T> m ) {
        return m;
    }

}