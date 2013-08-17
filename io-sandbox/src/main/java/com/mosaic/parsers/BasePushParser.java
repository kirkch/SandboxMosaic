package com.mosaic.parsers;

import com.mosaic.lang.function.Function1;
import com.mosaic.lang.reflect.ReflectionUtils;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.Stack;

/**
 *
 */
public abstract class BasePushParser implements PushParser {

    private static boolean DEBUG = false;

    private static void debug( String event, Object data ) {
        if ( DEBUG ) {
            System.out.println( event + " = " + data );
        }
    }
// todo line/col numbering
// todo error reporting

    private Matcher initialMatcher;
    private Matcher skipMatcher;
    private Matcher errorRecoveryMatcher;


    private Stack<ParseFrame> stack = new Stack<ParseFrame>();


    protected void setInitialMatcher( Matcher matcher ) {
        initialMatcher = matcher;
    }

    protected void setSkipMatcher(Matcher matcher) {
        skipMatcher = matcher;
    }

    protected void setErrorRecoverMatcher(Matcher matcher) {
        errorRecoveryMatcher = matcher;
    }


    protected void parserStartedEvent() {}
    protected void parserFinishedEvent() {}


    public void reset() {
        stack.clear();

        stack.push( new ParseFrame(initialMatcher) );
    }


    public long push( Reader in ) throws IOException {
        CharBuffer buf = CharBuffer.allocate(1024*4);

        long count = 0;
        int delta = in.read(buf);

        // TODO
        return 0;
    }

    public long push( CharBuffer buf, boolean isEOS ) {
        if ( stack.isEmpty() ) {
            reset();

            parserStartedEvent();
        }

        int startingPosition = buf.position();

        boolean keepGoing = true;
        while ( keepGoing ) {
            ParseFrame  currentFrame   = stack.peek();
            Matcher     currentMatcher = currentFrame.matcher;

            skipMatcher.match(buf,isEOS);

            MatchResult result = performMatch(buf, isEOS, currentMatcher);

            keepGoing = processResult(currentFrame, result, buf, isEOS);
        }

        return buf.position() - startingPosition;
    }

    private MatchResult performMatch(CharBuffer buf, boolean isEOS, Matcher currentMatcher) {
        int bufPositionBeforeInvocation = buf.position();

        MatchResult result = currentMatcher.match( buf, isEOS );

        int bufPositionAfterInvocation = buf.position();
        currentMatcher.setBufferIndexFromPreviousCall(bufPositionAfterInvocation);

        if ( DEBUG ) {
            int consumed = bufPositionAfterInvocation - bufPositionBeforeInvocation;
            assert consumed == result.getNumCharactersConsumed() : "buf position check failed: reported " + result.getNumCharactersConsumed() + " actual " + consumed + " ("+currentMatcher+"->"+result+")";
        }

        return result;
    }


    private boolean processResult( ParseFrame currentFrame, MatchResult result, CharBuffer buf, boolean isEOS ) {
        debug("matcher", currentFrame.matcher);
        debug("result", result);

        if ( result.isMatch() && result.getNumCharactersConsumed() == 0 && buf.position() == currentFrame.matcher.getBufferIndexFromPreviousCall() ) {
            result = MatchResult.noMatch();
            debug("switched_result", result);
        }

        if ( result.isContinuation() ) {
            ParseFrame newFrame = new ParseFrame(result.getNextMatcher(), result.getContinuation());

            stack.push(newFrame);
        } else if ( result.isIncompleteMatch() ) {
            return false;
        } else {
            if ( result.isMatch() ) {
                Method callbackMethod = currentFrame.matcher.resolveCallbackMethod(this);

                if ( callbackMethod != null ) {
                    ReflectionUtils.invoke(this, callbackMethod, result.getParsedValue());

                    debug( "callback", currentFrame.matcher.getCallbackMethodName() );
                }
            }

            if ( currentFrame.continuation != null ) {
                if ( !currentFrame.matcher.shouldParentKeepParsedValueOnMatch() ) {
                    result = result.skipParsedValue();
                }

                MatchResult continuationResult = currentFrame.continuation.invoke( result );

                stack.pop();

                return processResult( stack.peek(), continuationResult, buf, isEOS);
            } else {
                if ( isEOS ) {
                    debug( "jobdone", null );

                    parserFinishedEvent();
                }

                return false;
            }
        }

        return true;
    }


    private void appendError( int line, int column, String message ) {

    }



    private static class ParseFrame {
        public Matcher                            matcher;
        public Function1<MatchResult,MatchResult> continuation;

        public ParseFrame(Matcher m) {
            matcher = m;
        }

        public ParseFrame(Matcher m, Function1<MatchResult,MatchResult> c) {
            matcher = m;
            continuation   = c;
        }
    }

}
