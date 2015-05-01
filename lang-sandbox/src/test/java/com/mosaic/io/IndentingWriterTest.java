package com.mosaic.io;

import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;


public class IndentingWriterTest {

    @Test
    public void givenANewWriter_writeNothing_expectNothingToBeWritten() {
        StringWriter sw = new StringWriter();
        
        new IndentingWriter( sw );

        assertEquals( "", sw.toString() );
    }

    @Test
    public void givenANewWriter_writeAString_expectNoIndentation() {
        StringWriter    sw = new StringWriter();
        IndentingWriter iw = new IndentingWriter( sw );

        iw.print("hello");

        assertEquals( "hello", sw.toString() );
    }

    @Test
    public void givenANewWriter_incIndentationThenWriteAString_expectOneLevelOfIndentation() {
        StringWriter    sw = new StringWriter();
        IndentingWriter iw = new IndentingWriter( sw );

        iw.incIndent();
        iw.print("hello");

        assertEquals( "  hello", sw.toString() );
    }

    @Test
    public void givenANewWriter_incThenDecIndentationThenWriteAString_expectNoIndentation() {
        StringWriter    sw = new StringWriter();
        IndentingWriter iw = new IndentingWriter( sw );

        iw.incIndent();
        iw.decIndent();
        iw.print("hello");

        assertEquals( "hello", sw.toString() );
    }

    @Test
    public void givenANewWriter_writeSeveralLinesOfTextAtVaryingIndentationLevels() {
        StringWriter    sw = new StringWriter();
        IndentingWriter iw = new IndentingWriter( sw );

        iw.incIndent();
        iw.println( "hello" );

        iw.incIndent();
        iw.println( "world" );

        iw.decIndent();
        iw.println( "YOU" );
        iw.println( "ROCK" );

        String[] expectation = new String[] {
            "  hello",
            "    world",
            "  YOU",
            "  ROCK"
        };

        assertArrayEquals( expectation, sw.toString().split(SystemX.NEWLINE) );
    }

}