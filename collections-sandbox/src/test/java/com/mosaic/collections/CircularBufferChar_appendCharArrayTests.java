package com.mosaic.collections;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class CircularBufferChar_appendCharArrayTests {
    @Test
    public void appendBigArrayOfCharacters_tooBigForRing_expectFailure() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertFalse( buf.append(new char[] {'c','d','e','f','g'}) );
    }

    @Test
    public void appendArrayOfCharacters_readbackFromLHS_noOverflow() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new char[] {'c','d'}) );

        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'd', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
    }

    @Test
    public void appendTwoArraysOfCharacters_firstFitsSecondExceedsRingCapacity_expectSecondAppendToFail() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new char[] {'c','d','e','f'}) );
        assertFalse( buf.append(new char[] {'g','h'}) );

        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'd', buf.popFromLHS() );
        assertEquals( 'e', buf.popFromLHS() );
        assertEquals( 'f', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
    }

    @Test
    public void appendTwoArraysOfCharacters_firstFitsRemoveFirstCharsThenAddSecondWhichWillNowFitInTheRingWhileWrappingAround() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new char[] {'c','d'}) );
        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'd', buf.popFromLHS() );

        assertTrue( buf.append(new char[] {'e','f'}) );
        assertEquals( 'e', buf.popFromLHS() );
        assertEquals( 'f', buf.popFromLHS() );

        assertEquals( -1, buf.popFromLHS() );
    }



    @Test
    public void appendSubsetOfArray_doesNotFitIntoArray_rejected() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertFalse( buf.append(new char[] {'a','b','c','d','e'}, 1, 5) );
        assertEquals( -1, buf.popFromLHS() );
    }

    @Test
    public void appendSubsetOfArray_fitsIntoArray_readLHSFromRHS() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new char[] {'a','b','c','d','e'}, 1, 2) );

        assertEquals( 'b', buf.popFromLHS() );
        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
    }

    @Test
    public void appendTwoSubsetOfArray_secondCallDoesNotfitsIntoArray_expectRejection() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append(new char[] {'a','b','c','d','e'}, 1, 3);
        assertFalse( buf.append(new char[] {'a','b','c','d','e'}, 2, 2) );

        assertEquals( 'b', buf.popFromLHS() );
        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'd', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
    }

    @Test
    public void appendSubsetOfArrayWithWrapAround_fitsIntoArray_readLHSFromRHS() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append(new char[] {'a','b','c','d','e'}, 1, 2) );
        assertEquals( 'b', buf.popFromLHS() );
        assertTrue( buf.append(new char[] {'a','b','c','d','e'}, 2, 2) );

        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'd', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
    }
}
