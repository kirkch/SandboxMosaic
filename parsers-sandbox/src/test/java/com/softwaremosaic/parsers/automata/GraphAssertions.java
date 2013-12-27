package com.softwaremosaic.parsers.automata;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class GraphAssertions {

    public static <T extends Comparable<T>> void assertGraphEquals( Node<T> startingNode, String...graphDescription ) {
        NodeFormatter<T> f = new NodeFormatter<>();

        assertEquals( Arrays.asList(graphDescription), f.format(startingNode) );
    }

}
