package com.mosaic.lang;

/**
 * Represents up to two million major units (eg GBP) of cash to 3 decimal places (eg pence).
 *
 * Rounding occurs to the minor currency at request.
 */
public class SmallCashType {

    public static int MAX_VALUE = Integer.MAX_VALUE;
    public static int MIN_VALUE = Integer.MIN_VALUE;



    /**
     * Drops the minor unit of the currency, rounding down.  For example in
     * GBP then pence component will be dropped returning only pounds.
     */
    public static int extractMajorComponent( int amt ) {
        return amt/1000;
    }

    /**
     * Drops the major unit of currency, returning only the minor.  For example
     * in GBP only the pounds sterling part will be returned, dropping the pence.
     */
    public static int extractMinorComponent( int amt ) {
        return Math.abs(amt%1000)/10;
    }

    /**
     *
     * @param amt specified in major unit eg GBP
     */
    public static int fromMajor( int amt ) {
        return amt*1000;
    }

    /**
     *
     * @param amt specified in major unit eg pence
     */
    public static int fromMinor( int amt ) {
        return amt*10;
    }


    /**
     * Rounds the minor currency up.  So 42.1 pence becomes 43 pence
     * and 11.0 pence remains 11 pence.
     */
    public static long roundUp( long amt ) {
        long delta = amt < 0 ? -9 : 9;

        return ((amt+delta)/10)*10;
    }

    /**
     * Rounds the minor currency down.  So 42.1 pence becomes 42 pence
     * and 11.0 pence remains 11 pence.
     */
    public static long roundDown( long amt ) {
        return (amt/10)*10;
    }

    /**
     * Rounds the minor currency to the closest pence.  So 42.4 pence becomes 42 pence,
     * and 42.5 becomes 43 pence.
     */
    public static long roundClosest( long amt ) {
        long delta = amt < 0 ? -5 : 5;

        return ((amt+delta)/10)*10;
    }

    public static String toString( int amt ) {
        int major = extractMajorComponent( amt );
        int minor = extractMinorComponent( amt );

        if ( amt < 0 && (major == 0 && minor != 0)  ) {
            return String.format("-%d.%02d", major, minor);
        } else {
            return String.format("%d.%02d", major, minor);
        }
    }

}
