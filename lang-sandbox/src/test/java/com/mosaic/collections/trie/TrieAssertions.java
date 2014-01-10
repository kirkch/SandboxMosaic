package com.mosaic.collections.trie;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TrieAssertions {

    public static <T> void assertGraphEquals( CharacterNode<T> startingNode, String...graphDescription ) {
        CharacterNodeFormatter<T> f = new CharacterNodeFormatter<>();

        assertEquals( Arrays.asList( graphDescription ), f.format(startingNode) );
    }

}
