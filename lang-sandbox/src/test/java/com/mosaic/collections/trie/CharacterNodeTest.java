package com.mosaic.collections.trie;

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
        CharacterNode node = new CharacterNode();

        assertFalse( node.hasOutEdges() );
    }

// APPEND LABEL

    @Test
    public void givenBlankNode_append_expectNewNode() {
        CharacterNode node = new CharacterNode();
        CharacterNode nextNode = node.append('a').get(0);

        assertTrue( node.hasOutEdges() );
        assertFalse( nextNode.hasOutEdges() );
        assertEquals( Arrays.asList( nextNode ), node.fetch( 'a' ) );
        assertEquals( Arrays.<CharacterNode>asList(), node.fetch( 'b' ) );
    }

    @Test
    public void givenBlankNode_appendTwoCharacters_expectTwoNewConnectedNode() {
        CharacterNode startingNode = new CharacterNode();
        CharacterNode n1           = startingNode.append('a').get(0);
        CharacterNode n2           = startingNode.append('b').get(0);

        assertNotSame(n1, n2);
        assertEquals( Arrays.asList(n1), startingNode.fetch('a') );
        assertEquals( Arrays.asList(n2), startingNode.fetch('b') );
        assertEquals( Arrays.<CharacterNode>asList(), startingNode.fetch('c') );
    }


// replaceNode

    private CharacterPredicate a = CharacterPredicates.constant( 'a' );

    @Test
    public void givenBlankNode_replaceNode_expectNoChange() {
        CharacterNode n = new CharacterNode();

        assertEquals( 0, n.replace(a, new CharacterNode(), new CharacterNode()) );
    }

    @Test
    public void givenNodeWithOneEdge_replaceNodeWithNoMatch_expectNoChange() {
        CharacterNode n1 = new CharacterNode();
        CharacterNode n2 = new CharacterNode();
        CharacterNode n3 = new CharacterNode();

        n1.append(a, n2);

        assertEquals( 0, n1.replace(a, new CharacterNode(), n3) );
        assertSame(n2, n1.fetch('a').get(0));
    }

    @Test
    public void givenNodeWithOneEdge_replaceNodeWithMatch_expectOneChange() {
        CharacterNode n1 = new CharacterNode();
        CharacterNode n2 = new CharacterNode();
        CharacterNode n3 = new CharacterNode();

        n1.append(a, n2);

        int numNodesReplaced = n1.replace(a, n2, n3);
        assertEquals( 1, numNodesReplaced );
        assertSame(n3, n1.fetch('a').get(0));
    }

    @Test
    public void givenNodeWithTwoEdges_replaceNodeWithOneMatchOneCharMismatch_expectOneChange() {
        CharacterNode n1 = new CharacterNode();
        CharacterNode n2 = new CharacterNode();
        CharacterNode n3 = new CharacterNode();

        n1.append(a, n2);
        n1.append('b', n2);

        int numNodesReplaced = n1.replace(a, n2, n3);
        assertEquals( 1, numNodesReplaced );
        assertSame(n3, n1.fetch('a').get(0));
    }

    @Test
    public void givenNodeWithTwoEdges_replaceNodeWithOneMatchAndOneNodeMismatch_expectOneChange() {
        CharacterNode n1 = new CharacterNode();
        CharacterNode n2 = new CharacterNode();
        CharacterNode n3 = new CharacterNode();

        n1.append(a, n2);
        n1.append(a, n3);

        int numNodesReplaced = n1.replace(a, n2, n3);
        assertEquals( 1, numNodesReplaced );
        assertSame( n3, n1.fetch('a').get(0) );
    }

    @Test
    public void givenNodeWithTwoEdges_replaceNodeWithTwoMatches_expectTwoChanges() {
        CharacterNode n1 = new CharacterNode();
        CharacterNode n2 = new CharacterNode();
        CharacterNode n3 = new CharacterNode();

        n1.append(a, n2);
        n1.append(a, n2);

        int numNodesReplaced = n1.replace(a, n2, n3);
        assertEquals( 2, numNodesReplaced );
        assertSame( n3, n1.fetch('a').get(0) );
    }


// remove

    @Test
    public void givenBlankNode_remove_expectNoChange() {
        CharacterNode n1 = new CharacterNode();
        CharacterNode n2 = new CharacterNode();

        int numEdgesRemoved = n1.remove(a, n2);

        assertEquals(0, numEdgesRemoved);

        TrieAssertions.assertGraphEquals( n1, "1" );
    }

    @Test
    public void givenNodeWithTwoOutEdgesForSameChar_removeOneEdge_expectOneEdgeLeft() {
        CharacterNode n1 = new CharacterNode();
        CharacterNode n2 = new CharacterNode();
        CharacterNode n3 = new CharacterNode();

        n1.append( a, n2 );
        n1.append( a, n3 );

        int numEdgesRemoved = n1.remove(a, n2);

        assertEquals(1, numEdgesRemoved);

        TrieAssertions.assertGraphEquals( n1, "1 -a-> 2" );

        assertSame( n3, n1.getOutNodes().get(0) );
    }

}
