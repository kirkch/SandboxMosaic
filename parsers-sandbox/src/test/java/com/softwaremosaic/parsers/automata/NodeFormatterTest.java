package com.softwaremosaic.parsers.automata;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
*
*/
@SuppressWarnings("unchecked")
public class NodeFormatterTest {

    private NodeFormatter formatter = new NodeFormatter();


    @Test
    public void givenNull_format_expectException() {
        try {
            formatter.format(null);

            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("'startingNode' must not be null", e.getMessage());
        }
    }

    @Test
    public void givenEmptyNode_format_expectDisplay() {
        ObjectNode startingNode = new ObjectNode();

        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("1t"), formattedGraph );
    }

    @Test
    public void givenEmptyNodeWithLabel_format_expectDisplay() {
        ObjectNode startingNode = new ObjectNode();

        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("1t"), formattedGraph );
    }

    @Test
    public void formatNodeWithSingleTraversal() {
        ObjectNode startingNode = new ObjectNode();
        startingNode.append('a');

        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("1 -a-> 2t"), formattedGraph );
    }

    @Test
    public void formatThreeNodesLinkedInSerial() {
        ObjectNode startingNode = new ObjectNode();
        Nodes n1 = startingNode.append('a');
        n1.append( 'b' );

        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("1 -a-> 2 -b-> 3t"), formattedGraph );
    }

    @Test
    public void formatNodeWithTwoTraversalsToSameNode() {
        ObjectNode startingNode = new ObjectNode();
        Nodes n1 = startingNode.append('a');
        startingNode.append('b', n1);


        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("1 -[ab]-> 2t"), formattedGraph );
    }

    @Test
    public void formatNodeWithMultipleTraversalsToSameNode_expectCharactersToBeGrouped() {
        ObjectNode startingNode = new ObjectNode();
        Nodes n1 = startingNode.append('a');
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

        assertEquals( Arrays.asList("1 -[0-5a-dftxyz]-> 2t"), formattedGraph );
    }

    @Test
    public void formatNodeLinkedToTwoOtherNodes() {
        ObjectNode startingNode = new ObjectNode();
        startingNode.append('a');
        startingNode.append('b');


        List<String> formattedGraph = formatter.format(startingNode);

        List<String> expected = Arrays.asList(
                "1 -a-> 2t",
                "  -b-> 3t"
        );

        assertEquals(expected, formattedGraph );
    }

    @Test
    public void formatNodeThatLoopsBackToItself() {
        ObjectNode startingNode = new ObjectNode();

        startingNode.append('a', startingNode);


        List<String> formattedGraph = formatter.format(startingNode);

        List<String> expected = Arrays.asList(
                "1 -a-> 1"
        );

        assertEquals(expected, formattedGraph );
    }


    // cycles
}
