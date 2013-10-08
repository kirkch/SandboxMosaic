package com.mosaic.utils;

/**
 *
 */
public class ComparatorUtils {
    public static int compare( long a, long b ) {
        if ( a < b ) {
            return -1;
        }

        return a > b ? 1 : 0;
    }
}
