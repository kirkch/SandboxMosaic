package com.mosaic.utils;

import com.mosaic.lang.functional.Predicate;

/**
 *
 */
public class EscapableCharacterIterator {
    private CharSequence str;
    private int          pos;

    private char                 escapeChar;
    private Predicate<Character> isSpecialPredicate;

    private transient boolean hasNext;
    private transient char    nextChar;
    private transient boolean isSpecial;



    public EscapableCharacterIterator( CharSequence str, char escapeChar, Predicate<Character> isSpecialChar ) {
        this.str                = str;
        this.escapeChar         = escapeChar;
        this.isSpecialPredicate = isSpecialChar;

        this.hasNext            = str.length() > 0;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public char next() {
        scrollToNext();

        return nextChar;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    private void scrollToNext() {
        hasNext = true;

        char c = str.charAt(pos);
        if ( c == escapeChar ) {
            if ( pos+1 >= str.length() ) {
                throw new IllegalArgumentException( "Escape char at end of input string: '"+str+"'" );
            }

            nextChar  = str.charAt( pos+1 );
            isSpecial = false;
            pos      += 2;
        } else {
            nextChar  = c;
            isSpecial = isSpecialPredicate.invoke( c );
            pos      += 1;
        }

        hasNext = pos < str.length();
    }
}
