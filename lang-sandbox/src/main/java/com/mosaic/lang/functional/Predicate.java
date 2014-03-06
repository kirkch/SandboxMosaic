package com.mosaic.lang.functional;

/**
 *
 */
public interface Predicate<T> {

    public boolean invoke( T arg );

}
