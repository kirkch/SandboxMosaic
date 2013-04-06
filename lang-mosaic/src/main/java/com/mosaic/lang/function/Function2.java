package com.mosaic.lang.function;

/**
 *
 */
public interface Function2<R,A1,A2> {
    public R invoke( A1 arg1, A2 arg2 );
}
