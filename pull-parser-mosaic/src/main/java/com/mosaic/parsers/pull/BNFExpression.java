package com.mosaic.parsers.pull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Pulls tokens from the tokenizer and converts them into Match objects. This represents the grammatical part of the parse.
 * As matches occur, 'callbacks' can be registered with each expression allowing the match to be further processed. The
 * runtime context passed through the match is passed to the callbacks to facilitate other lookups that may affect the
 * processing. It is not used by the grammar or tokenizer themselves (unless you pull them in to it ;).
 *
 * The default text within the Match objects is the parsed token characters. These characters can be elevated to other
 * objects via the callbacks registered with each expression. Thus it is important to document with each BNFExpression
 * what type is passed up within each Match object (hence the generic on Match).
 */
public abstract class BNFExpression<T> {

    public <L> Match<T> parseOptional( L runtimeContext, Tokenizer in ) throws IOException {
        Match<T> match = doParse( runtimeContext, in, true );

        return processMatch( runtimeContext, match );
    }

    public <L> Match<T> parseMandatory( L runtimeContext, Tokenizer in ) throws IOException {
        Match<T> match = doParse( runtimeContext, in, false );

        return processMatch( runtimeContext, match );
    }



    public abstract <L> Match<T> doParse( L runtimeContext, Tokenizer in, boolean isOptional ) throws IOException;


    private final List<ParserCallback> callbacks = new LinkedList<ParserCallback>();

    public <L> BNFExpression<T> onMatch( ParserCallback<L, T> callback ) {
        callbacks.add( callback );

        return this;
    }

    private <L> Match<T> processMatch( L runtimeContext, Match<T> match ) {
        Match<T> translatedMatch;
        if ( match == null ) {
            // match has already been discarded, skip
            translatedMatch = match;
        } else if ( match.hasValue() ) {
            if ( match.isNone() ) {
                translatedMatch = delegateIsNoneTranslation( runtimeContext, match );
            } else {
                translatedMatch = delegateHasValueTranslation( runtimeContext, match );
            }
        } else {
            translatedMatch = delegateHasErrorTranslation( runtimeContext, match );
        }

        return translatedMatch;
    }

    private <L> Match<T> delegateIsNoneTranslation( L runtimeContext, Match<T> match ) {
        Match<T> translatedMatch = match;

        for ( ParserCallback c : callbacks ) {
            translatedMatch = c.onNone( runtimeContext, match );
        }

        return translatedMatch;
    }

    private <L> Match<T> delegateHasValueTranslation( L runtimeContext, Match<T> match ) {
        Match<T> translatedMatch = match;

        for ( ParserCallback c : callbacks ) {
            translatedMatch = c.onResult( runtimeContext, match );
        }

        return translatedMatch;
    }

    private <L> Match<T> delegateHasErrorTranslation( L runtimeContext, Match<T> match ) {
        Match<T> translatedMatch = match;

        for ( ParserCallback c : callbacks ) {
            translatedMatch = c.onError( runtimeContext, match );
        }

        return translatedMatch;
    }
}
