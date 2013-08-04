package com.mosaic.io;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 *
 */
public class IOUtils {

    public static byte[] toByteArray( CharSequence seq, String charset ) {
        try {
            return seq.toString().getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert multiple strings to an array of bytes, using the specified character
     * set.
     */
    public static byte[][] toBytes(Charset charset, String...values) {
        int numValues = values.length;

        byte[][] bytes = new byte[numValues][];
        for ( int i=0; i<numValues; i++ ) {
            bytes[i] = values[i].getBytes(charset);
        }

        return bytes;
    }

}
