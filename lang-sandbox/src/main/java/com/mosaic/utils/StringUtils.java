package com.mosaic.utils;

import com.mosaic.io.Formatter;
import com.mosaic.io.Formatters;
import com.mosaic.io.RuntimeIOException;
import com.mosaic.lang.UTF8;

import java.io.IOException;

/**
 *
 */
@SuppressWarnings("unchecked")
public class StringUtils {

    public static String concat( String prefix, Object[] elements, String separator, String postfix ) {
        StringBuilder buf = new StringBuilder(100);

        buf.append( prefix );

        for ( int i=0; i<elements.length; i++ ) {
            if ( i != 0 ) {
                buf.append( separator );
            }
            buf.append( elements[i] );
        }

        buf.append( postfix );

        return buf.toString();
    }

    public static boolean isBlank( String chars ) {
        return chars == null || chars.trim().length() == 0;
    }

    public static boolean isBlank( UTF8 chars ) {
        return chars == null || chars.getSizeInBytes() == 0;
    }

    public static <T> String join( Iterable<T> elements, String separator ) {
        StringBuilder buf = new StringBuilder();

        join( buf, elements, separator );

        return buf.toString();
    }

    public static <T> void join( Appendable buf, Iterable<T> elements, String separator ) {
        join( buf, elements, separator, Formatters.TO_STRING );
    }

    public static <T> String join( Iterable<T> elements, String separator, Formatter<T> formatter) {
        StringBuilder buf = new StringBuilder();

        join( buf, elements, separator, formatter );

        return buf.toString();
    }

    public static <T> void join( Appendable buf, Iterable<T> elements, String separator, Formatter<T> formatter ) {
        if ( elements == null )  {
            return;
        }


        boolean requiresSeparator = false;

        try {
            for ( T e : elements ) {
                if ( requiresSeparator ) {
                    buf.append(separator);
                } else {
                    requiresSeparator = true;
                }

                formatter.write( buf, e );
            }
        } catch ( IOException e ) {
            throw new RuntimeIOException(e);
        }
    }

    public static void repeat( StringBuilder buf, int numTimes, char c ) {
        for ( int i=0; i<numTimes; i++ ) {
            buf.append(c);
        }
    }

    /**
     * Given two strings, compares them in order and identifies the index of
     * the first char that does not match between them.  For example
     * given 'abc' and 'abd' it will return 2.  'ab' and 'ba' will return
     * 0.
     */
    public static int endIndexExcOfCommonPrefix(String a, String b) {
        int max = Math.min( a.length(), b.length() );
        for ( int i=0; i<max; i++ ) {
            if ( a.charAt(i) != b.charAt(i) )  {
                return i;
            }
        }

        return max;
    }

}
