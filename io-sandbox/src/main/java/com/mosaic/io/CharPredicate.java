package com.mosaic.io;

/**
 *
 */
public interface CharPredicate extends Comparable<CharPredicate> {
    public boolean matches( char c );
}
