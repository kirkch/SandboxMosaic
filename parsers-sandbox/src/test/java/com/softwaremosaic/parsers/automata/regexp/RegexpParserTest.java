package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.LabelNode;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.NodeFormatter;
import com.softwaremosaic.parsers.automata.Nodes;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class RegexpParserTest {

    private RegexpParser parser = new RegexpParser();


    @Test
    public void givenA_expectConstantAOpBack() {
        GraphBuilder op = parser.parse( "a" );

        assertEquals( "a", op.toString() );
    }

    @Test
    public void givenAIgnoreCase_expectConstantAOpBack() {
        GraphBuilder op = parser.parse( "~a" );

        assertEquals( "~a", op.toString() );
        assertEquals( false, ((StringOp) op).isCaseSensitive() );
        assertEquals( "a", ((StringOp) op).getConstant() );
    }

    @Test
    public void givenAEscapedIgnoreCase_expectConstantTildaAOpBack() {
        GraphBuilder op = parser.parse( "\\~a" );

        assertEquals( "\\~a", op.toString() );
        assertEquals( true, ((StringOp) op).isCaseSensitive() );
        assertEquals( "~a", ((StringOp) op).getConstant() );
    }

    @Test
    public void givenABC_expectConstantABCOpBack() {
        GraphBuilder op = parser.parse( "abc" );

        assertEquals( "abc", op.toString() );
    }

    @Test
    public void givenAZeroOrMore_expectConstantAZeroOrMoreOpBack() {
        ZeroOrMoreOp op = (ZeroOrMoreOp) parser.parse( "a*" );

        assertEquals( "(a)*", op.toString() );
    }

    @Test
    public void givenABCZeroOrMore_expectConstantAZeroOrMoreOpBack() {
        ZeroOrMoreOp op = (ZeroOrMoreOp) parser.parse( "abc*" );

        assertEquals( "(abc)*", op.toString() );
    }

    @Test
    public void givenAZeroOrMoreWithBrackets_expectConstantAZeroOrMoreOpBack() {
        ZeroOrMoreOp op = (ZeroOrMoreOp) parser.parse( "(a)*" );

        assertEquals( "(a)*", op.toString() );
    }

    @Test
    public void givenAOneOrMore_expectConstantAOneOrMoreOpBack() {
        OneOrMoreOp op = (OneOrMoreOp) parser.parse( "a+" );

        assertEquals( "(a)+", op.toString() );
    }

    @Test
    public void givenABOneOrMore_expectConstantAOneOrMoreOpBack() {
        OneOrMoreOp op = (OneOrMoreOp) parser.parse( "ab+" );

        assertEquals( "(ab)+", op.toString() );
    }

    @Test
    public void givenABCaseInsensitveOneOrMore_expectConstantAOneOrMoreOpBack() {
        OneOrMoreOp op = (OneOrMoreOp) parser.parse( "~ab+" );

        assertEquals( "(~ab)+", op.toString() );
    }

    @Test
    public void givenAOneOrMoreWithBrackets_expectConstantAZeroOrMoreOpBack() {
        OneOrMoreOp op = (OneOrMoreOp) parser.parse( "(ab)+" );

        assertEquals( "(ab)+", op.toString() );
    }

    @Test
    public void givenABOpt_expectABOptBack() {
        OptionalOp op = (OptionalOp) parser.parse( "ab?" );

        assertEquals( "(ab)?", op.toString() );
    }

    @Test
    public void givenAorB_expectAorBBack() {
        OrOp op = (OrOp) parser.parse( "a|b" );

        assertEquals( "a|b", op.toString() );
    }

    @Test
    public void givenAorBorC_expectAorBorCBack() {
        OrOp op = (OrOp) parser.parse( "a|b|c" );

        assertEquals( "a|b|c", op.toString() );
    }

    @Test
    public void givenABCSelection_expectABCSelectionBack() {
        LabelOp op = (LabelOp) parser.parse( "[abc]" );

        assertEquals( "[abc]", op.toString() );
    }

    @Test
    public void givenRangeSelection_expectRangeSelectionBack() {
        LabelOp op = (LabelOp) parser.parse( "[A-D]" );

        assertEquals( "[A-D]", op.toString() );
    }

    @Test
    public void givenMixOfRangeAndSingleCharSelection_expectItBackAtYa() {
        LabelOp op = (LabelOp) parser.parse( "[abcA-D01-3]" );

        assertEquals( "[abcA-D01-3]", op.toString() );
    }

    @Test
    public void invertedCharSelection() {
        LabelOp op = (LabelOp) parser.parse( "[^abcA-D01-3]" );

        assertEquals( "[^abcA-D01-3]", op.toString() );
    }

    @Test
    public void escapedPartsInCharSelection() {
        LabelOp op = (LabelOp) parser.parse( "[\\^abcA\\-D01-3]" );

        assertEquals( "[\\^abcA\\-D01-3]", op.toString() );
    }

    @Test
    public void nestedBrackets() {
        GraphBuilder op = parser.parse( "((abc|0123)?|[a-z])" );

        assertEquals( "(abc|0123)?|[a-z]", op.toString() );
    }

    @Test
    public void unbalancedBrackets_expectException() {
        try {
            parser.parse( "((abc|0123)?|[a-z]" );

            fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "Expected closing bracket ')' but found instead ''", e.getMessage() );
        }
    }

    @Test
    public void anyChar() {
        GraphBuilder op = parser.parse( "a.c" );

        assertEquals( "a.c", op.toString() );


        Node n = createGraph( op );

        assertEquals( Arrays.asList("1 -a-> 2 -.-> 3 -c-> 4t"), new NodeFormatter().format( n ) );

        assertTrue( walk( n, Arrays.asList('a', 'b', 'c') ) );
        assertTrue( walk( n, Arrays.asList('a', 'a', 'c') ) );
        assertTrue( walk( n, Arrays.asList('a', 'c', 'c') ) );
        assertTrue( walk( n, Arrays.asList('a', '|', 'c') ) );
        assertTrue( walk( n, Arrays.asList('a', '\"', 'c') ) );
        assertTrue( walk( n, Arrays.asList('a', '!', 'c') ) );
        assertTrue( walk( n, Arrays.asList('a', 'f', 'c') ) );

        assertFalse( walk( n, Arrays.asList('b', 'f', 'c') ) );
        assertFalse( walk( n, Arrays.asList( 'a', 'f', 'd' ) ) );
    }

    @Test
    public void whiteSpace() {
        GraphBuilder op = parser.parse( "[ \n\t\r]*" );

        assertEquals( "([ \n\t\r])*", op.toString() );


        Node n = createGraph( op );

        assertTrue( walk( n, Arrays.asList(' ') ) );
        assertTrue( walk( n, Arrays.asList('\t') ) );
        assertTrue( walk( n, Arrays.asList(' ', ' ') ) );
        assertTrue( walk( n, Arrays.asList('\n', '\r', ' ') ) );

        assertFalse( walk( n, Arrays.asList('b', 'f', 'c') ) );
        assertFalse( walk( n, Arrays.asList( 'a', 'f', 'd' ) ) );
    }

    private boolean walk( Node n, List<Character> input ) {
        Nodes pos = new Nodes(n);

        for ( Character c : input ) {
            pos = pos.walk( c );

            if ( pos.isEmpty() ) {
                return false;
            }
        }

        return true;
    }

    private Node createGraph( GraphBuilder op ) {
        Node n = new LabelNode();

        op.appendTo( n );

        return n;
    }
}