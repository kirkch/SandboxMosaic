package com.mosaic.parser.graph;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TrieAssertions {

    public static <T> void assertGraphEquals( Node<T> startingNode, String...graphDescription ) {
        NodeFormatter<T> f = new NodeFormatter<>();

        assertEquals( Arrays.asList( graphDescription ), f.format(startingNode) );
    }

}
