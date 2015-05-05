package com.mosaic.utils;

import com.mosaic.io.PrettyPrinter;
import com.mosaic.io.PrettyPrinters;
import com.mosaic.io.RuntimeIOException;
import com.mosaic.lang.text.UTF8;

import java.io.IOException;
import java.util.List;


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

    public static String concat( List elements, String prefix, String separator, String postfix ) {
        StringBuilder buf = new StringBuilder(100);

        buf.append( prefix );

        for ( int i=0; i<elements.size(); i++ ) {
            if ( i != 0 ) {
                buf.append( separator );
            }
            buf.append( elements.get(i) );
        }

        buf.append( postfix );

        return buf.toString();
    }

    public static boolean isBlank( String chars ) {
        return chars == null || chars.trim().length() == 0;
    }

    public static boolean isBlank( String[] lines ) {
        return lines == null || lines.length == 0 || (lines.length == 1 && isBlank(lines[0]));
    }

    public static boolean isBlank( UTF8 chars ) {
        return chars == null || chars.getByteCount() == 0;
    }

    public static String join( String...elements ) {
        StringBuilder buf = new StringBuilder(100);

        for ( String s: elements ) {
            buf.append( s );
        }

        return buf.toString();
    }

    public static <T> String join( Iterable<T> elements, String separator ) {
        StringBuilder buf = new StringBuilder();

        join( buf, elements, separator );

        return buf.toString();
    }

    // todo move to PrettyPrinter
    public static <T> void join( Appendable buf, Iterable<T> elements, String separator ) {
        join( buf, elements, separator, PrettyPrinters.TO_STRING );
    }

    public static <T> String join( Iterable<T> elements, String separator, PrettyPrinter<T> formatter) {
        StringBuilder buf = new StringBuilder();

        join( buf, elements, separator, formatter );

        return buf.toString();
    }

    public static <T> void join( Appendable buf, Iterable<T> elements, String separator, PrettyPrinter<T> formatter ) {
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


    public static String trimRight( String string ) {
        int len = string.length();
        int i = len;

        while ( i > 0 ) {
            char c = string.charAt(i-1);

            if ( c == ' ' || c == '\t' ) {
                i--;
            } else if ( i == len ) {
                return string;
            } else {
                return string.substring(0,i);
            }
        }

        return "";
    }

    public static String removePostFix( String string, String postfix ) {
        return string != null && string.endsWith(postfix) ? string.substring(0,string.length()-postfix.length()) : string;
    }

    /**
     * Return the string up to and excluding the specified character.
     */
    public static String upto( String str, char c ) {
        if ( str == null ) {
            return null;
        }

        int i = str.indexOf(c);

        return i >= 0 ? str.substring( 0, i ) : str;
    }

}
