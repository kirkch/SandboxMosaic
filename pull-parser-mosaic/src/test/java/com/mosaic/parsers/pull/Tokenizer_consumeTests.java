package com.mosaic.parsers.pull;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 *
 */
public class Tokenizer_consumeTests {

    @Test
    public void consumeWithoutWalkingOverAnyCharacters_expectEmptyString() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        assertFalse( in.isEOF() );
        assertPosition( 1, 1, in );
        assertEquals( "", in.consume() );
    }

    @Test
    public void walkFailed_expectNoTokenMoved() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        assertFalse( in.walkConstant( "abc" ) );

        assertPosition( 1, 1, in );
        assertEquals( "", in.consume() );
    }

    @Test
    public void singleWalk_expectToken() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        assertTrue( in.walkConstant( "foo" ) );

        assertFalse( in.isEOF() );
        assertPosition( 1, 1, in );
        assertEquals( "foo", in.consume() );
    }

    @Test
    public void multipleWalkInOneGo_expectToken() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        assertTrue( in.walkConstant( "foo" ) );
        assertTrue( in.walkConstant(" ") );
        assertFalse( in.isEOF() );
        assertTrue( in.walkConstant( "bar" ) );

        assertTrue( in.isEOF() );
        assertPosition( 1, 1, in );
        assertEquals( "foo bar", in.consume() );
    }

    @Test
    public void autoskipwhitespace() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );
        in.autoskipWhitespace( true );

        assertTrue( in.walkConstant("foo") );
        assertTrue( in.walkConstant( "bar" ) );

        assertTrue( in.isEOF() );
        assertPosition( 1, 1, in );
        assertEquals( "foo bar", in.consume() );
        assertPosition( 1, 8, in );
    }

    @Test
    public void autoskipwhitespace_withNewLine() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo\nbar") );
        in.autoskipWhitespace( true );

        assertTrue( in.walkConstant("foo") );
        assertTrue( in.walkConstant( "bar" ) );

        assertTrue( in.isEOF() );
        assertPosition( 1, 1, in );
        assertEquals( "foo\nbar", in.consume() );
        assertPosition( 2, 3, in );
    }

    @Test
    public void autoskipwhitespace_expectWhitespaceTobeKeptInMiddleOfToken() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );
        in.autoskipWhitespace( true );

        assertTrue( in.walkConstant("foo") );
        assertTrue( in.walkConstant( "bar" ) );

        assertTrue( in.isEOF() );
        assertPosition( 1, 1, in );
        assertEquals( "foo bar", in.consume() );
        assertPosition( 1, 8, in );
        assertTrue( in.isEOF() );
    }

    @Test
    public void autoskipwhitespace_expectWhitespaceTobeSkippedAtStartAndEndOfToken() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader(" foo bar ") );
        in.autoskipWhitespace( true );

        assertTrue( in.walkConstant( "foo" ) );
        assertTrue( in.walkConstant( "bar" ) );

        assertTrue( in.isEOF() );
        assertPosition( 1, 1, in );
        assertEquals( "foo bar", in.consume() );
        assertPosition( 1, 10, in );
        assertTrue( in.isEOF() );
    }

    @Test
    public void checkIsEOFSupportsAutoSkippingOfWhitespace() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader(" foo bar   ") );
        in.autoskipWhitespace( true );

        assertTrue( in.walkConstant( "foo" ) );
        assertFalse( in.isEOF() );
        assertTrue( in.walkConstant( "bar" ) );

        assertTrue( in.isEOF() );
        assertPosition( 1, 1, in );
        assertEquals( "foo bar", in.consume() );
        assertPosition( 1, 12, in );
        assertTrue( in.isEOF() );
    }

    @Test
    public void walkConsumeWalkConsume_expectSuccess() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        assertTrue( in.walkConstant( "foo" ) );
        assertFalse( in.isEOF() );
        assertPosition( 1, 1, in );
        assertEquals( "foo", in.consume() );

        assertTrue( in.walkConstant( " " ) );
        assertFalse( in.isEOF() );
        assertPosition( 1, 4, in );
        assertEquals( " ", in.consume() );
        assertFalse( in.isEOF() );
    }

    @Test
    public void walkAndConsumeEntireInputInMultipleGoes_expectSuccessAndEndOfStringToBeDetected() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        in.walkConstant( "foo" );
        in.consume();

        in.walkConstant(" ");
        in.consume();

        assertFalse( in.isEOF() );
        assertTrue( in.walkConstant("bar") );
        assertTrue( in.isEOF() );
        assertPosition( 1, 5, in );
        assertEquals( "bar", in.consume() );

        assertTrue( in.isEOF() );
    }

    @Test
    public void firstMatchFails_expectNoMovementOfTokenizer() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        assertFalse( in.walkConstant( "oo" ) );
        assertEquals( "", in.consume() );
        assertPosition( 1, 1, in );
    }

    @Test
    public void secondMatchFails_expectNoMovementOfTokenizer() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        assertTrue( in.walkConstant( "f" ) );
        assertFalse( in.walkConstant( "foo" ) );

        assertEquals( "f", in.consume() );
        assertPosition( 1, 2, in );
    }

    @Test
    public void consumeWithNoPreceedingWalk_expectEmptyStringAndNoMoveOfIndexes() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        assertEquals( "", in.consume() );
        assertPosition( 1, 1, in );
    }

    private void assertPosition( int expectedLine, int expectedColumn, Tokenizer tokenizer ) {
        assertEquals( "line numbers do not match", expectedLine, tokenizer.getLineNumber() );
        assertEquals( "column numbers do not match", expectedColumn, tokenizer.getColumnNumber() );
    }
}
