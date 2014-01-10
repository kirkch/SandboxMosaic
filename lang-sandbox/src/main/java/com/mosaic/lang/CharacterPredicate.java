package com.mosaic.lang;

/**
 *
 */
public interface CharacterPredicate extends Comparable<CharacterPredicate> {
    public boolean matches( char c );
}
