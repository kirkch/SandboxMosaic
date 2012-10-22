package com.mosaic.collections;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class CircularBufferChar_popStringLHSTests {

    @Test
    public void popZeroLengthStringFromEmptyRing_expectEmptyString() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertEquals( "", buf.popStringLHS(0) );
    }

    @Test
    public void tryPoppingMoreCharactersThanExistInRing_fromEmptyRing_expectException() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        try {
            buf.popStringLHS(1);
            fail( "expected ArrayIndexOutOfBoundsException" );
        } catch ( ArrayIndexOutOfBoundsException e ) {
            assertEquals( "cannot read 1 characters from a ring that only contains 0 characters", e.getMessage() );
        }
    }

    @Test
    public void popTwoCharactersFromARingContainingTwoCharacters() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new StringReader("ab")) );

        assertEquals( "ab", buf.popStringLHS(2) );
        assertEquals( 0, buf.usedCapacity() );
    }

    @Test
    public void popTwoCharactersFromARingContainingThreeCharacters() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 4 );

        assertTrue( buf.append(new StringReader("abc")) );

        assertEquals( "ab", buf.popStringLHS(2) );
        assertEquals( 1, buf.usedCapacity() );
    }

    @Test
    public void fillBuffer_popTwoCharString_expectSuccess() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new StringReader("abc")) );

        assertEquals( "ab", buf.popStringLHS(2) );
        assertEquals( 1, buf.usedCapacity() );
    }

    @Test
    public void fillMiddleOfBuffer_fillWholeBufferSoFromIsGTEnd_popTwoCharString_expectSuccess() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append(new StringReader("abc"));
        buf.popFromLHS();
        buf.popFromRHS();
        buf.append(new StringReader("12"));

        assertEquals( "b12", buf.popStringLHS( 3 ) );
        assertEquals( 0, buf.usedCapacity() );
    }

    //

    // wrap
    // overflow

//    @Test
//    public void fillRing_peek_expectToSeeCharacters() throws IOException {
//        CircularBufferChar buf = new CircularBufferChar( 3 );
//
//        buf.append(new StringReader("abc"));
//
//        assertEquals( 'a', buf.peekLHS(0) );
//        assertEquals( 'b', buf.peekLHS(1) );
//        assertEquals( 'c', buf.peekLHS(2) );
//        assertEquals( -1, buf.peekLHS(3) );
//        assertEquals( -1, buf.peekLHS(4) );
//    }
//
//    @Test
//    public void peekWrapsOverRHSOfRing() throws IOException {
//        CircularBufferChar buf = new CircularBufferChar( 3 );
//
//        buf.append(new StringReader("abc"));
//        buf.popFromLHS();
//        buf.popFromRHS();
//        buf.append(new StringReader("13"));
//
//        assertEquals( 'b', buf.peekLHS(0) );
//        assertEquals( '1', buf.peekLHS(1) );
//        assertEquals( '3', buf.peekLHS(2) );
//        assertEquals( -1, buf.peekLHS(3) );
//        assertEquals( -1, buf.peekLHS(4) );
//    }

}
