package com.mosaic.parsers.pull;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class BNFExpressions_eof_Tests {

    @Test
    public void givenEmptyInput_parseOptional_expectNone() throws IOException {
        BNFExpression exp = BNFExpressions.eof();
        Tokenizer     in  = new Tokenizer( new StringReader("") );

        Match<String> result = exp.parseOptional( null, in );

        assertTrue( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );
        assertNull( result.getValue() );
    }

    @Test
    public void givenEmptyInput_parseMandatory_expectNone() throws IOException {
        BNFExpression exp = BNFExpressions.eof();
        Tokenizer     in  = new Tokenizer( new StringReader("") );

        Match<String> result = exp.parseMandatory( null, in );

        assertTrue( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );
        assertNull( result.getValue() );
    }

    @Test
    public void givenNoneEmptyInput_parseMandatory_expectError() throws IOException {
        BNFExpression exp = BNFExpressions.eof();
        Tokenizer     in  = new Tokenizer( new StringReader("hello") );

        Match<String> result = exp.parseMandatory( null, in );

        assertFalse( result.isNone() );
        assertFalse( result.hasValue() );
        assertPos( 1,1, result );
        assertEquals( "expected EOF", result.getError() );
    }

    @Test
    public void givenNoneEmptyInput_parseOptional_expectError() throws IOException {
        BNFExpression exp = BNFExpressions.eof();
        Tokenizer     in  = new Tokenizer( new StringReader("hello") );

        Match<String> result = exp.parseOptional( null, in );

        assertFalse( result.isNone() );
        assertFalse( result.hasValue() );
        assertPos( 1, 1, result );
        assertEquals( "expected EOF", result.getError() );
    }

    private void assertPos( int expectedLine, int expectedColumn, Match actualResult ) {
        assertEquals( expectedLine, actualResult.getLine() );
        assertEquals( expectedColumn, actualResult.getColumn() );
    }

}
