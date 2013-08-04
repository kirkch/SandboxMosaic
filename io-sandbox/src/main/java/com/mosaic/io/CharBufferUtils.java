package com.mosaic.io;


import java.nio.CharBuffer;


/**
 *
 */
public class CharBufferUtils {

    /**
     * Is the charactor at index 'pos' of 'buf' one of the characters in the
     * 'candidateCharacters' array?
     */
    public static boolean isOneOf( int pos, CharBuffer buf, char[] candidateCharacters ) {
        char c = buf.get(pos);
        for ( int i=0; i<candidateCharacters.length; i++ ) {
            if ( candidateCharacters[i] == c ) {
                return true;
            }
        }

        return false;
    }

    public static boolean startsWith( int pos, CharBuffer buf, char[] targetChars ) {
        int remaining = buf.limit() - pos;

        if ( targetChars.length > remaining ) {
            return false;
        }

        for ( int i=0; i<targetChars.length; i++ ) {
            if ( targetChars[i] != buf.get(i+pos) ) {
                return false;
            }
        }

        return true;
    }

}
