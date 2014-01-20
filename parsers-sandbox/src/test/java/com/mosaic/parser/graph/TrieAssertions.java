package com.mosaic.parser.graph;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TrieAssertions {

    public static  void assertGraphEquals( Node startingNode, String...graphDescription ) {
        NodeFormatter f = new NodeFormatter();

        assertEquals( Arrays.asList( graphDescription ), f.format(startingNode) );
    }

}
