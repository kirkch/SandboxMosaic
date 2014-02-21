package com.mosaic.lang.text;

/**
 * The result of decoding bytes into a single character.  This object is used
 * as an OUT parameter to a method as Java does not support returning tuples
 * efficiently.
 */
public class DecodedCharacter {
    public char c;
    public int  numBytesConsumed;
}
