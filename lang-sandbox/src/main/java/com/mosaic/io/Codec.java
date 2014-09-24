package com.mosaic.io;

import com.mosaic.lang.functional.Try;
import com.mosaic.lang.functional.TryNow;


/**
 *
 */
public interface Codec<A,B> {

    public B encode( A v );
    public A decode( B v );

    public boolean supportsDecoding();
    public boolean supportsEncoding();


    public default Try<B> tryEncode( A v ) {
        return TryNow.tryNow( () -> this.encode( v ) );
    }

    public default Try<A> tryDecode( B v ) {
        return TryNow.tryNow( () -> this.decode(v) );
    }

}
