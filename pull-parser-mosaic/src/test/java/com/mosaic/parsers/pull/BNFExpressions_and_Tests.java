package com.mosaic.parsers.pull;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static com.mosaic.parsers.pull.BNFExpressions.*;
import static org.junit.Assert.*;

/**
 *
 */
public class BNFExpressions_and_Tests {

    @Test
    public void givenInputThatDoesNotMatchTargetConstant_parseOptional_expectNone() throws IOException {
        BNFExpression exp = and( constant("public"), constant("Account") );
        Tokenizer     in  = new Tokenizer( new StringReader("private Account") );

        Match<String> result = exp.parseOptional( null, in );

        assertTrue( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );
        assertNull( result.getValue() );
    }

    @Test
    public void givenInputThatFullyMatchsAllTargetExpressions_parseOptional_expectMatch() throws IOException {
        BNFExpression exp = and( constant("private"), constant("Account") );
        Tokenizer     in  = createTokenizer( "private Account" );

        Match<List<String>> result = exp.parseOptional( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );
        assertEquals( Arrays.asList( "private", "Account" ), result.getValue() );
    }

    @Test
    public void givenInputThatMatchsFirstExpressionButFailsSecond_parseOptional_expectError() throws IOException {
        BNFExpression exp = and( constant("private"), constant("House") );
        Tokenizer     in  = createTokenizer( "private Account" );

        Match<List<String>> result = exp.parseOptional( null, in );

        assertTrue( result.isError() );
        assertFalse( result.isNone() );
        assertFalse( result.hasValue() );
        assertPos( 1,8, result );
        assertEquals( "expected 'House'", result.getError() );
    }

    @Test
    public void givenInputThatFullyMatchsAllTargetExpressions_parseOptionalWithDiscardCommand_expectDiscardedValuesToGo() throws IOException {
        BNFExpression exp = and( discard(constant("Hello")), discard( constant( "Mr" ) ), regexp( "[A-Za-z]+" ) );
        Tokenizer     in  = createTokenizer( "Hello Mr Kirk" );

        Match<List<String>> result = exp.parseOptional( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );
        assertEquals( Arrays.asList( "Kirk" ), result.getValue() );
    }

    @Test
    public void givenDiscardOptionalDiscard_parseOptionalWithStringThatMatchesWithNone_expectSuccessAndFullParse() throws IOException {
        BNFExpression exp = and( discard(constant("(")), optional( regexp( "[A-Za-z]+" ) ), discard(constant(")")) );
        Tokenizer     in  = createTokenizer( "()" );

        Match<List<String>> result = exp.parseOptional( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );

        List parsedValue = result.getValue();
        assertEquals( 1, parsedValue.size() );
        assertNull( parsedValue.get(0) );

        assertEquals( 3, in.getColumnNumber() ); // ensure the full input has been parsed
    }




    @Test
    public void givenInputThatDoesNotMatchTargetConstant_parseMandatory_expectError() throws IOException {
        BNFExpression exp = and( constant("public"), constant("Account") );
        Tokenizer     in  = new Tokenizer( new StringReader("private Account") );

        Match<String> result = exp.parseMandatory( null, in );

        assertFalse( result.isNone() );
        assertFalse( result.hasValue() );
        assertTrue( result.isError() );
        assertPos( 1, 1, result );

        assertEquals( "expected 'public'", result.getError() );
    }

    @Test
    public void givenInputThatFullyMatchsAllTargetExpressions_parseMandatory_expectMatch() throws IOException {
        BNFExpression exp = and( constant("private"), constant("Account") );
        Tokenizer     in  = createTokenizer( "private Account" );

        Match<List<String>> result = exp.parseMandatory( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );
        assertEquals( Arrays.asList( "private", "Account" ), result.getValue() );
    }

    @Test
    public void givenInputThatMatchsFirstExpressionButFailsSecond_parseMandatory_expectError() throws IOException {
        BNFExpression exp = and( constant("private"), constant("House") );
        Tokenizer     in  = createTokenizer( "private Account" );

        Match<List<String>> result = exp.parseMandatory( null, in );

        assertTrue( result.isError() );
        assertFalse( result.isNone() );
        assertFalse( result.hasValue() );
        assertPos( 1,8, result );
        assertEquals( "expected 'House'", result.getError() );
    }

    @Test
    public void givenInputThatFullyMatchsAllTargetExpressions_parseMandatoryWithDiscardCommand_expectDiscardedValuesToGo() throws IOException {
        BNFExpression exp = and( discard(constant("Hello")), discard(constant("Mr")), regexp("[A-Za-z]+") );
        Tokenizer     in  = createTokenizer( "Hello Mr Kirk" );

        Match<List<String>> result = exp.parseMandatory( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );
        assertEquals( Arrays.asList( "Kirk" ), result.getValue() );
    }



    private void assertPos( int expectedLine, int expectedColumn, Match actualResult ) {
        assertEquals( expectedLine, actualResult.getLine() );
        assertEquals( expectedColumn, actualResult.getColumn() );
    }

    private Tokenizer createTokenizer( String input ) {
        Tokenizer t = new Tokenizer( new StringReader(input) );

        t.autoskipWhitespace( true );

        return t;
    }
}
