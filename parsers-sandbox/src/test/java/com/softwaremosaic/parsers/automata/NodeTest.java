package com.softwaremosaic.parsers.automata;

import org.junit.Assert;
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

// APPEND CHARACTER

    @Test
    public void givenBlankNode_appendCharacter_expectNewNode() {
        Node node     = new Node();
        Node nextNode = node.appendCharacter('a').get(0);

        assertFalse( node.isTerminal() );
        assertTrue( nextNode.isTerminal() );
        assertEquals( Arrays.asList(nextNode), node.walk('a') );
        assertEquals(Arrays.<Node>asList(), node.walk('b'));
    }

    @Test
    public void givenBlankNode_appendTwoCharacters_expectTwoNewConnectedNode() {
        Node startingNode = new Node();
        Node n1           = startingNode.appendCharacter('a').get(0);
        Node n2           = startingNode.appendCharacter('b').get(0);

        assertNotSame(n1, n2);
        assertEquals( Arrays.asList(n1), startingNode.walk('a') );
        assertEquals( Arrays.asList(n2), startingNode.walk('b') );
        assertEquals( Arrays.<Node>asList(), startingNode.walk('c') );
    }

    @Test
    public void givenBlankNode_appendSameCharTwice_expectEndNodeToBeTheSameForBoth() {
        Node startingNode = new Node();
        Node n1           = startingNode.appendCharacter('a').get(0);
        Node n2           = startingNode.appendCharacter('a').get(0);

        assertSame( "nodes differed", n1, n2 );
        assertEquals( Arrays.asList(n1), startingNode.walk('a') );
        assertEquals( Arrays.<Node>asList(), startingNode.walk('b') );
    }

    @Test
    public void givenBlankNode_appendSameCharTwiceUsingDifferentLabels_expectTwoNewConnectedNode() {
        Node startingNode = new Node();
        Node n1           = startingNode.appendCharacter("l1",'a').get(0);
        Node n2           = startingNode.appendCharacter("l2",'a').get(0);

        assertNotSame( n1, n2 );
        assertEquals( Arrays.asList(n1,n2), startingNode.walk('a') );
        assertEquals( Arrays.<Node>asList(), startingNode.walk('b') );
    }

    @Test
    public void givenBlankNode_appendChar_expectNewNodeToHaveSameLabelAsSourceNode() {
        Node node     = new Node("l1");
        Node nextNode = node.appendCharacter('a').get(0);

        assertEquals( "l1", nextNode.getLabel() );
    }

    @Test
    public void givenBlankNode_appendCharWithLabel_expectNewNodeToHaveNewLabelAsSourceNode() {
        Node node     = new Node("l1");
        Node nextNode = node.appendCharacter("l2", 'a').get(0);

        assertEquals( "l2", nextNode.getLabel() );
    }



// APPEND CONSTANT

    @Test
    public void givenBlankNode_appendNullConstant_expectException() {
        Node node     = new Node();

        try {
            node.appendConstant(null);

            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("'constant' must not be empty (was null)", e.getMessage());
        }
    }

    @Test
    public void givenBlankNode_appendBlankConstant_expectException() {
        Node node     = new Node();

        try {
            node.appendConstant("");

            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("'constant' must not be empty (was '')", e.getMessage());
        }
    }

    @Test
    public void givenBlankNode_appendSingleCharacterConstant_expectNewNode() {
        Node node     = new Node();
        Node nextNode = node.appendConstant("a").get(0);

        assertFalse( node.isTerminal() );
        assertTrue( nextNode.isTerminal() );
        assertEquals( Arrays.asList(nextNode), node.walk('a') );
        assertEquals( Arrays.<Node>asList(), node.walk('b') );
    }

    @Test
    public void givenBlankNode_appendSingleWhitespaceCharacterConstant_expectNewNode() {
        Node node     = new Node();
        Node nextNode = node.appendConstant(" ").get(0);

        assertFalse( node.isTerminal() );
        assertTrue( nextNode.isTerminal() );
        assertEquals( Arrays.asList(nextNode), node.walk(' ') );
        assertEquals( Arrays.<Node>asList(), node.walk('b') );
    }

    @Test
    public void givenBlankNode_appendMultiCharacterConstant_expectNewNode() {
        Node node    = new Node();
        Node endNode = node.appendConstant("hello").get(0);

        assertFalse( node.isTerminal() );
        assertTrue( endNode.isTerminal() );

        assertEquals( Arrays.asList(endNode), node.walk("hello") );
        assertEquals( Arrays.<Node>asList(), node.walk('e') );
    }

    @Test
    public void givenBlankNode_appendTwoConstants_expectTwoNewConnectedNode() {
        Node startingNode = new Node();
        Node n1           = startingNode.appendConstant("a").get(0);
        Node n2           = startingNode.appendConstant("b").get(0);

        assertNotSame(n1, n2);
        assertEquals( Arrays.asList(n1), startingNode.walk('a') );
        assertEquals( Arrays.asList(n2), startingNode.walk('b') );
        assertEquals( Arrays.<Node>asList(), startingNode.walk('c') );
    }

    @Test
    public void givenBlankNode_appendSameConstantTwice_expectSameEndNodes() {
        Node startingNode = new Node();
        Node n1           = startingNode.appendConstant("abc").get(0);
        Node n2           = startingNode.appendConstant("abc").get(0);

        assertSame(n1, n2);
        assertEquals( Arrays.asList(n1), startingNode.walk("abc") );
        assertEquals(Arrays.<Node>asList(), startingNode.walk("abd"));
    }

    @Test
    public void givenBlankNode_appendSameConstantTwiceWithSameLabel_expectSharedCharactersToResultInSharedNodes() {
        Node startingNode = new Node();
        Node n1           = startingNode.appendConstant("l1", "abc").get(0);
        Node n2           = startingNode.appendConstant("l1", "abc").get(0);

        assertSame(n1, n2);
        assertEquals( Arrays.asList(n1), startingNode.walk("abc") );
        assertEquals(Arrays.<Node>asList(), startingNode.walk("abd"));
    }

    @Test
    public void givenBlankNode_appendTwoConstantsThatShareSamePrefixButDiffer_expectSharedInternalNodesButDifferentEndNodes() {
        Node startingNode = new Node();
        Node n1           = startingNode.appendConstant("l1", "abc").get(0);
        Node n2           = startingNode.appendConstant("l1", "abd").get(0);

        assertNotSame(n1, n2);

        assertEquals( 1, startingNode.walk('a').size() );
        assertEquals( 1, startingNode.walk('a').get(0).walk('b').size() );

        assertEquals( Arrays.asList(n1), startingNode.walk("abc") );
        assertEquals( Arrays.asList(n2), startingNode.walk("abd") );
    }

    @Test
    public void givenBlankNode_appendTwoConstantsThatShareSamePrefixButDifferAndHaveDifferentLabels_expectNoSharedNodes() {
        Node startingNode = new Node();
        Node n1           = startingNode.appendConstant("l1", "abc").get(0);
        Node n2           = startingNode.appendConstant("l2", "abd").get(0);

        assertNotSame(n1, n2);

        assertEquals( 2, startingNode.walk('a').size() );
        assertEquals( 2, startingNode.walk("ab").size() );

        assertEquals( Arrays.asList(n1), startingNode.walk("abc") );
        assertEquals( Arrays.asList(n2), startingNode.walk("abd") );
    }


    @Test
    public void givenBlankNode_appendConstant_expectNewNodeToHaveSameLabelAsSourceNode() {
        Node node     = new Node("l1");
        Node nextNode = node.appendConstant("abc").get(0);

        assertEquals( "l1", nextNode.getLabel() );
    }

    @Test
    public void givenBlankNode_appendConstantWithLabel_expectNewNodeToHaveNewLabelAsSourceNode() {
        Node node     = new Node("l1");
        Node nextNode = node.appendConstant("l2","abc").get(0);

        assertEquals( "l2", nextNode.getLabel() );
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
