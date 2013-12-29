package com.softwaremosaic.parsers.automata.regexp;

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

//    @Test
    public void givenABCSelection_expectABCSelectionBack() {
        LabelOp op = (LabelOp) parser.parse( "[abc]" );

        assertEquals( "[abc]", op.toString() );
    }

//    @Test
//    public void givenABCSelection_expectABCSelectionBack() {
//        LabelOp op = (LabelOp) parser.parse( "[acdA-D]" );
//
//        assertEquals( "[acdA-D]", op.toString() );
//    }


    // or chars
    // char range
    // mixed char selection
    // not chars
    // nested brackets

    //escapedChars
}
