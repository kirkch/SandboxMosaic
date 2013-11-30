package com.mosaic.parsers;

import com.mosaic.io.CharPosition;
import com.mosaic.io.CharPredicate;
import com.mosaic.io.Characters;

/**
 * Walks a CharSequence breaking it up into defined sub-strings.  In essence, a
 * pull parser.  Stores more data in memory than a push parser, but simpler
 * to understand.
 */
public class Tokenizer implements CharSequence {

    private Characters characters;

    public Tokenizer() {
        this( Characters.EMPTY );
    }

    public Tokenizer( CharSequence characters ) {
        this.characters = Characters.wrapCharacters(characters);
    }


    public void append( CharSequence s ) {

    }

    public void pushMark() {

    }

    public void popMark() {

    }

    public void rollbackToLastMark() {

    }



    public CharPosition getPosition() {
        return null;
    }

    public void skipWhitespace() {

    }

    public void skipAll( String regexp ) {

    }

    public void skipAll( CharPredicate predicate ) {

    }

    public CharSequence consumeIdentifier() {
        return null;
    }

    public CharSequence consumeNonWhitespace() {
        return null;
    }

    public CharSequence consumeAll( String regexp ) {
        return null;
    }

    public CharSequence consumeAll( CharPredicate predicate ) {
        return null;
    }

    public CharSequence consumeConstant( CharSequence expectedCharacters ) {
        return null;
    }

    public CharSequence consumeDigits() {
        return null;
    }

    public CharSequence consumeLatinCharacters() {
        return null;
    }

    public boolean consumeEOL() {
        return false;
    }



    public int length() {
        return 0;
    }

    public char charAt(int index) {
        return 'a';
    }

    public CharSequence subSequence(int start, int end) {
        return null;
    }

    public String toString() {
        return null;
    }

}
