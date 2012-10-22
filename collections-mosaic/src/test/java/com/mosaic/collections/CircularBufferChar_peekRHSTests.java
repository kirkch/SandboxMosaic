package com.mosaic.collections;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 *
 */
public class CircularBufferChar_peekRHSTests {

    @Test
    public void peekFromEmptyRing_expectMinusOne() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertEquals( -1, buf.peekRHS() );
    }

    @Test
    public void peekTwice_expectToSeeSameCharacter() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new StringReader("abc")) );

        assertEquals( 'c', buf.peekRHS() );
        assertEquals( 'c', buf.peekRHS() );
        assertEquals( 3, buf.usedCapacity() );
    }

    @Test
    public void fillRing_peek_expectToSeeCharacters() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append(new StringReader("abc"));

        assertEquals( 'c', buf.peekRHS(0) );
        assertEquals( 'b', buf.peekRHS(1) );
        assertEquals( 'a', buf.peekRHS(2) );
        assertEquals( -1, buf.peekRHS(3) );
        assertEquals( -1, buf.peekRHS(4) );
    }

    @Test
    public void peekWrapsOverRHSOfRing() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append(new StringReader("abc"));
        buf.popFromLHS();
        buf.popFromRHS();
        buf.append(new StringReader("13"));

        assertEquals( '3', buf.peekRHS(0) );
        assertEquals( '1', buf.peekRHS(1) );
        assertEquals( 'b', buf.peekRHS(2) );
        assertEquals( -1, buf.peekRHS(3) );
        assertEquals( -1, buf.peekRHS(4) );
    }
    
}
