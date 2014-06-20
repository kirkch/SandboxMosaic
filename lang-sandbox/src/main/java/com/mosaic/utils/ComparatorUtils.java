package com.mosaic.utils;

/**
 *
 */
public class ComparatorUtils {

    public static int compareAsc( long a, long b ) {
        if ( a < b ) {
            return -1;
        }

        return a > b ? 1 : 0;
    }

    public static int compareDesc( long a, long b ) {
        return compareAsc( b, a );
    }

    public static int compareAsc( float a, float b ) {
        if ( a < b ) {
            return -1;
        }

        return a > b ? 1 : 0;
    }

    public static int compareDesc( float a, float b ) {
        return compareAsc( b, a );
    }

}
