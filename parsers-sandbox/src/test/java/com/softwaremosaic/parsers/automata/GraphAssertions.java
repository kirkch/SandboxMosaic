package com.softwaremosaic.parsers.automata;

import com.softwaremosaic.parsers.trie.CharacterNode;
import com.softwaremosaic.parsers.trie.CharacterNodeFormatter;

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

    public static <T> void assertGraphEquals( CharacterNode<T> startingNode, String...graphDescription ) {
        CharacterNodeFormatter<T> f = new CharacterNodeFormatter<>();

        assertEquals( Arrays.asList(graphDescription), f.format(startingNode) );
    }

}
