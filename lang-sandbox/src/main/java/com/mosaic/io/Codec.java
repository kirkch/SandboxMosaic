package com.mosaic.io;

import com.mosaic.lang.functional.Try;

/**
 *
 */
public interface Codec<FROM,TO> {

    /**
     *
     * @return null if the string does not match
     */
    public Try<TO> encode( FROM source );
//  encode( source:FROM ) : TO

    /**
     *
     * @return null if the string does not match
     */
    public Try<FROM> decode( TO source );

    public boolean supportsDecoding();
    public boolean supportsEncoding();

}
