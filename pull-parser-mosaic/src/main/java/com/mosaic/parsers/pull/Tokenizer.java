package com.mosaic.parsers.pull;

import com.mosaic.collections.CircularBufferChar;
import com.mosaic.io.RuntimeIOException;
import com.mosaic.utils.string.CharacterMatcher;
import com.mosaic.utils.string.CharacterMatchers;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

/**
 * Powerful tool for incrementally breaking a stream of characters into identified tokens. This Tokenizer is a pull tokenizer,
 * meaning that it only processes characters as it is commanded to. No more, no less. Making it extremely efficient.<p/>
 *
 * Methods fall into two categories, 1) scan along the incoming bytes, 2) extract string from mark up to current position.
 */
public class Tokenizer {
    private static final int DEFAULT_BUFFER_SIZE  = 8192;

    public static Tokenizer autoskipWhitespace( Reader in ) {
        Tokenizer tokenizer = new Tokenizer( in );
        tokenizer.autoskipWhitespace( true );

        return tokenizer;
    }


    private Source source;

    public Tokenizer( Reader in ) {
        this( in, DEFAULT_BUFFER_SIZE );
    }

    public Tokenizer( Reader in, int bufferSize ) {
        this.source = new Source( in, bufferSize );
    }

    public int getLineNumber()   { return source.getLineNumber(); }
    public int getColumnNumber() { return source.getColumnNumber(); }

    public boolean isEOF() {
        return source.isEOF();
    }

    public void autoskipWhitespace( boolean flag ) {
        source.autoskipWhitespace( flag );
    }

    public boolean walkConstant( String targetConstant  ) throws IOException {
        return walk( CharacterMatchers.constant( targetConstant ) );
    }

    public boolean walkWhitespace() throws IOException {
        return walk( CharacterMatchers.whitespace() );
    }

    public boolean walkRegexp( String regexp ) throws IOException {
        return walk( CharacterMatchers.jdkRegexp( regexp ) );
    }

    public boolean walkRegexp( Pattern regexp ) throws IOException {
        return walk( CharacterMatchers.jdkRegexp( regexp ) );
    }

    public boolean walkNonWhitespace() throws IOException {
        return walk( CharacterMatchers.nonWhitespace() );
    }

    public boolean walk( int numCharacters ) throws IOException {
        return walk( CharacterMatchers.consumeUptoNCharacters( numCharacters ) );
    }

    public boolean walk( CharacterMatcher matcher ) throws IOException {
        return source.walk( matcher );
    }


    /**
     * Returns all of the characters that have been walked over since the last call to consume or skip. Implicitly
     * starts a new walk.
     */
    public String consume() {
        return source.consume();
    }

    /**
     * Ignore all characters that have been walked thus far and start a new walk.
     */
    public void skip() {
        source.consume();
    }

    /**
     * Returns back to the starting point of this walk. A new walk starts straight after a call to consume() or skip().
     */
    public void undoWalk() {
        source.undoWalk();
    }

    public TextPosition getPosition() {
        return new TextPosition( getLineNumber(), getColumnNumber() );
    }
}

class Source {
    private Reader             in;
    private CircularBufferChar ring;

    private CircularBuffer2CharSequenceAdapter charSequenceAdapter;

    private boolean autoskipWhitespace = false;

    private int currentLine   = 1;
    private int currentColumn = 1;

    public Source( Reader in, int bufSize ) {
        this.in   = in;
        this.ring = new CircularBufferChar( bufSize );

        charSequenceAdapter = new CircularBuffer2CharSequenceAdapter( in, ring );
    }

    public void autoskipWhitespace( boolean flag ) {
        autoskipWhitespace = flag;
    }

    public int getLineNumber()   { return currentLine; }
    public int getColumnNumber() { return currentColumn; }

    public boolean walk( CharacterMatcher matcher ) {
        charSequenceAdapter.refillIfLow();

        if ( autoskipWhitespace ) {
            useMatcher( CharacterMatchers.whitespace() );
        }

        return useMatcher( matcher );
    }

    public String consume() {
        String v = charSequenceAdapter.consume();

        updateLineAndColumnNumbers( v );

        return autoskipWhitespace ? v.trim() : v;
    }

    public boolean isEOF() {
        charSequenceAdapter.refillIfLow();

        if ( autoskipWhitespace ) {
            useMatcher( CharacterMatchers.whitespace() );
        }

//        return ring.isEmpty();
        return charSequenceAdapter.isEOF();
    }

    public void undoWalk() {
        charSequenceAdapter.undoWalk();
    }

    private boolean useMatcher( CharacterMatcher matcher ) {
        int numCharactersMatched = matcher.consumeFrom( charSequenceAdapter, 0, charSequenceAdapter.length() );
        charSequenceAdapter.incOffset( numCharactersMatched );

        return numCharactersMatched > 0;
    }

    private void updateLineAndColumnNumbers( String v ) {
        int maxIndexExc = v.length();
        for ( int i=0; i< maxIndexExc; i++ ) {
            char c = v.charAt( i );

            if ( c == '\n' ) {
                currentLine++;
                currentColumn = 0;
            } else {
                currentColumn++;
            }
        }
    }
}


class CircularBuffer2CharSequenceAdapter implements CharSequence {

    private int waterMark;
    private int offset = 0;
    private Reader in;
    private CircularBufferChar ring;

    public CircularBuffer2CharSequenceAdapter( Reader in, CircularBufferChar ring ) {
        this.in   = in;
        this.ring = ring;

        this.waterMark = ring.bufferSize()/2;
    }

    public void refillIfLow() {
        if ( ring.freeSpace() < waterMark ) {
            return;
        }

        try {
            ring.append( in );
        } catch (IOException e) {
            throw RuntimeIOException.recast(e);
        }
    }

    @Override
    public int length() {
        return ring.usedCapacity();
    }

    @Override
    public char charAt( int index ) {
        return (char) ring.peekLHS( offset+index );
    }

    @Override
    public CharSequence subSequence( int start, int end ) {
        throw new UnsupportedOperationException( "" );
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException( "" );
    }

    public String consume() {
        String v = ring.popStringLHS( offset );

        offset = 0;

        return v;
    }

    public void incOffset( int numCharactersMatched ) {
        offset += numCharactersMatched;
    }

    public void undoWalk() {
        offset = 0;
    }

    public boolean isEOF() {
        return ring.usedCapacity() - offset <= 0;
    }
}
