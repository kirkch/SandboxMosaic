package com.softwaremosaic.parsers.automata;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class GraphAssertions {


    public static void assertGraphEquals( Node startingNode, String...graphDescription ) {
        NodeFormatter f = new NodeFormatter();

        assertEquals( Arrays.asList(graphDescription), f.format(startingNode) );
    }

}
