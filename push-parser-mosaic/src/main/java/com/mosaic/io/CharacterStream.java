package com.mosaic.io;

import com.mosaic.collections.IntStack;

/**
 * Collects and stores Characters until processed. Supports multiple mark points within the stream.
 *
 * @Motivation support asynchronous parsing of large streams of characters
 * @NotThreadSafe
 * @Mutable
 */
public class CharacterStream implements CharSequence {

    private Characters characters;
    private int        offset;
    private IntStack   markPoints = new IntStack(3);


    public CharacterStream() {
        this( Characters.wrapString("") );
    }

    public CharacterStream( Characters initialCharacters ) {
        characters = initialCharacters;
    }

    public void appendCharacters( Characters newCharacters ) {
        this.characters = this.characters.appendCharacters(newCharacters);
    }

    public CharPosition getPosition() {
        return characters.getPosition();
    }

    public void pushMark() {

    }

    public void popMark() {

    }


    @Override
    public int length() {
        return this.characters.length();
    }

    @Override
    public char charAt( int index ) {
        return this.characters.charAt(index);
    }

    @Override
    public CharSequence subSequence( int start, int end ) {
        return this.characters.subSequence( start, end );
    }


// Functions that 'consume' content.

    public Characters skipCharacters( int numCharacters ) {
        return null;
    }

//    public boolean startsWith( String targetString )

//    /**
//     * Returns true if targetString is at fromIndex.
//     */
//    public boolean containsAt( String targetString, int fromIndex );
//
//    public Characters skipWhile( CharPredicate predicate )
}
