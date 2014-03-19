package com.mosaic.lang;

/**
 * Represents up to 922 trillion units of cash to 4 decimal places.
 *
 * Rounding occurs to the minor currency at request.
 */
public class BigCashType {

    public static long MAX_VALUE = Long.MAX_VALUE;
    public static long MIN_VALUE = Long.MIN_VALUE;


    /**
     * Drops the minor unit of the currency, rounding down.  For example in
     * GBP then pence component will be dropped returning only pounds.
     */
    public static long extractMajorComponent( long amt ) {
        return amt/10000;
    }

    /**
     * Drops the major unit of currency, returning only the minor.  For example
     * in GBP only the pounds sterling part will be returned, dropping the pence.
     */
    public static long extractMinorComponent( long amt ) {
        long minor = Math.abs( amt % 10000 );

        return minor/100;
    }

    /**
     * Rounds the minor currency up.  So 42.1 pence becomes 43 pence
     * and 11.0 pence remains 11 pence.
     */
    public static long roundUp( long amt ) {
        long delta = amt < 0 ? -99 : 99;

        return ((amt+delta)/100)*100;
    }

    /**
     * Rounds the minor currency down.  So 42.1 pence becomes 42 pence
     * and 11.0 pence remains 11 pence.
     */
    public static long roundDown( long amt ) {
        return (amt/100)*100;
    }

    /**
     * Rounds the minor currency to the closest pence.  So 42.4 pence becomes 42 pence,
     * and 42.5 becomes 43 pence.
     */
    public static long roundClosest( long amt ) {
        long delta = amt < 0 ? -50 : 50;

        return ((amt+delta)/100)*100;
    }

    public static String toString( long amt ) {
        long major = extractMajorComponent( amt );
        long minor = extractMinorComponent( amt );

        if ( amt < 0 && (major == 0 && minor != 0)  ) {
            return String.format("-%d.%02d", major, minor);
        } else {
            return String.format("%d.%02d", major, minor);
        }
    }

    public static String toStringMinor( long amt ) {
        long major = amt/100;
        long minor = Math.abs(amt%100);

        if ( amt < 0 && (major == 0 && minor != 0)  ) {
            return String.format("-%d.%02d", major, minor);
        } else {
            return String.format("%d.%02d", major, minor);
        }
    }

}
