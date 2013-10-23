package com.mosaic.utils;

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

}
