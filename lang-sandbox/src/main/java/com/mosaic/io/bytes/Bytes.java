package com.mosaic.io.bytes;

import com.mosaic.lang.SystemX;


/**
 *
 */
public abstract class Bytes implements OutputBytes, InputBytes {

    public static Bytes wrap( String str ) {
        return new ArrayBytes( str.getBytes(SystemX.UTF8) );
    }

}
