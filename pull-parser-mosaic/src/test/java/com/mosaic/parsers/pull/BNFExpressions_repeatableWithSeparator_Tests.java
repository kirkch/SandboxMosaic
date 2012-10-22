package com.mosaic.parsers.pull;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mosaic.parsers.pull.BNFExpressions.*;
import static org.junit.Assert.*;

/**
 *
 */
public class BNFExpressions_repeatableWithSeparator_Tests {

    @Test
    public void givenEmptyInput_parseOptional_expectEmptyList() throws IOException {
        BNFExpression exp = repeatableWithSeparator( constant("Hello"), constant(",") );
        Tokenizer     in  = new Tokenizer( new StringReader("") );

        Match<List> result = exp.parseOptional( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );
        assertEquals( new ArrayList(), result.getValue() );
    }

    @Test
    public void givenEmptyInput_parseMandatory_expectEmptyList() throws IOException {
        BNFExpression exp = repeatableWithSeparator( constant( "Hello" ), constant(",") );
        Tokenizer     in  = new Tokenizer( new StringReader("") );

        Match<List> result = exp.parseMandatory( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );
        assertEquals( new ArrayList(), result.getValue() );
    }

    @Test
    public void givenNoneMatchingInput_parseOptional_expectEmptyList() throws IOException {
        BNFExpression exp = repeatableWithSeparator( constant( "Hello" ), constant(",") );
        Tokenizer     in  = new Tokenizer( new StringReader("Welcome") );

        Match<List> result = exp.parseOptional( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );
        assertEquals( new ArrayList(), result.getValue() );
    }

    @Test
    public void givenNoneMatchingInput_parseMandatory_expectEmptyList() throws IOException {
        BNFExpression exp = repeatableWithSeparator( constant( "Hello" ), constant(",") );
        Tokenizer     in  = new Tokenizer( new StringReader("Welcome") );

        Match<List> result = exp.parseMandatory( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1,1, result );
        assertEquals( new ArrayList(), result.getValue() );
    }

    @Test
    public void givenSingleMatchOfWholeInput_parseOptional_expectSingleValueInList() throws IOException {
        BNFExpression exp = repeatableWithSeparator( constant( "Hello"), constant(",") );
        Tokenizer     in  = new Tokenizer( new StringReader("Hello") );

        Match<List> result = exp.parseOptional( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1, 1, result );
        assertEquals( Arrays.asList( "Hello" ), result.getValue() );
    }

    @Test
    public void givenSingleMatchOfWholeInput_parseMandatory_expectSingleValueInList() throws IOException {
        BNFExpression exp = repeatableWithSeparator( constant( "Hello" ), constant(",") );
        Tokenizer     in  = new Tokenizer( new StringReader("Hello") );

        Match<List> result = exp.parseMandatory( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1, 1, result );
        assertEquals( Arrays.asList( "Hello" ), result.getValue() );
    }

    @Test
    public void givenMultipleMatchsOfWholeInput_parseOptional_expectMultipleValuesInList() throws IOException {
        BNFExpression exp = repeatableWithSeparator( regexp("[a-zA-Z0-9]+"), constant(",") );
        Tokenizer in = createTokenizer( "Hello, Jim" );

        Match<List> result = exp.parseOptional( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1, 1, result );
        assertEquals( Arrays.asList("Hello","Jim"), result.getValue() );
    }

    @Test
    public void givenMultipleMatchsOfWholeInput_parseMandatory_expectMultipleValuesInList() throws IOException {
        BNFExpression exp = repeatableWithSeparator( regexp("[a-zA-Z0-9]+"), constant(",") );
        Tokenizer in = createTokenizer( "Hello, Jim" );

        Match<List> result = exp.parseMandatory( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1, 1, result );
        assertEquals( Arrays.asList("Hello","Jim"), result.getValue() );
    }

    @Test
    public void missingSeparator_parseOptional_expectStopParsingAfterFirstElement() throws IOException {
        BNFExpression exp = repeatableWithSeparator( regexp("[a-zA-Z0-9]+"), constant(",") );
        Tokenizer in = createTokenizer( "Hello Jim" );

        Match<List> result = exp.parseOptional( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1, 1, result );
        assertEquals( Arrays.asList("Hello"), result.getValue() );
    }

    @Test
    public void missingSeparator_parseMandatory_expectStopParsingAfterFirstElement() throws IOException {
        BNFExpression exp = repeatableWithSeparator( regexp("[a-zA-Z0-9]+"), constant(",") );
        Tokenizer in = createTokenizer( "Hello Jim" );

        Match<List> result = exp.parseMandatory( null, in );

        assertFalse( result.isNone() );
        assertTrue( result.hasValue() );
        assertPos( 1, 1, result );
        assertEquals( Arrays.asList("Hello"), result.getValue() );
    }

    @Test
    public void missingValueAfterSeparator_parseOptional_expectErrorAsValueAfterSeperatorIsMandatory() throws IOException {
        BNFExpression exp = repeatableWithSeparator( regexp("[a-zA-Z0-9]+"), constant(",") );
        Tokenizer in = createTokenizer( "Hello, " );

        Match<List> result = exp.parseMandatory( null, in );

        assertFalse( result.isNone() );
        assertFalse( result.hasValue() );
        assertPos( 1, 7, result );
        assertNull( result.getValue() );
        assertEquals( "expected '[a-zA-Z0-9]+'", result.getError() );
    }

    private Tokenizer createTokenizer( String text ) {
        Tokenizer in  = new Tokenizer( new StringReader( text ) );
        in.autoskipWhitespace( true );

        return in;
    }


    private void assertPos( int expectedLine, int expectedColumn, Match actualResult ) {
        assertEquals( expectedLine, actualResult.getLine() );
        assertEquals( expectedColumn, actualResult.getColumn() );
    }
}
