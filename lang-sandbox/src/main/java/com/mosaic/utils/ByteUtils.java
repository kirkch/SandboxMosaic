package com.mosaic.utils;

/**
 *
 */
public class ByteUtils {

    public static byte[] copy( byte[] source, int fromInc, int toExc ) {
        int    numBytes = toExc-fromInc;
        byte[] dest     = new byte[numBytes];

        System.arraycopy( source, fromInc, dest, 0, numBytes );

        return dest;
    }

}
