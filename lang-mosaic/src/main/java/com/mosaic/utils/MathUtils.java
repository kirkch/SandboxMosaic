package com.mosaic.utils;

/**
 *
 */
public class MathUtils {
    public static <T extends Comparable> T max( T a, T b ) {
        if ( a == null ) {
            return b;
        } else if ( b == null ) {
            return a;
        }

        int c = a.compareTo(b);
        if ( c == 0 ) {
            return a;
        }

        return c > 0 ? a : b;
    }

    public static int roundUpToClosestPowerOf2( int v ) {
        int n = 2;

        while ( v > n ) {
            n *= 2;
        }

        return n;
    }

    public static int roundDownToClosestPowerOf2( int v ) {
        int n = 2;

        while ( (n*2) <= v ) {
            n *= 2;
        }

        return Math.max(2,n);
    }

    public static boolean isPowerOf2( int v ) {
        return v == roundUpToClosestPowerOf2( v );
    }
}
