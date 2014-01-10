package com.mosaic.collections;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class CircularBufferChar_appendCharTests {

    @Test
    public void appendCharacter_expectSuccess() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertTrue( buf.append('c') );
    }

    @Test
    public void appendCharacterMultipleCharactersWithoutOverflowing_expectSuccess() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append('c');
        assertTrue( buf.append('c') );
        assertTrue( buf.append('d') );
    }

    @Test
    public void appendCharacterMultipleCharactersAndOverFloew_expectTheAppendThatOverflowsToFall() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append('c');
        assertTrue( buf.append('c') );
        assertTrue( buf.append('c') );
        assertTrue( buf.append('c') );
        assertFalse( buf.append('d') );
    }

    @Test
    public void givenEmptyBuffer_removeFromLHS_expectMinusOne() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertEquals( -1, buf.popFromLHS() );
    }

    @Test
    public void appendCharacter_thenRemoveFromLHS() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append('c');
        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
    }

    @Test
    public void appendThenRemoveCharacterMultipleCharactersWithoutOverflowing_expectSuccess() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append('c');
        buf.append('c');
        buf.append('d');

        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'd', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
    }

    @Test
    public void appendCharacterThenRemoveMultipleCharactersAndOverFlow_expectTheAppendThatOverflowsToFall() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append('c');
        buf.append('c');
        buf.append('d');
        buf.append('d');
        buf.append('d');

        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'd', buf.popFromLHS() );
        assertEquals( 'd', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
    }

    @Test
    public void appendCharacterThatWrapsAroundRHSOfBuffer_thenRetrieveSPoppingFromLHS() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append('c');
        buf.popFromLHS();
        buf.append('c');
        buf.append('d');
        assertTrue( buf.append('e') );

        assertEquals( 'c', buf.popFromLHS() );
        assertEquals( 'd', buf.popFromLHS() );
        assertEquals( 'e', buf.popFromLHS() );
        assertEquals( -1, buf.popFromLHS() );
    }

    @Test
    public void givenEmptyBuffer_removeFromRHS_expectMinusOne() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        assertEquals( -1, buf.popFromRHS() );
    }

    @Test
    public void appendCharacter_thenRemoveFromRHS() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append('c');
        assertEquals( 'c', buf.popFromRHS() );
        assertEquals( -1, buf.popFromRHS() );
    }

    @Test
    public void appendThenRemoveCharactersFromRHSMultipleCharactersWithoutOverflowing_expectSuccess() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append('c');
        buf.append('c');
        buf.append('d');

        assertEquals( 'd', buf.popFromRHS() );
        assertEquals( 'c', buf.popFromRHS() );
        assertEquals( 'c', buf.popFromRHS() );
        assertEquals( -1, buf.popFromRHS() );
    }

    @Test
    public void appendCharacterThenRemoveMultipleCharactersFromtRHSAndOverFlow_expectTheAppendThatOverflowsToFall() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append('c');
        buf.append('c');
        buf.append('d');
        buf.append('d');
        buf.append('d');

        assertEquals( 'd', buf.popFromRHS() );
        assertEquals( 'd', buf.popFromRHS() );
        assertEquals( 'c', buf.popFromRHS() );
        assertEquals( 'c', buf.popFromRHS() );
        assertEquals( -1, buf.popFromRHS() );
    }

    @Test
    public void appendCharacterThatWrapsAroundRHSOfBuffer_thenRetrieveSPoppingFromRHS() {
        CircularBufferChar buf = new CircularBufferChar( 3 );

        buf.append('c');
        buf.popFromLHS();
        buf.append('c');
        buf.append('d');
        assertTrue( buf.append('e') );

        assertEquals( 'e', buf.popFromRHS() );
        assertEquals( 'd', buf.popFromRHS() );
        assertEquals( 'c', buf.popFromRHS() );
        assertEquals( -1, buf.popFromRHS() );
    }

}
