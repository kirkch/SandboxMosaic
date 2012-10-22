package com.mosaic.parsers.pull;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 *
 */
public class Tokenizer_skipTests {

    @Test
    public void matchPartOfString_skipMatch_expectNoChangeToPosition() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        in.walkConstant( "foo" );
        in.skip();

        assertFalse( in.isEOF() );
        assertPosition( 1, 4, in );
        assertEquals( "", in.consume() );
    }

    @Test
    public void matchPartOfString_skipMatch_completeMatchingRestOfString() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        in.walkConstant( "foo" );
        in.skip();
        in.walkConstant( " bar" );

        assertEquals( " bar", in.consume() );
        assertPosition( 1, 8, in );
        assertTrue( in.isEOF() );
    }

    @Test
    public void skipLastMatchOfString_expectEOF() throws IOException {
        Tokenizer in = new Tokenizer( new StringReader("foo bar") );

        in.walkConstant( "foo" );
        in.walkConstant( " bar" );
        in.skip();

        assertPosition( 1, 8, in );
        assertTrue( in.isEOF() );
    }

    private void assertPosition( int expectedLine, int expectedColumn, Tokenizer tokenizer ) {
        assertEquals( "line numbers do not match", expectedLine, tokenizer.getLineNumber() );
        assertEquals( "column numbers do not match", expectedColumn, tokenizer.getColumnNumber() );
    }

}
