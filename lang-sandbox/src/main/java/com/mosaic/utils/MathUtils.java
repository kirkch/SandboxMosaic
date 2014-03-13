package com.mosaic.utils;

/**
 *
 */
@SuppressWarnings("unchecked")
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



    /**
     * An array of max values for n digit numbers (base 10). Index 0 is for single
     * digit numbers, index 1 is for two digit, 2 for three digits and so on (i+1).
     */
    private static long[] MAX_NUMBER_BOUNDARIES = generateMaxValuesForBase10();

    /**
     * For each boundary in MAX_NUMBER_BOUNDARIES, stores how many characters a string
     * representation of that number would require.
     */
    private static int[] NUM_DIGITS_FOR_NUMBER_BOUNDARIES = generateDigitLengthsMatchingNumberBoundaries();


    private static long[] generateMaxValuesForBase10() {
        long[] buf = new long[19*2];

        long soFar = -1000000000000000000L;

        for ( int i=0; i<buf.length; i++ ) {
            buf[i] = i > 20 && soFar < 0 ? Long.MAX_VALUE : soFar;

            if ( soFar < 0 ) {
                soFar = soFar/10;

                if ( soFar == 0 ) {
                    soFar = 9;
                }
            } else if ( soFar >= 0 ) {
                soFar = soFar*10 + 9;
            }
        }

        return buf;
    }

    private static int[] generateDigitLengthsMatchingNumberBoundaries() {
        int[] buf = new int[19*2];

        int soFar = 20;
        for ( int i=0; i<buf.length; i++ ) {
            buf[i] = soFar;

            if ( i < 19 ) {
                soFar -= 1;
            } else {
                soFar += 1;
            }
        }

        return buf;
    }

    public static int charactersLengthOf( long v ) {
        for ( int i=0; i<MAX_NUMBER_BOUNDARIES.length; i++ ) {
            if ( v <= MAX_NUMBER_BOUNDARIES[i] ) {
                return NUM_DIGITS_FOR_NUMBER_BOUNDARIES[i];
            }
        }

        throw new IllegalStateException( "MAX_NUMBER_BOUNDARIES is too small for " + v );
    }
}
