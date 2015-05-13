package com.mosaic.parser;

import com.mosaic.io.CharPosition;
import com.mosaic.utils.string.CharacterMatcher;
import com.mosaic.utils.string.CharacterMatchers;
import org.junit.Test;

import static org.junit.Assert.*;


public class ParserStreamTest {

    @Test
    public void skipWhitespaceTests() {
        testSkipWhitespace(0, " abc", 1 );
        testSkipWhitespace(4, " abc   d", 3 );
        testSkipWhitespace(0, "abc ", 0 );
        testSkipWhitespace(1, "abc ", 0 );
        testSkipWhitespace(2, "abc ", 0 );
        testSkipWhitespace(3, "abc ", 1 );
        testSkipWhitespace(3, "abc  \n\t a", 5 );
    }

    @Test
    public void getCurrentPositionTests() {
        ParserStream p = new ParserStream( "abc\n123\rabc\r\n123\n\rabc" );

        assertEquals( 'a', p.peekAtCurrentChar() );
        assertCurrentPositionEquals( p, 0, 0, 0 );


        testCurrentPositionAt( p,  0, 'a',   0, 0 );
        testCurrentPositionAt( p,  1, 'b',   1, 0 );
        testCurrentPositionAt( p,  2, 'c',   2, 0 );
        testCurrentPositionAt( p,  4, '1',   0, 1 );
        testCurrentPositionAt( p,  5, '2',   1, 1 );
        testCurrentPositionAt( p,  6, '3',   2, 1 );
        testCurrentPositionAt( p,  7, '\r',  3, 1 );
        testCurrentPositionAt( p,  8, 'a',   4, 1 );
        testCurrentPositionAt( p,  9, 'b',   5, 1 );
        testCurrentPositionAt( p, 10, 'c',   6, 1 );
        testCurrentPositionAt( p, 11, '\r',  7, 1 );
        testCurrentPositionAt( p, 12, '\n',  8, 1 );
        testCurrentPositionAt( p, 13, '1',   0, 2 );
        testCurrentPositionAt( p, 14, '2',   1, 2 );
        testCurrentPositionAt( p, 15, '3',   2, 2 );
        testCurrentPositionAt( p, 16, '\n',  3, 2 );
        testCurrentPositionAt( p, 17, '\r',  0, 3 );
        testCurrentPositionAt( p, 18, 'a', 1, 3 );
        testCurrentPositionAt( p, 19, 'b', 2, 3 );
        testCurrentPositionAt( p, 20, 'c',   3, 3 );

        p.jumpTo( 21 );
        assertTrue( p.isEOF() );
    }

    @Test
    public void consumeNextTests() {
        ParserStream p = new ParserStream( "abc\n123\rabc\r\n123\n\rabc" );

        testConsumeNext( p, 'a', 1, 0 );
        testConsumeNext( p, 'b', 2, 0 );
        testConsumeNext( p, 'c', 3, 0 );
        testConsumeNext( p, '\n', 0, 1 );
        testConsumeNext( p, '1', 1, 1 );
        testConsumeNext( p, '2', 2, 1 );
        testConsumeNext( p, '3', 3, 1 );
        testConsumeNext( p, '\r', 4, 1 );
        testConsumeNext( p, 'a', 5, 1 );
        testConsumeNext( p, 'b', 6, 1 );
        testConsumeNext( p, 'c', 7, 1 );
        testConsumeNext( p, '\r', 8, 1 );
        testConsumeNext( p, '\n', 0, 2 );
        testConsumeNext( p, '1', 1, 2 );
        testConsumeNext( p, '2', 2, 2 );
        testConsumeNext( p, '3', 3, 2 );
        testConsumeNext( p, '\n', 0, 3 );
        testConsumeNext( p, '\r', 1, 3 );
        testConsumeNext( p, 'a', 2, 3 );
        testConsumeNext( p, 'b', 3, 3 );
        testConsumeNext( p, 'c', 4, 3 );

        assertTrue( p.isEOF() );
    }

    @Test
    public void charAtTests() {
        ParserStream p = new ParserStream( "abc\n123\rabc\r\n123\n\rabc" );

        while ( !p.isEOF() ) {  // no matter where the current position is, charAt indexing remains the same
            p.consumeNext();

            testCharAt( p, 0, 'a' );
            testCharAt( p, 1, 'b' );
            testCharAt( p, 2, 'c' );
            testCharAt( p, 3, '\n' );
            testCharAt( p, 4, '1' );
            testCharAt( p, 5, '2' );
            testCharAt( p, 6, '3' );
            testCharAt( p, 7, '\r' );
            testCharAt( p, 8, 'a' );
            testCharAt( p, 9, 'b' );
            testCharAt( p, 10, 'c' );
            testCharAt( p, 11, '\r' );
            testCharAt( p, 12, '\n' );
            testCharAt( p, 13, '1' );
            testCharAt( p, 14, '2' );
            testCharAt( p, 15, '3' );
            testCharAt( p, 16, '\n' );
            testCharAt( p, 17, '\r' );
            testCharAt( p, 18, 'a' );
            testCharAt( p, 19, 'b' );
            testCharAt( p, 20, 'c' );
        }
    }

    @Test
    public void consumeToEndOfLineTests() {
        testConsumeToEndOfLine(  0, "abc \n\n abc 12 \r\n", "abc " );
        testConsumeToEndOfLine(  1, "abc \n\n abc 12 \r\n", "bc " );
        testConsumeToEndOfLine(  2, "abc \n\n abc 12 \r\n", "c " );
        testConsumeToEndOfLine(  3, "abc \n\n abc 12 \r\n", " " );
        testConsumeToEndOfLine(  4, "abc \n\n abc 12 \r\n", "" );
        testConsumeToEndOfLine(  5, "abc \n\n abc 12 \r\n", "" );
        testConsumeToEndOfLine(  6, "abc \n\n abc 12 \r\n", " abc 12 \r" );
        testConsumeToEndOfLine(  7, "abc \n\n abc 12 \r\n", "abc 12 \r" );
        testConsumeToEndOfLine(  8, "abc \n\n abc 12 \r\n", "bc 12 \r" );
        testConsumeToEndOfLine(  9, "abc \n\n abc 12 \r\n", "c 12 \r" );
        testConsumeToEndOfLine( 10, "abc \n\n abc 12 \r\n", " 12 \r" );
        testConsumeToEndOfLine( 11, "abc \n\n abc 12 \r\n", "12 \r" );
        testConsumeToEndOfLine( 12, "abc \n\n abc 12 \r\n", "2 \r" );
        testConsumeToEndOfLine( 13, "abc \n\n abc 12 \r\n", " \r" );
        testConsumeToEndOfLine( 14, "abc \n\n abc 12 \r\n", "\r" );
        testConsumeToEndOfLine( 15, "abc \n\n abc 12 \r\n", "" );
        testConsumeToEndOfLine( 16, "abc \n\n abc 12 \r\n", "" );
    }

    private void testConsumeToEndOfLine( int offset, String text, String expected ) {
        ParserStream p = new ParserStream( text, offset );

        CharPosition from = p.getCurrentPosition();
        int numCharactersConsumed = p.consumeToEndOfLine();
        CharPosition toExc = p.getCurrentPosition();

        assertEquals( expected.length(), numCharactersConsumed );
        assertEquals( expected, p.toString(from, toExc) );
    }

    @Test
    public void consumeTests() {
        testConsumeThatDoesNotMatch( 0, "abc", "bc" );
        testConsumeThatMatches( 0, "abc", "ab" );
        testConsumeThatMatches( 1, "abc", "bc" );
        testConsumeThatDoesNotMatch( 1, "abc", "ab" );
    }

    private void testConsumeThatMatches( int offset, String text, String targetString ) {
        ParserStream     p          = new ParserStream( text, offset );
        CharacterMatcher matcher    = CharacterMatchers.constant(targetString);
        CharPosition     initialPos = p.getCurrentPosition();

        int numCharactersMatched = p.consume( matcher );
        assertEquals( targetString.length(), numCharactersMatched );
        assertEquals( "characters should have been consumed", (int) (initialPos.getCharacterOffset() + targetString.length()), p.getCurrentPosition().getCharacterOffset() );
    }

    private void testConsumeThatDoesNotMatch( int offset, String text, String targetString ) {
        ParserStream     p          = new ParserStream( text, offset );
        CharacterMatcher matcher    = CharacterMatchers.constant(targetString);
        CharPosition     initialPos = p.getCurrentPosition();

        int numCharactersMatched = p.consume( matcher );
        assertEquals( 0, numCharactersMatched );
        assertSame( "no characters should have been consumed", initialPos, p.getCurrentPosition() );
    }

    private void testCharAt( ParserStream p, int pos, char expectedChar ) {
        CharPosition currentPosition = p.getCurrentPosition();

        assertEquals( expectedChar, p.charAt(pos) );
        assertSame( "charAt() must not change the current position", currentPosition, p.getCurrentPosition() );
    }

    private void testConsumeNext( ParserStream in, char expectedNextChar, int expectedCol, int expectedLine ) {
        long prevOffset = in.getCurrentPosition().getCharacterOffset();

        assertFalse( in.isEOF() );

        char actual = in.consumeNext();

        assertEquals( expectedNextChar, actual );
        assertCurrentPositionEquals( in, expectedCol, expectedLine, (int) prevOffset+1 );
    }

    private void testCurrentPositionAt( ParserStream in, int destOffset, char expectedNextChar, int expectedCol, int expectedLine ) {
        in.jumpTo( destOffset );

        assertFalse( in.isEOF() );
        assertEquals( expectedNextChar, in.peekAtCurrentChar() );
        assertCurrentPositionEquals( in, expectedCol, expectedLine, destOffset );
    }

    private void assertCurrentPositionEquals( ParserStream in, int expectedCol, int expectedLine, int expectedOffset ) {
        CharPosition p = in.getCurrentPosition();

        assertEquals( expectedCol,    p.getColumnNumber() );
        assertEquals( expectedLine,   p.getLineNumber() );
        assertEquals( expectedOffset, p.getCharacterOffset() );
    }


    private void testSkipWhitespace( int from, String text, int expectNumCharactersConsumed ) {
        ParserStream in = new ParserStream( text, from );

        int numCharsConsumed = in.skipWhitespace();

        assertEquals( expectNumCharactersConsumed, numCharsConsumed );
        assertEquals( from+numCharsConsumed, in.getCurrentPosition().getCharacterOffset() );
    }



//    public void pushPosition()
//    public void popPosition()
//    rollbackToPreviousPosition()

//    public boolean jumpTo( CharPosition targetPos )

}