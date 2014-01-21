package com.mosaic.parser.graph.builder;

import com.mosaic.parser.ProductionRule;
import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.NodeFormatter;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.utils.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class RegexpParserTest {

    private RegexpParser parser = new RegexpParser();


    @Test
    public void givenA_expectConstantAOpBack() {
        NodeBuilder op = parser.parse( "a" );

        assertEquals( "a", op.toString() );
    }

    @Test
    public void givenAIgnoreCase_expectConstantAOpBack() {
        NodeBuilder op = parser.parse( "~a" );

        assertEquals( "~a", op.toString() );
        assertEquals( false, ((StringOp) op).isCaseSensitive() );
        assertEquals( "a", ((StringOp) op).getConstant() );
    }

    @Test
    public void givenAEscapedIgnoreCase_expectConstantTildaAOpBack() {
        NodeBuilder op = parser.parse( "\\~a" );

        assertEquals( "\\~a", op.toString() );
        assertEquals( true, ((StringOp) op).isCaseSensitive() );
        assertEquals( "~a", ((StringOp) op).getConstant() );
    }

    @Test
    public void givenABC_expectConstantABCOpBack() {
        NodeBuilder op = parser.parse( "abc" );

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
        PredicateOp op = (PredicateOp) parser.parse( "[abc]" );

        assertEquals( "[abc]", op.toString() );
    }

    @Test
    public void givenRangeSelection_expectRangeSelectionBack() {
        PredicateOp op = (PredicateOp) parser.parse( "[A-D]" );

        assertEquals( "[A-D]", op.toString() );
    }

    @Test
    public void givenMixOfRangeAndSingleCharSelection_expectItBackAtYa() {
        PredicateOp op = (PredicateOp) parser.parse( "[abcA-D01-3]" );

        assertEquals( "[abcA-D01-3]", op.toString() );
    }

    @Test
    public void invertedCharSelection() {
        PredicateOp op = (PredicateOp) parser.parse( "[^abcA-D01-3]" );

        assertEquals( "[^abcA-D01-3]", op.toString() );
    }

    @Test
    public void escapedPartsInCharSelection() {
        PredicateOp op = (PredicateOp) parser.parse( "[\\^abcA\\-D01-3]" );

        assertEquals( "[\\^abcA\\-D01-3]", op.toString() );
    }

    @Test
    public void nestedBrackets() {
        NodeBuilder op = parser.parse( "((abc|0123)?|[a-z])" );

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
        NodeBuilder op = parser.parse( "a.c" );

        assertEquals( "a.c", op.toString() );


        Node n = createGraph( op );

        assertEquals( Arrays.asList("1 -a-> 2 -.-> 3 -c-> 4"), new NodeFormatter().format( n ) );

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
        NodeBuilder op = parser.parse( "[ \n\t\r]*" );

        assertEquals( "([ \n\t\r])*", op.toString() );


        Node n = createGraph( op );

        assertTrue( walk( n, Arrays.asList(' ') ) );
        assertTrue( walk( n, Arrays.asList('\t') ) );
        assertTrue( walk( n, Arrays.asList(' ', ' ') ) );
        assertTrue( walk( n, Arrays.asList('\n', '\r', ' ') ) );

        assertFalse( walk( n, Arrays.asList('b', 'f', 'c') ) );
        assertFalse( walk( n, Arrays.asList( 'a', 'f', 'd' ) ) );
    }


// EMBEDDED REFERENCES

    @Test
    public void givenEmptyBuilder_declareRuleWithSingleEmbeddedRuleThatDoesNotExist_expectError() {
        try {
            parser.parse( "$rule2*" );

            Assert.fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            Assert.assertEquals( "'rule2' has not been declared yet; forward references are not supported", e.getMessage() );
        }
    }

    @Test
    public void givenRule1_declarePatternWithSingleReference_expectParse() {
        Map<String,ProductionRule> ops = MapUtils.asMap(
            "op1", createRule("op1", "abc")
        );

        NodeBuilder op2 = parser.parse( "$op1", ops );

        assertEquals( "$op1", op2.toString() );


        Node n = createGraph( op2 );

        assertEquals( Arrays.asList("1 -$op1-> 2"), new NodeFormatter().format( n ) );
    }

    @Test
    public void givenTwoRules_declarePatternWithSingleReference_expectParse() {
        Map<String,ProductionRule> ops = MapUtils.asMap(
            "op1", createRule("op1", "abc"),
            "op2", createRule("op2", "123")
        );

        NodeBuilder rootOp = parser.parse( "$op1$op2", ops );

        assertEquals( "$op1 $op2", rootOp.toString() );


        Node n = createGraph( rootOp );

        assertEquals( Arrays.asList("1 -$op1-> 2 -$op2-> 3"), new NodeFormatter().format( n ) );
    }

    @Test
    public void givenTwoEmbeddedRulesWithWhitespace_declarePatternWithSingleReference_expectParse() {
        Map<String,ProductionRule> ops = MapUtils.asMap(
            "op1", createRule("op1", "abc"),
            "op2", createRule("op2", "123")
        );

        NodeBuilder rootOp = parser.parse( "$op1  $op2", ops );

        assertEquals( "$op1 $op2", rootOp.toString() );


        Node n = createGraph( rootOp );

        assertEquals( Arrays.asList("1 -$op1-> 2 -$op2-> 3"), new NodeFormatter().format( n ) );
    }

    private ProductionRule createRule( String ruleName, String regExp ) {
        NodeBuilder b = parser.parse( "abc" );
        Node start = new Node();
        Nodes end = b.appendTo( start );

        return new ProductionRule( ruleName, start, end );
    }




    private boolean walk( Node n, List<Character> input ) {
        Nodes pos = new Nodes(n);

        for ( Character c : input ) {
            pos = pos.fetch( c );

            if ( pos.isEmpty() ) {
                return false;
            }
        }

        return true;
    }

    private Node createGraph( NodeBuilder op ) {
        Node n = new Node();

        op.appendTo( n );

        return n;
    }
}
