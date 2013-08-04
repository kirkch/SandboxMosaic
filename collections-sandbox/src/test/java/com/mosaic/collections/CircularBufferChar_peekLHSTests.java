package com.mosaic.collections;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class CircularBufferChar_peekLHSTests {

    @Test
    public void peekFromEmptyRing_expectMinusOne() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertEquals( -1, buf.peekLHS() );
    }

    @Test
    public void peekTwice_expectToSeeSameCharacter() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new StringReader("abc")) );

        assertEquals( 'a', buf.peekLHS() );
        assertEquals( 'a', buf.peekLHS() );
        assertEquals( 3, buf.usedCapacity() );
    }

    @Test
    public void fillRing_peek_expectToSeeCharacters() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append( new StringReader( "abc" ) );

        assertEquals( 'a', buf.peekLHS(0) );
        assertEquals( 'b', buf.peekLHS(1) );
        assertEquals( 'c', buf.peekLHS(2) );
        assertEquals( -1, buf.peekLHS(3) );
        assertEquals( -1, buf.peekLHS( 4 ) );
    }

    @Test
    public void peekWrapsOverRHSOfRing() throws IOException {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append(new StringReader("abc"));
        buf.popFromLHS();
        buf.popFromRHS();
        buf.append(new StringReader("13"));

        assertEquals( 'b', buf.peekLHS( 0 ) );
        assertEquals( '1', buf.peekLHS(1) );
        assertEquals( '3', buf.peekLHS(2) );
        assertEquals( -1, buf.peekLHS(3) );
        assertEquals( -1, buf.peekLHS( 4 ) );
    }

}
