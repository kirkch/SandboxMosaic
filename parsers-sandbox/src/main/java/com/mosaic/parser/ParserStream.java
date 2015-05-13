package com.mosaic.parser;

import com.mosaic.collections.FastStack;
import com.mosaic.io.CharPosition;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.string.CharacterMatcher;
import com.mosaic.utils.string.CharacterMatchers;

import static com.mosaic.lang.system.Backdoor.toInt;


/**
 * A stream of characters.  The stream is designed to support a recursive decent parser with
 * support for backtracking.
 */
public class ParserStream {

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
        return consume(WHITESPACE_MATCHER);
    }

    public int consume( CharacterMatcher matcher ) {
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
        return chars.charAt( toInt(pos.getCharacterOffset()) );
    }

    /**
     * Consumes up to the next '\n'.
     */
    public int consumeToEndOfLine() {
        return consume(TOEOL_MATCHER);
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

    public String toString( CharPosition from, CharPosition toExc ) {
        int start = toInt( from.getCharacterOffset() );
        int end   = toInt( toExc.getCharacterOffset() );

        return chars.subSequence(start, end).toString();
    }

}
