package com.mosaic.utils.string;

/**
 *
 */
public interface CharacterMatcher<T> {
    /**
     * @return numCharactersMatched
     */
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc );

    public T matchType();
}
