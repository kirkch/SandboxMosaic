package com.mosaic.parsers.pull;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 *
 */
public class Tokenizer_undoWalkTests {

    @Test
    public void matchPartOfString_rewindMatch_expectNoChangeToPosition() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        in.walkConstant( "foo" );
        in.undoWalk();

        assertFalse( in.isEOF() );
        assertPosition( 1, 1, in );
        assertEquals( "", in.consume() );
    }

    @Test
    public void matchPartOfString_rewindMatch_completeMatchingRestOfString() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        in.walkConstant( "foo" );
        in.undoWalk();
        in.walkConstant( "foo bar" );

        assertEquals( "foo bar", in.consume() );
        assertPosition( 1, 8, in );
        assertTrue( in.isEOF() );
    }

    @Test
    public void rewindLastMatchOfString_expectNotEOF() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        in.walkConstant( "foo" );
        in.consume();
        in.walkConstant( " bar" );
        in.undoWalk();

        assertPosition( 1, 4, in );
        assertFalse( in.isEOF() );
    }

    private void assertPosition( int expectedLine, int expectedColumn, Tokenizer tokenizer ) {
        assertEquals( "line numbers do not match", expectedLine, tokenizer.getLineNumber() );
        assertEquals( "column numbers do not match", expectedColumn, tokenizer.getColumnNumber() );
    }

}
