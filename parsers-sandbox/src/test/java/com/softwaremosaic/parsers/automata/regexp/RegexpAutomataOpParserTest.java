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
        AutomataOp op = parser.parse( "a" );

        assertEquals( "a", op.toString() );
    }

    @Test
    public void givenAIgnoreCase_expectConstantAOpBack() {
        AutomataOp op = parser.parse( "~a" );

        assertEquals( "~a", op.toString() );
        assertEquals( false, ((ConstantOp) op).isCaseSensitive() );
        assertEquals( "a", ((ConstantOp) op).getConstant() );
    }

    @Test
    public void givenABC_expectConstantABCOpBack() {
        AutomataOp op = parser.parse( "abc" );

        assertEquals( "abc", op.toString() );
    }

    @Test
    public void givenAZeroOrMore_expectConstantAZeroOrMoreOpBack() {
        ZeroOrMoreOp op = (ZeroOrMoreOp) parser.parse( "a*" );

        assertEquals( "(a)*", op.toString() );
    }

}
