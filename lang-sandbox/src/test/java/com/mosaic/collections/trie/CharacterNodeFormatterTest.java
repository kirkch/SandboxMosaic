package com.mosaic.collections.trie;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@SuppressWarnings("unchecked")
public class CharacterNodeFormatterTest {

    private CharacterNodeFormatter formatter = new CharacterNodeFormatter();


    @Test
    public void givenNull_format_expectException() {
        try {
            formatter.format(null);

            Assert.fail( "expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals("'startingNode' must not be null", e.getMessage());
        }
    }

    @Test
    public void givenEmptyNode_format_expectDisplay() {
        CharacterNode startingNode = new CharacterNode();

        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("1"), formattedGraph );
    }

    @Test
    public void givenEmptyNodeWithLabel_format_expectDisplay() {
        CharacterNode startingNode = new CharacterNode();

        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("1"), formattedGraph );
    }

    @Test
    public void formatNodeWithSingleTraversal() {
        CharacterNode startingNode = new CharacterNode();
        startingNode.append('a');

        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("1 -a-> 2"), formattedGraph );
    }

    @Test
    public void formatThreeNodesLinkedInSerial() {
        CharacterNode startingNode = new CharacterNode();
        CharacterNodes n1 = startingNode.append('a');
        n1.append( 'b' );

        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("1 -a-> 2 -b-> 3"), formattedGraph );
    }

    @Test
    public void formatNodeWithTwoTraversalsToSameNode() {
        CharacterNode startingNode = new CharacterNode();
        CharacterNodes n1 = startingNode.append('a');
        startingNode.append('b', n1);


        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("1 -a|b-> 2"), formattedGraph );
    }

    @Test
    public void formatNodeWithMultipleTraversalsToSameNode_expectCharactersToBeGrouped() {
        CharacterNode startingNode = new CharacterNode();
        CharacterNodes n1 = startingNode.append('a');
        startingNode.append('f', n1);
        startingNode.append('c', n1);
        startingNode.append('d', n1);
        startingNode.append('t', n1);
        startingNode.append('x', n1);
        startingNode.append('b', n1);
        startingNode.append('z', n1);
        startingNode.append('y', n1);
        startingNode.append('0', n1);
        startingNode.append('1', n1);
        startingNode.append('2', n1);
        startingNode.append('3', n1);
        startingNode.append('4', n1);
        startingNode.append('5', n1);


        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("1 -a|f|c|d|t|x|b|z|y|0|1|2|3|4|5-> 2"), formattedGraph );
    }

    @Test
    public void formatNodeLinkedToTwoOtherNodes() {
        CharacterNode startingNode = new CharacterNode();
        startingNode.append('a');
        startingNode.append('b');


        List<String> formattedGraph = formatter.format(startingNode);

        List<String> expected = Arrays.asList(
            "1 -a-> 2",
            "  -b-> 3"
        );

        assertEquals(expected, formattedGraph );
    }


// cycles

    @Test
    public void formatNodeThatLoopsBackToItself() {
        CharacterNode startingNode = new CharacterNode();

        startingNode.append('a', startingNode);


        List<String> formattedGraph = formatter.format(startingNode);

        List<String> expected = Arrays.asList(
            "1 -a-> 1"
        );

        assertEquals(expected, formattedGraph );
    }


// custom node formatting

    @Test
    public void relabelTheNodeNamesViaAPlugin() {
        CharacterNode startingNode = new CharacterNode();
        startingNode.append('a');

        List<String> formattedGraph = formatter.format(startingNode, new CharacterNodeFormatter.NodeFormatPlugin() {
            public String getNodeLabelFor( long nodeId, CharacterNode node ) {
                return "'"+nodeId+"'";
            }
        });

        assertEquals( Arrays.asList("'1' -a-> '2'"), formattedGraph );
    }

}
