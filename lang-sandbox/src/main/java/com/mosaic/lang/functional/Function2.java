package com.mosaic.lang.functional;

/**
 *
 */
public interface Function2<A1,A2,R> {
    public R invoke( A1 arg1, A2 arg2 );
}
