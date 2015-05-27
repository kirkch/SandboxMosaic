package com.mosaic.parser;

import com.mosaic.collections.FastStack;
import com.mosaic.io.CharPosition;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.VoidFunction1;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.StringUtils;
import com.mosaic.utils.string.CharacterMatcher;
import com.mosaic.utils.string.CharacterMatchers;

import java.util.Arrays;

import static com.mosaic.lang.system.Backdoor.toInt;


/**
 * A stream of characters.  The stream is designed to support a recursive decent parser with
 * support for backtracking.
 */
public class ParserStream {

    public static ParserStream fromText( String...lines ) {
        return new ParserStream( StringUtils.join( Arrays.asList( lines ), SystemX.NEWLINE ) );
    }

    private static final CharacterMatcher WHITESPACE_MATCHER = CharacterMatchers.whitespace();
    private static final CharacterMatcher TOEOL_MATCHER      = CharacterMatchers.everythingExcept( '\n' );

    private final FastStack<CharPosition> stack = new FastStack<>();

    private final CharSequence chars;
    private final int          maxExc;

    private CharPosition pos;


    public ParserStream( CharSequence chars ) {
        this( chars, 0 );
    }

    public ParserStream( CharSequence chars, int pos ) {
        this.chars  = chars;
        this.pos    = new CharPosition();
        this.maxExc = chars.length();

        this.pos = this.pos.walkCharacters( pos, chars );
    }


    public CharPosition getCurrentPosition() {
        return pos;
    }

    public int skipWhitespace() {
        return skip( WHITESPACE_MATCHER );
    }

    /**
     * Return the characters matched by matcher.  Starts matching from the current position, and
     * moves the current position on to the character immediately after the last character matched.
     */
    public String consume( CharacterMatcher matcher ) {
        int from = toInt( pos.getCharacterOffset() );
        int len  = matcher.consumeFrom( chars, from, maxExc );

        if ( len <= 0 ) {
            return null;
        }

        String match = chars.subSequence( from, from+len ).toString();

        this.pos = pos.walkCharacters( len, chars );

        return match;
    }

    public int skip( CharacterMatcher matcher ) {
        int from                  = toInt(pos.getCharacterOffset());
        int numCharactersConsumed = matcher.consumeFrom( chars, from, maxExc );

        this.pos = pos.walkCharacters( numCharactersConsumed, chars );

        return numCharactersConsumed;
    }

    public void pushPosition() {
        stack.push( pos );
    }

    public void popPosition() {
        stack.pop();
    }

    public void rollbackToPreviousPosition() {
        this.pos = stack.pop();
    }

    public char charAt( int offset ) {
        return chars.charAt( offset );
    }

    public boolean isEOF() {
        return pos.getCharacterOffset() >= maxExc;
    }

    public char consumeNext() {
        char c = peekAtCurrentChar();

        pos = pos.walkCharacters( 1, chars );

        return c;
    }

    public char peekAtCurrentChar() {
        return chars.charAt( toInt( pos.getCharacterOffset() ) );
    }

    /**
     * Consumes up to the next '\n'.
     */
    public int skipToEndOfLine() {
        return skip( TOEOL_MATCHER );
    }

    public boolean jumpTo( CharPosition targetPos ) {
        QA.argIsBetween( 0, targetPos.getCharacterOffset(), maxExc, "targetPos.getCharacterOffset" );

        if ( SystemX.isDebugRun() ) {
            jumpTo(toInt(targetPos.getCharacterOffset()));

            assert targetPos.getColumnNumber() == this.pos.getColumnNumber();
            assert targetPos.getLineNumber() == this.pos.getLineNumber();
        }

        this.pos = targetPos;

        return true;
    }

    public void jumpTo( int pos ) {
        this.pos = new CharPosition().walkCharacters( pos, chars );
    }

    @SuppressWarnings("unchecked")
    public ParseResult parseZeroOrMore( ParserAndAction...parserAndActions ) {
        return new ZeroOrMoreParser(parserAndActions).parseFrom( this );
    }

    public ParseResult<String> parse( CharacterMatcher m ) {
        CharPosition from = getCurrentPosition();

        int fromInt               = Backdoor.toInt( from.getCharacterOffset() );
        int numCharactersConsumed = m.consumeFrom( chars, fromInt, maxExc );
        if ( numCharactersConsumed == 0 ) {
            return ParseResult.noMatch( from );
        }

        String value = chars.subSequence( fromInt, fromInt+numCharactersConsumed ).toString();

        this.pos = pos.walkCharacters( numCharactersConsumed, chars );

        return ParseResult.matchSucceeded( value, from, getCurrentPosition() );
    }

    private class ZeroOrMoreParser extends Parser {
        private ParserAndAction[] parserAndActions;

        public ZeroOrMoreParser( ParserAndAction[] parserAndActions ) {
            this.parserAndActions = parserAndActions;
        }

        protected ParseResult doParse( ParserStream in ) {
            CharPosition from = getCurrentPosition();

            loop:
            while (true) {
                for ( ParserAndAction parserAndAction : parserAndActions ) {
                    Parser      p = parserAndAction.parser;
                    ParseResult r = p.parseFrom( ParserStream.this );

                    if ( r.matched() ) {
                        parserAndAction.action.invoke( r.getParsedValueNbl() );

                        continue loop;
                    } else if ( r.errored() ) {
                        return r;
                    }
                }

                return ParseResult.matchSucceeded( null, from, getCurrentPosition() );
            }
        }
    }

    public String toString( CharPosition from, CharPosition toExc ) {
        int start = toInt( from.getCharacterOffset() );
        int end   = toInt( toExc.getCharacterOffset() );

        return chars.subSequence(start, end).toString();
    }




    // NB we extend Pair rather than use it directly because the Java8 compiler could not infer
    // a method reference in the following style of usage:
    //
    //          parseZeroOrMore( new Pair<>(FIELD_PARSER, rc::addField) )
    //
    // this is because the java8 compiler could not see a functional interface at the second
    // argument of Pair without being led by the noise.

    public static class ParserAndAction<T> {
        public final Parser<T>        parser;
        public final VoidFunction1<T> action;

        public ParserAndAction( Parser<T> parser, VoidFunction1<T> action ) {
            this.parser = parser;
            this.action = action;
        }
    }

}
