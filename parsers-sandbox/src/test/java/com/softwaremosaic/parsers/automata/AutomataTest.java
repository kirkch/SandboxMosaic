package com.softwaremosaic.parsers.automata;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class AutomataTest {

    private Automata automata = new Automata();


    @Test
    public void givenBlankAutomata_tryWalkingA_expectNo() {
        Node startingNode = automata.getStartingNode();

        List<Node> edges = startingNode.walk( 'a' );

        assertTrue( edges.isEmpty() );
    }

    @Test
    public void givenBlankAutomata_fetchStartingNode_expectNoLabel() {
        Node startingNode = automata.getStartingNode();

        assertNull( startingNode.getLabel() );
    }

    @Test
    public void givenBlankAutomata_fetchStartingNode_expectTerminal() {
        Node startingNode = automata.getStartingNode();

        assertTrue(startingNode.isTerminal());
    }

    @Test
    public void givenBlankAutomata_appendA_expectStartNodeToNoLongerBeATerminal() {
        Node startingNode = automata.getStartingNode();
        startingNode.appendConstant("a");

        assertFalse( startingNode.isTerminal() );
    }

    @Test
    public void givenAutomataA_tryWalkingA_expectNextNode() {
        Node startingNode = automata.getStartingNode();
        startingNode.appendConstant("a");


        List<Node> edges = startingNode.walk( 'a' );
        Node       nextNode = edges.get(0);

        assertEquals( 1, edges.size() );
        assertNotSame( startingNode, nextNode );
        assertTrue( nextNode.isTerminal() );
        assertNull( nextNode.getLabel() );
    }

}
