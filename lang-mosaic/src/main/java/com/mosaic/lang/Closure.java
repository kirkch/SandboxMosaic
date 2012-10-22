package com.mosaic.lang;

/**
 *
 */
public interface Closure<T> {
    public T invoke( T input );
}
