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

}
