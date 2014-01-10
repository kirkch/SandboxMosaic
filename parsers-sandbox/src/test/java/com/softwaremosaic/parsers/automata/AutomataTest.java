//package com.softwaremosaic.parsers.automata;
//
//import org.junit.Test;
//
//import java.util.List;
//
//import static org.junit.Assert.*;
//
///**
// *
// */
//public class AutomataTest {
//
//    private Automata automata = new Automata();
//
//
//    @Test
//    public void givenBlankAutomata_tryWalkingA_expectNo() {
//        ObjectNode startingNode = automata.getStartingNode();
//
//        List<ObjectNode> edges = startingNode.walk( 'a' );
//
//        assertTrue( edges.isEmpty() );
//    }
//
//    @Test
//    public void givenBlankAutomata_fetchStartingNode_expectNoLabel() {
//        ObjectNode startingNode = automata.getStartingNode();
//
//        assertNull( startingNode.getPredicate() );
//    }
//
//    @Test
//    public void givenBlankAutomata_fetchStartingNode_expectTerminal() {
//        ObjectNode startingNode = automata.getStartingNode();
//
//        assertTrue(startingNode.isTerminal());
//    }
//
//    @Test
//    public void givenBlankAutomata_appendA_expectStartNodeToNoLongerBeATerminal() {
//        ObjectNode startingNode = automata.getStartingNode();
//        startingNode.appendConstant("a");
//
//        assertFalse( startingNode.isTerminal() );
//    }
//
//    @Test
//    public void givenAutomataA_tryWalkingA_expectNextNode() {
//        ObjectNode startingNode = automata.getStartingNode();
//        startingNode.appendConstant("a");
//
//
//        List<ObjectNode> edges = startingNode.walk( 'a' );
//        ObjectNode nextNode = edges.get(0);
//
//        assertEquals( 1, edges.size() );
//        assertNotSame( startingNode, nextNode );
//        assertTrue( nextNode.isTerminal() );
//        assertNull( nextNode.getPredicate() );
//    }
//
//}
