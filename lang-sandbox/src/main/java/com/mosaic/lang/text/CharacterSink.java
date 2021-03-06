package com.mosaic.lang.text;

/**
 * Receives characters one char at a time.
 */
public interface CharacterSink {

    /**
     * Append one character.  Returns true if the character was accepted, else
     * false.
     */
    public boolean append( char c );

}
