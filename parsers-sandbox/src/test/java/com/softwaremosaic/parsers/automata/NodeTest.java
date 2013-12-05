package com.softwaremosaic.parsers.automata;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 *
 */
public class NodeTest {

    @Test
    public void givenBlankNode_isTerminal_expectTrue() {
        Node node = new Node();

        assertTrue( node.isTerminal() );
    }

    @Test
    public void givenBlankNode_appendConstantChar_expectNewNode() {
        Node node     = new Node();
        Node nextNode = node.appendChar('a');

        assertFalse( node.isTerminal() );
        assertTrue( nextNode.isTerminal() );
        assertEquals( Arrays.asList(nextNode), node.walk('a') );
        assertEquals(Arrays.<Node>asList(), node.walk('b'));
    }

    @Test
    public void givenBlankNode_appendTwoConstantChar_expectTwoNewConnectedNode() {
        Node startingNode = new Node();
        Node n1           = startingNode.appendChar('a');
        Node n2           = startingNode.appendChar('b');

        assertNotSame(n1, n2);
        assertEquals( Arrays.asList(n1), startingNode.walk('a') );
        assertEquals( Arrays.asList(n2), startingNode.walk('b') );
        assertEquals( Arrays.<Node>asList(), startingNode.walk('c') );
    }

    @Test
    public void givenBlankNode_appendSameCharTwice_expectTwoNewConnectedNode() {
        Node startingNode = new Node();
        Node n1           = startingNode.appendChar('a');
        Node n2           = startingNode.appendChar('a');

        assertNotSame(n1, n2);
        assertEquals( Arrays.asList(n1,n2), startingNode.walk('a') );
        assertEquals(Arrays.<Node>asList(), startingNode.walk('b'));
    }

    @Test
    public void givenBlankNode_appendConstantChar_expectNewNodeToHaveSameLabelAsSourceNode() {
        Node node     = new Node("l1");
        Node nextNode = node.appendChar('a');

        assertEquals( "l1", nextNode.getLabel() );
    }


    // appendChar
    // appendConstant
    // appendSet
    // appendRange
    // appendNotChar
    // appendNotConstant
    // appendNotSet
    // appendNotRange
    // setElse
    // setRecovery
    // appendAction

    // linkCharToExistingNode
}
