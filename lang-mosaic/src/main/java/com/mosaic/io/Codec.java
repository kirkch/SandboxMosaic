package com.mosaic.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Converts a source object into an alternative representation, and optionally back again.
 */
public interface Codec<T> {
    public String encode( T v );
    public void encodeTo( T v, Writer out ) throws IOException;

    /**
     *
     * @return null if the string does not match
     */
    public T decode( String v );

    /**
     *
     * @return null if the string does not match
     */
    public T encodeFrom( Reader in ) throws IOException;

    /**
     * When decoding into a instance object is not necessary, consumes matching characters from the input reader that
     * would otherwise have given a valid value. If the characters do not match then no characters will be consumed.
     *
     * @return number of characters consumed
     */
    public int skipMatchingCharactersFrom( Reader in ) throws IOException;

    public boolean supportsDecoding();
    public boolean supportsEncoding();
}
