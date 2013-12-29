package com.softwaremosaic.parsers.automata.regexp;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class RegexpAutomataOpParserTest {

    private RegexpAutomataOpParser parser = new RegexpAutomataOpParser();


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

//    @Test
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

            Assert.fail( "expected IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            Assert.assertEquals( "Expected closing bracket ')' at index 18 of '((abc|0123)?|[a-z]'", e.getMessage() );
        }
    }



    //escapedChars
    // .
}
