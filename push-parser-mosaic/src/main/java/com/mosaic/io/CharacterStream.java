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
    private boolean    hasReceivedEOS;

    private int        commitMark;
    private String     commitSource;
    private String     commitReason;


    public CharacterStream() {
        this( Characters.wrapString("") );
    }

    public CharacterStream( String initialContents ) {
        this( Characters.wrapString(initialContents) );
    }

    public CharacterStream( Characters initialCharacters ) {
        characters = initialCharacters;
    }

    public boolean hasReceivedEOS() {
        return hasReceivedEOS;
    }

    /**
     * Is at the end of the input stream. Requires hasReceivedEOS to be true.
     */
    public boolean isAtEOS() {
        return hasReceivedEOS && offset == characters.length();
    }

    public void appendCharacters( String newCharacters ) {
        appendCharacters( Characters.wrapString(newCharacters) );
    }

    public CharacterStream appendCharacters( Characters newCharacters ) {
        Validate.isTrueState( !hasReceivedEOS, "cannot append to closed stream" );

        this.characters = this.characters.appendCharacters(newCharacters);

        return this;
    }

    public CharacterStream appendEOS() {
        hasReceivedEOS = true;

        return this;
    }

    public CharPosition getPosition() {
        return characters.skipCharacters(offset).getPosition();
    }

    public void pushMark() {
        markPoints.push( offset );
    }

    public void popMark() {
        markPoints.pop();
    }

    /**
     * Makes it illegal to rollback past the current point in the character stream. Should be used sparingly. As a result
     * calls to 'rollback' will only be used to consume characters on the stream and to release their memory and will not
     * actually perform the act of rolling back.
     *
     * @param source who performed this devious act
     * @param reason why they thought it so important that they did so
     */
    public void markNonRollbackablePoint( String source, String reason ) {
        commitMark   = offset;
        commitSource = source;
        commitReason = reason;
    }

    /**
     * Returns how many marks are currently active on the stream.
     */
    public int markCount() {
        return markPoints.size();
    }

    public void returnToMark() {
        int targetOffset = markPoints.pop();

        if ( targetOffset >= commitMark ) { // NB only rollback if the rollback point is after the commitMark
            offset = targetOffset;
        }

        if ( markPoints.isEmpty() && offset > 0 ) {
            // NB: there is no way to return past the last mark, thus by skipping over these characters
            // we give the implementation of Characters the opportunity to drop any objects that it no longer needs.
            // Thus allowing us to walk BIG streams of objects without the entire stream being in memory at once.
            characters = characters.skipCharacters( offset );
            offset     = 0;
            commitMark = 0;
        }
    }

    public int getLineNumber() {
        return getPosition().getLineNumber();
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

    public boolean startsWith( String targetString ) {
        return characters.containsAt( targetString, offset );
    }

    public CharSequence consumeCharacters( int numCharacters ) {
        Validate.isLTE( numCharacters, this.length(), "numCharacters" );

        if ( numCharacters == 0 ) {
            return "";
        }


        CharSequence v = subSequence( offset, offset+numCharacters );

        offset += numCharacters;

        return v;
    }

//    public boolean startsWith( String targetString )

//    /**
//     * Returns true if targetString is at fromIndex.
//     */
//    public boolean containsAt( String targetString, int fromIndex );
//
//    public Characters skipWhile( CharPredicate predicate )
}
