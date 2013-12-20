package com.softwaremosaic.parsers.automata;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
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
        Node startingNode = new Node();

        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("1t"), formattedGraph );
    }

    @Test
    public void givenEmptyNodeWithLabel_format_expectDisplay() {
        Node startingNode = new Node("l1");

        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("l1: 1t"), formattedGraph );
    }

    @Test
    public void formatNodeWithSingleTraversal() {
        Node startingNode = new Node("l1");
        startingNode.appendCharacter('a');

        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("l1: 1 -a-> 2t"), formattedGraph );
    }

    @Test
    public void formatThreeNodesLinkedInSerial() {
        Node startingNode = new Node("l1");
        Nodes n1 = startingNode.appendCharacter('a');
        n1.appendCharacter('b');

        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("l1: 1 -a-> 2 -b-> 3t"), formattedGraph );
    }

    @Test
    public void formatNodeWithTwoTraversalsToSameNode() {
        Node startingNode = new Node("l1");
        Nodes n1 = startingNode.appendCharacter('a');
        startingNode.appendEdge('b', n1);


        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("l1: 1 -[ab]-> 2t"), formattedGraph );
    }

    @Test
    public void formatNodeWithMultipleTraversalsToSameNode_expectCharactersToBeGrouped() {
        Node startingNode = new Node("l1");
        Nodes n1 = startingNode.appendCharacter('a');
        startingNode.appendEdge('f', n1);
        startingNode.appendEdge('c', n1);
        startingNode.appendEdge('d', n1);
        startingNode.appendEdge('t', n1);
        startingNode.appendEdge('x', n1);
        startingNode.appendEdge('b', n1);
        startingNode.appendEdge('z', n1);
        startingNode.appendEdge('y', n1);
        startingNode.appendEdge('0', n1);
        startingNode.appendEdge('1', n1);
        startingNode.appendEdge('2', n1);
        startingNode.appendEdge('3', n1);
        startingNode.appendEdge('4', n1);
        startingNode.appendEdge('5', n1);


        List<String> formattedGraph = formatter.format(startingNode);

        assertEquals( Arrays.asList("l1: 1 -[0-5a-dftxyz]-> 2t"), formattedGraph );
    }

    @Test
    public void formatNodeLinkedToTwoOtherNodes() {
        Node startingNode = new Node("l1");
        startingNode.appendCharacter('a');
        startingNode.appendCharacter('b');


        List<String> formattedGraph = formatter.format(startingNode);

        List<String> expected = Arrays.asList(
                "l1: 1 -a-> 2t",
                "      -b-> 3t"
        );

        assertEquals(expected, formattedGraph );
    }

    @Test
    public void formatThreeNodesInSerialSpanningTwoLabels() {
        Node  startingNode = new Node("l1");
        Nodes secondNode   = startingNode.appendCharacter('a');
        secondNode.appendCharacter("l2", 'a');


        List<String> formattedGraph = formatter.format(startingNode);

        List<String> expected = Arrays.asList(
                "l1: 1 -a-> 2",
                "l2:          -a-> 3t"
        );

        assertEquals(expected, formattedGraph );
    }

}
