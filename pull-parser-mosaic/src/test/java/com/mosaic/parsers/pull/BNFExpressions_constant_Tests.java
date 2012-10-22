package com.mosaic.parsers.pull;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * Tests for the object created by factory method: BNFExpressions.constant
 */
public class BNFExpressions_constant_Tests {

    @Test
    public void givenInputThatDoesNotMatchTargetConstant_parseOptional_expectNone() throws IOException {
        BNFExpression exp = BNFExpressions.constant( "public" );
        Tokenizer     in  = new Tokenizer( new StringReader("private Account") );

        Match<String> result = exp.parseOptional( null, in );

        assertTrue( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );
        assertNull( result.getValue() );
    }

    @Test
    public void givenInputThatMatchesTargetConstant_parseOptional_expectMatch() throws IOException {
        BNFExpression exp = BNFExpressions.constant( "private" );
        Tokenizer     in  = new Tokenizer( new StringReader("private Account") );

        Match<String> result = exp.parseOptional( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1, 1, result );
        assertEquals( "private", result.getValue() );
    }

    @Test
    public void givenInputThatMatchesTargetConstant_parseMandatory_expectMatch() throws IOException {
        BNFExpression exp = BNFExpressions.constant( "private" );
        Tokenizer     in  = new Tokenizer( new StringReader("private Account") );

        Match<String> result = exp.parseMandatory( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1, 1, result );
        assertEquals( "private", result.getValue() );
    }

    @Test
    public void givenInputThatDoesNotMatchTargetConstant_parseMandatory_expectError() throws IOException {
        BNFExpression exp = BNFExpressions.constant( "private" );
        Tokenizer     in  = new Tokenizer( new StringReader("public Account") );

        Match<String> result = exp.parseMandatory( null, in );

        assertFalse( result.isNone() );
        assertFalse( result.hasValue() );
        assertTrue( result.isError() );
        assertPos( 1, 1, result );

        assertEquals( "expected 'private'", result.getError() );
    }

    private void assertPos( int expectedLine, int expectedColumn, Match actualResult ) {
        assertEquals( expectedLine, actualResult.getLine() );
        assertEquals( expectedColumn, actualResult.getColumn() );
    }

}
