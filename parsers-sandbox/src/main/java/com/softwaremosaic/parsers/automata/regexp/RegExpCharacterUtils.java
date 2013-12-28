package com.softwaremosaic.parsers.automata.regexp;

import java.util.*;

/**
 *
 */
public class RegExpCharacterUtils {

    public static void formatCharacters( StringBuilder buf, Set<Character> characters ) {
        List<Character> list = new ArrayList<>(characters.size());

        list.addAll(characters);

        Collections.sort(list);

        formatCharacters( buf, list );
    }

    /**
     * If more than one character, rewrite into a regexp [] block, including ranges.
     */
    public static void formatCharacters( StringBuilder buf, List<Character> characters ) {
        switch ( characters.size() ) {
            case 0:
                break;
            case 1:
                buf.append( characters.get(0).charValue() );
                break;
            default:
                buf.append('[');

                appendRegExpBlock(buf, characters);


                buf.append(']');
        }
    }

    /**
     * Summarizes the characters using a [] regexp block.  Characters that
     * are next to each other get grouped into a range, and solo characters
     * get printed by themselves.  For example the character set {abce2345}
     * would get displayed as 'a-ce2-5'  (without the quotes).
     */
    private static void appendRegExpBlock( StringBuilder buf, List<Character> characters ) {
        Character lowerBound = null;
        Character upperBound = null;
        int       rangeCount = 0;

        Iterator<Character> it = characters.iterator();
        while ( it.hasNext() ) {
            Character c = it.next();

            if ( lowerBound == null ) {
                lowerBound = c;
                rangeCount = 1;
            } else if ( upperBound == null ) {
                if ( lowerBound.charValue()+1 == c.charValue() ) {
                    upperBound = c;
                    rangeCount++;
                } else {
                    buf.append( lowerBound.charValue() );
                    lowerBound = c;
                    rangeCount = 1;
                }
            } else {
                if ( upperBound.charValue()+1 == c.charValue() ) {
                    upperBound = c;
                    rangeCount++;
                } else if ( rangeCount >= 4 ) {
                    buf.append( lowerBound.charValue() );
                    buf.append( '-' );
                    buf.append( upperBound.charValue() );

                    upperBound = null;
                    lowerBound = c;
                    rangeCount = 1;
                } else {
                    for ( char v=lowerBound.charValue(); v<=upperBound.charValue(); v++ ) {
                        buf.append(v);
                    }

                    upperBound = null;
                    lowerBound = c;
                    rangeCount = 1;
                }
            }
        }

        //noinspection StatementWithEmptyBody
        if ( lowerBound == null ) {
            // do nothing in this case
        } else if ( upperBound == null ) {
            buf.append( lowerBound.charValue() );
        } else if ( rangeCount >= 4 ) {
            buf.append( lowerBound.charValue() );
            buf.append( '-' );
            buf.append( upperBound.charValue() );
        } else {
            for ( char v=lowerBound.charValue(); v<=upperBound.charValue(); v++ ) {
                buf.append(v);
            }
        }
    }

    public static String escape( String s ) {
        return s; // todo
    }

}
