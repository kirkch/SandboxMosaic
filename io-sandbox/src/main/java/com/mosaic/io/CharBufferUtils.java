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


    public static boolean isWhitespaceAt( int pos, CharBuffer buf ) {
        char c = buf.get(pos);

        return c == ' ' || c == '\t';
    }

    /**
     *
     * @return the index of the first non-whitespace char
     */
    public static int skipWhitespace( CharBuffer buf, int startInc, int limitExc ) {
        for ( int i=startInc; i<limitExc; i++ ) {
            if ( !CharBufferUtils.isWhitespaceAt(i, buf) ) {
                return i;
            }
        }

        return limitExc;
    }

    /**
     * Trim spaces and tabs moving left from toExc.  Do not go beyond from.
     *
     * @return the position after removing whitespace, toExc if no whitespace was found
     */
    public static int trimRight( CharBuffer buf, int from, int toExc ) {
        for ( int i=toExc-1; i>= from; i-- ) {
            char c = buf.get(i);

            if ( c != ' ' && c != '\t' ) {
                return i+1;
            }
        }

        return toExc;
    }

}
