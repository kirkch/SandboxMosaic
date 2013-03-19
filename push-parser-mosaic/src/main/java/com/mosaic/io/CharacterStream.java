package com.mosaic.io;

import com.mosaic.collections.IntStack;
import com.mosaic.lang.Validate;

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

    public CharacterStream( String initialContents ) {
        this( Characters.wrapString(initialContents) );
    }

    public CharacterStream( Characters initialCharacters ) {
        characters = initialCharacters;
    }

    public void appendCharacters( Characters newCharacters ) {
        this.characters = this.characters.appendCharacters(newCharacters);
    }

    public CharPosition getPosition() {
        return characters.skipCharacters(offset).getPosition();
    }

    public void pushMark() {
        markPoints.push( offset );
    }

    public void popMark() {
        offset = markPoints.pop();

        if ( markPoints.isEmpty() && offset > 0 ) {
            // NB: there is no way to return past the last mark, thus by skipping over these characters
            // we give the implementation of Characters the opportunity to drop an objects that it no longer needs.
            // Thus allowing us to walk BIG streams of objects without the entire stream being in memory at once.
            characters = characters.skipCharacters( offset );
            offset     = 0;
        }
    }


    @Override
    public int length() {
        return this.characters.length() - offset;
    }

    @Override
    public char charAt( int index ) {
        return this.characters.charAt(index + offset);
    }

    @Override
    public CharSequence subSequence( int start, int end ) {
        return this.characters.subSequence( start, end );
    }

    @Override
    public String toString() {
        return this.characters.skipCharacters(offset).toString();
    }

    // Functions that 'consume' content.

    public void skipCharacters( int numCharacters ) {
        Validate.isLTE( numCharacters, this.length(), "numCharacters" );

        offset += numCharacters;
    }

//    public boolean startsWith( String targetString )

//    /**
//     * Returns true if targetString is at fromIndex.
//     */
//    public boolean containsAt( String targetString, int fromIndex );
//
//    public Characters skipWhile( CharPredicate predicate )
}
