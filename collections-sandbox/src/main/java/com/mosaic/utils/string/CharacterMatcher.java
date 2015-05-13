package com.mosaic.utils.string;

/**
 *
 */
public interface CharacterMatcher {
    /**
     * @return numCharactersMatched
     */
    public int consumeFrom( CharSequence seq, int minIndex, int maxIndexExc );

}
