package com.mosaic.parser.graph;

import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.CharacterPredicates;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;


/**
 *
 */
@SuppressWarnings("unchecked")
public class CharacterNodeTest {

    @Test
    public void givenBlankNode_hasOutEdges_expectFalse() {
        Node node = new Node();

        assertFalse( node.hasOutEdges() );
    }

// APPEND LABEL

    @Test
    public void givenBlankNode_append_expectNewNode() {
        Node node = new Node();
        Node nextNode = node.append('a').get(0);

        assertTrue( node.hasOutEdges() );
        assertFalse( nextNode.hasOutEdges() );
        assertEquals( Arrays.asList( nextNode ), node.fetch( 'a' ) );
        assertEquals( Arrays.<Node>asList(), node.fetch( 'b' ) );
    }

    @Test
    public void givenBlankNode_appendTwoCharacters_expectTwoNewConnectedNode() {
        Node startingNode = new Node();
        Node n1           = startingNode.append('a').get(0);
        Node n2           = startingNode.append('b').get(0);

        assertNotSame(n1, n2);
        assertEquals( Arrays.asList(n1), startingNode.fetch('a') );
        assertEquals( Arrays.asList(n2), startingNode.fetch('b') );
        assertEquals( Arrays.<Node>asList(), startingNode.fetch('c') );
    }


// replaceNode

    private CharacterPredicate a = CharacterPredicates.constant( 'a' );

    @Test
    public void givenBlankNode_replaceNode_expectNoChange() {
        Node n = new Node();

        assertEquals( 0, n.replace(a, new Node(), new Node()) );
    }

    @Test
    public void givenNodeWithOneEdge_replaceNodeWithNoMatch_expectNoChange() {
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();

        n1.append(a, n2);

        assertEquals( 0, n1.replace(a, new Node(), n3) );
        assertSame(n2, n1.fetch('a').get(0));
    }

    @Test
    public void givenNodeWithOneEdge_replaceNodeWithMatch_expectOneChange() {
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();

        n1.append(a, n2);

        int numNodesReplaced = n1.replace(a, n2, n3);
        assertEquals( 1, numNodesReplaced );
        assertSame(n3, n1.fetch('a').get(0));
    }

    @Test
    public void givenNodeWithTwoEdges_replaceNodeWithOneMatchOneCharMismatch_expectOneChange() {
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();

        n1.append(a, n2);
        n1.append('b', n2);

        int numNodesReplaced = n1.replace(a, n2, n3);
        assertEquals( 1, numNodesReplaced );
        assertSame(n3, n1.fetch('a').get(0));
    }

    @Test
    public void givenNodeWithTwoEdges_replaceNodeWithOneMatchAndOneNodeMismatch_expectOneChange() {
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();

        n1.append(a, n2);
        n1.append(a, n3);

        int numNodesReplaced = n1.replace(a, n2, n3);
        assertEquals( 1, numNodesReplaced );
        assertSame( n3, n1.fetch('a').get(0) );
    }

    @Test
    public void givenNodeWithTwoEdges_replaceNodeWithTwoMatches_expectTwoChanges() {
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();

        n1.append(a, n2);
        n1.append(a, n2);

        int numNodesReplaced = n1.replace(a, n2, n3);
        assertEquals( 2, numNodesReplaced );
        assertSame( n3, n1.fetch('a').get(0) );
    }


// remove

    @Test
    public void givenBlankNode_remove_expectNoChange() {
        Node n1 = new Node();
        Node n2 = new Node();

        int numEdgesRemoved = n1.remove(a, n2);

        assertEquals(0, numEdgesRemoved);

        TrieAssertions.assertGraphEquals( n1, "1" );
    }

    @Test
    public void givenNodeWithTwoOutEdgesForSameChar_removeOneEdge_expectOneEdgeLeft() {
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();

        n1.append( a, n2 );
        n1.append( a, n3 );

        int numEdgesRemoved = n1.remove(a, n2);

        assertEquals(1, numEdgesRemoved);

        TrieAssertions.assertGraphEquals( n1, "1 -a-> 2" );

        assertSame( n3, n1.getOutNodes().get(0) );
    }

}
