package com.mosaic.utils;

import com.mosaic.io.Formatter;
import com.mosaic.io.Formatters;
import com.mosaic.io.RuntimeIOException;
import com.mosaic.lang.functional.Function2;
import com.mosaic.lang.functional.VoidFunction2;

import java.io.IOException;
import java.util.List;

/**
 *
 */
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

}
