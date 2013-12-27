package com.softwaremosaic.parsers.automata;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static com.softwaremosaic.parsers.automata.GraphAssertions.*;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ObjectNodeTest {

    @Test
    public void givenBlankNode_isTerminal_expectTrue() {
        Node<Character> node = new ObjectNode();

        assertTrue( node.isTerminal() );
    }

// APPEND LABEL

    @Test
    public void givenBlankNode_append_expectNewNode() {
        Node<Character> node = new ObjectNode();
        Node<Character> nextNode = node.append( 'a' ).get( 0 );

        assertFalse( node.isTerminal() );
        assertTrue( nextNode.isTerminal() );
        assertEquals( Arrays.asList( nextNode ), node.walk( 'a' ) );
        assertEquals( Arrays.<Node<Character>>asList(), node.walk( 'b' ) );
    }

    @Test
    public void givenBlankNode_appendTwoCharacters_expectTwoNewConnectedNode() {
        Node<Character> startingNode = new ObjectNode();
        Node<Character> n1           = startingNode.append('a').get(0);
        Node<Character> n2           = startingNode.append('b').get(0);

        assertNotSame(n1, n2);
        assertEquals( Arrays.asList(n1), startingNode.walk('a') );
        assertEquals( Arrays.asList(n2), startingNode.walk('b') );
        assertEquals( Arrays.<Node<Character>>asList(), startingNode.walk('c') );
    }
    

// replaceNode

    @Test
    public void givenBlankNode_replaceNode_expectNoChange() {
        Node<Character> n = new ObjectNode();

        assertEquals( 0, n.replace('a', new ObjectNode(), new ObjectNode()) );
    }

    @Test
    public void givenNodeWithOneEdge_replaceNodeWithNoMatch_expectNoChange() {
        Node<Character> n1 = new ObjectNode();
        Node<Character> n2 = new ObjectNode();
        Node<Character> n3 = new ObjectNode();

        n1.append('a', n2);

        assertEquals( 0, n1.replace('a', new ObjectNode(), n3) );
        assertSame(n2, n1.walk('a').get(0));
    }

    @Test
    public void givenNodeWithOneEdge_replaceNodeWithMatch_expectOneChange() {
        Node<Character> n1 = new ObjectNode();
        Node<Character> n2 = new ObjectNode();
        Node<Character> n3 = new ObjectNode();

        n1.append('a', n2);

        int numNodesReplaced = n1.replace('a', n2, n3);
        assertEquals( 1, numNodesReplaced );
        assertSame(n3, n1.walk('a').get(0));
    }

    @Test
    public void givenNodeWithTwoEdges_replaceNodeWithOneMatchOneCharMismatch_expectOneChange() {
        Node<Character> n1 = new ObjectNode();
        Node<Character> n2 = new ObjectNode();
        Node<Character> n3 = new ObjectNode();

        n1.append('a', n2);
        n1.append('b', n2);

        int numNodesReplaced = n1.replace('a', n2, n3);
        assertEquals( 1, numNodesReplaced );
        assertSame(n3, n1.walk('a').get(0));
    }

    @Test
    public void givenNodeWithTwoEdges_replaceNodeWithOneMatchAndOneNodeMismatch_expectOneChange() {
        Node<Character> n1 = new ObjectNode();
        Node<Character> n2 = new ObjectNode();
        Node<Character> n3 = new ObjectNode();

        n1.append('a', n2);
        n1.append('a', n3);

        int numNodesReplaced = n1.replace('a', n2, n3);
        assertEquals( 1, numNodesReplaced );
        assertSame( n3, n1.walk('a').get(0) );
    }

    @Test
    public void givenNodeWithTwoEdges_replaceNodeWithTwoMatches_expectOneChange() {
        Node<Character> n1 = new ObjectNode();
        Node<Character> n2 = new ObjectNode();
        Node<Character> n3 = new ObjectNode();

        n1.append('a', n2);
        n1.append('a', n2);

        int numNodesReplaced = n1.replace('a', n2, n3);
        assertEquals( 2, numNodesReplaced );
        assertSame( n3, n1.walk('a').get(0) );
    }


// remove

    @Test
    public void givenBlankNode_remove_expectNoChange() {
        Node<Character> n1 = new ObjectNode();
        Node<Character> n2 = new ObjectNode();

        int numEdgesRemoved = n1.remove('a', n2);

        assertEquals(0, numEdgesRemoved);

        assertGraphEquals( n1, "1t" );
    }

    @Test
    public void givenNodeWithTwoOutEdgesForSameChar_removeOneEdge_expectOneEdgeLeft() {
        Node<Character> n1 = new ObjectNode();
        Node<Character> n2 = new ObjectNode();
        Node<Character> n3 = new ObjectNode();

        n1.append( 'a', n2 );
        n1.append( 'a', n3 );

        int numEdgesRemoved = n1.remove('a', n2);

        assertEquals(1, numEdgesRemoved);

        assertGraphEquals( n1, "1 -a-> 2t" );

        assertSame( n3, n1.getOutNodes().get(0) );
    }

//// appendRegexpIC
//
//    @Test
//    public void givenBlankNode_appendRegexpICConstant() {
//        Node<Character> start     = new ObjectNode("l1");
//        start.appendRegexpIC( "hello" );
//
//        assertCanWalk( start, "hello" );
//        assertCanWalk( start, "Hello" );
//        assertCanWalk( start, "HeLlo" );
//        assertCanWalk( start, "HeLlO" );
//
//        assertEquals( 0, start.walk("Heo").size() );
//    }
//
//    @Test
//    public void givenNodeContainingRegExp_expectUpperLowercaseTransitionToShareTheSameNode() {
//        Node<Character> start     = new ObjectNode("l1");
//        start.appendRegexpIC( "hello" );
//
//        Node<Character> n1 = start.walk('h').get(0);
//        Node<Character> n2 = start.walk('H').get(0);
//
//        assertSame(n1, n2);
//    }

//    @Test
//    public void givenBlankNode_appendRegexpICZeroOrMore() {
//        Node start     = new Node("l1");
//        start.appendRegexpIC( "a*" );
//
//        Node n1 = start.walk('h').get(0);
//        Node n2 = start.walk('H').get(0);
//
//        assertSame( n1, n2 );
//    }

    // a*
    // abc*
    // a+
    // abc+
    // a?
    // abc?
    // [abc]
    // [a-z]
    // [0-9]
    // [a\-z]
    // [a\\z]
    // [a\\z]
    // [abc]*
    // [a-z]*
    // [abc]+
    // [a-z]+
    // [abc]?
    // [a-z]?


    private void assertCanWalk( Node<Character> start, String path ) {
        Nodes pos = new Nodes( start );

        for ( char c : path.toCharArray() ) {
            pos = pos.walk( c );

            assertTrue( pos.size() > 0 );
        }
    }

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
