package com.mosaic.collections.trie.builder;

import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodeFormatter;
import com.mosaic.collections.trie.CharacterNodes;
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
        TrieBuilderOp op = parser.parse( "a" );

        assertEquals( "a", op.toString() );
    }

    @Test
    public void givenAIgnoreCase_expectConstantAOpBack() {
        TrieBuilderOp op = parser.parse( "~a" );

        assertEquals( "~a", op.toString() );
        assertEquals( false, ((StringOp) op).isCaseSensitive() );
        assertEquals( "a", ((StringOp) op).getConstant() );
    }

    @Test
    public void givenAEscapedIgnoreCase_expectConstantTildaAOpBack() {
        TrieBuilderOp op = parser.parse( "\\~a" );

        assertEquals( "\\~a", op.toString() );
        assertEquals( true, ((StringOp) op).isCaseSensitive() );
        assertEquals( "~a", ((StringOp) op).getConstant() );
    }

    @Test
    public void givenABC_expectConstantABCOpBack() {
        TrieBuilderOp op = parser.parse( "abc" );

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
        TrieBuilderOp op = parser.parse( "((abc|0123)?|[a-z])" );

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
        TrieBuilderOp op = parser.parse( "a.c" );

        assertEquals( "a.c", op.toString() );


        CharacterNode n = createGraph( op );

        assertEquals( Arrays.asList("1 -a-> 2 -.-> 3 -c-> 4"), new CharacterNodeFormatter().format( n ) );

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
        TrieBuilderOp op = parser.parse( "[ \n\t\r]*" );

        assertEquals( "([ \n\t\r])*", op.toString() );


        CharacterNode n = createGraph( op );

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
        Map<String,TrieBuilderOp> ops = MapUtils.asMap(
            "op1", parser.parse( "abc" )
        );

        TrieBuilderOp op2 = parser.parse( "$op1", ops );

        assertEquals( "$op1", op2.toString() );


        CharacterNode n = createGraph( op2 );

        assertEquals( Arrays.asList("1 -a-> 2 -b-> 3 -c-> 4"), new CharacterNodeFormatter().format( n ) );

//        assertTrue( walk( n, Arrays.asList('a', 'b', 'c') ) );
//        assertTrue( walk( n, Arrays.asList('a', 'a', 'c') ) );
//        assertTrue( walk( n, Arrays.asList('a', 'c', 'c') ) );
//        assertTrue( walk( n, Arrays.asList('a', '|', 'c') ) );
//        assertTrue( walk( n, Arrays.asList('a', '\"', 'c') ) );
//        assertTrue( walk( n, Arrays.asList('a', '!', 'c') ) );
//        assertTrue( walk( n, Arrays.asList('a', 'f', 'c') ) );
//
//        assertFalse( walk( n, Arrays.asList('b', 'f', 'c') ) );
//        assertFalse( walk( n, Arrays.asList( 'a', 'f', 'd' ) ) );
    }




    private boolean walk( CharacterNode n, List<Character> input ) {
        CharacterNodes pos = new CharacterNodes(n);

        for ( Character c : input ) {
            pos = pos.fetch( c );

            if ( pos.isEmpty() ) {
                return false;
            }
        }

        return true;
    }

    private CharacterNode createGraph( TrieBuilderOp op ) {
        CharacterNode n = new CharacterNode();

        op.appendTo( n );

        return n;
    }
}
