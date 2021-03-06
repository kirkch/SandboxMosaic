package com.mosaic.parser;

import com.mosaic.io.CharPosition;
import com.mosaic.utils.string.CharacterMatcher;


public abstract class Parser<R> {

    public static Parser<String> wrap( CharacterMatcher m ) {
        return new Parser<String>() {
            protected ParseResult<String> doParse( ParserStream in ) {
                CharPosition from        = in.getCurrentPosition();
                String       matchedText = in.consume( m );

                return matchedText == null ? noMatch(from) : matched(matchedText, from, in.getCurrentPosition());
            }
        };
    }

    public final ParseResult<R> parseFrom( ParserStream in ) {
        in.pushPosition();

        in.skipWhitespace();

        ParseResult<R> r = doParse( in );

        if ( r.successful() ) {
            in.popPosition();
        } else {
            in.rollbackToPreviousPosition();
        }

        return r;
    }

    protected abstract ParseResult<R> doParse( ParserStream in );


    protected ParseResult<R> matched( R v, CharPosition from, CharPosition to ) {
        return ParseResult.matchSucceeded( v, from, to );
    }

    protected ParseResult<R> noMatch( CharPosition pos ) {
        return ParseResult.noMatch( pos );
    }

    protected ParseResult<R> failure( CharPosition pos, String msg, Object...messageArgs ) {
        return ParseResult.matchFailed( String.format( msg, messageArgs ), pos );
    }

    protected ParseResult<R> failure( ParserStream in, String msg, Object...messageArgs ) {
        return ParseResult.matchFailed( String.format(msg,messageArgs), in.getCurrentPosition() );
    }

}
