package com.mosaic.parsers;

import com.mosaic.parsers.matchers.NullMatcher;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Stack;

/**
 *
 */
public abstract class BasePushParser implements PushParser {

    private Matcher initialMatcher;
    private boolean hasReachedEndOfFile;

    private Stack<ParseFrame> stack = new Stack<ParseFrame>();


    protected BasePushParser( Matcher initialMatcher ) {
        this( initialMatcher, NullMatcher.INSTANCE, NullMatcher.INSTANCE );
    }

    /**
     * @param initialMatcher the starting matcher
     * @param skipMatcher    a matcher to use (and throw away results of) between every other matcher call
     * @param onErrorMatcher matcher to use upon receiving an error; the parse will still fail but gives
     *                       the parser a chance to parse more to see if more errors appear
     */
    protected BasePushParser( Matcher initialMatcher, Matcher skipMatcher, Matcher onErrorMatcher ) {
        this.initialMatcher = initialMatcher;
    }

    public void pushStartOfFile() {

    }

    public void pushEndOfFile() {
        hasReachedEndOfFile = true;
    }

    public long push( String in ) {
        return push( CharBuffer.wrap(in) );
    }

    public long push( Reader in ) throws IOException {
        CharBuffer buf = CharBuffer.allocate(1024*4);

        long count = 0;
        int delta = in.read(buf);

        // TODO
        return 0;
    }

    public long push( CharBuffer buf ) {
        MatchResult result         = new MatchResult();

        ParseFrame  currentFrame   = stack.peek();                  // if no frame, let error occur
        Matcher     currentMatcher = currentFrame.currentMatcher;

//        int numCharactersConsumed = currentMatcher.match( buf, result, hasReachedEndOfFile );
//        result.numCharactersConsumed = numCharactersConsumed;

        if ( result.wasSuccessfulMatch() ) {

        } else if ( result.wasNoMatch() ) {

        } else if ( result.wasIncompleteMatch() ) {

        } else {
            assert result.wasError();

        }

        return 0;
    }


    private void appendError( int line, int column, String message ) {

    }



    private static class ParseFrame {
        public Matcher currentMatcher;

        public MatcherContinuation continuation;
    }


    /**
     * When a matcher hands off to a 'sub matcher', the matcher provides
     * a 'continuation'.  A result handler for the sub matcher that selects
     * the next matcher to take over after this sub matcher completes.
     *
     * todo what if the continuation is null, or returns null...
     */
    public static interface MatcherContinuation {
        public Matcher givenResultSelectNextMatcher( MatchResult result );
    }

}
