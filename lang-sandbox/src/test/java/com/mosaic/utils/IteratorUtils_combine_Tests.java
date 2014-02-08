package com.mosaic.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 *
 */
@SuppressWarnings( "unchecked" )
public class IteratorUtils_combine_Tests {

    @Test
    public void combineNull_expectException() {
        try {
            IteratorUtils.combine( (Iterator[]) null );
            fail( "expected IAE" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'iterators' is not allowed to be null", e.getMessage() );
        }
    }

    @Test
    public void combineNullIteratorWithIterator_expectException() {
        try {
            Iterator a = Arrays.asList( new Object[]{1, 2} ).iterator();

            IteratorUtils.combine( a, null );

            fail( "expected IAE" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'iterators[1]' must not be null", e.getMessage() );
        }
    }

    @Test
    public void combineOneEmptyIterator_expectSameIteratorBack() {
        Iterator  a = Arrays.asList().iterator();
        Iterator it = IteratorUtils.combine( a );

        assertTrue( it == a );
        assertIteratorContents( new Object[] {}, it );
    }

    @Test
    public void combineOneIteratorWithOneElement_expectSameIteratorBack() {
        Object[] elements = {1};
        Iterator  a = Arrays.asList(elements).iterator();
        Iterator it = IteratorUtils.combine( a );

        assertTrue( it == a );
        assertIteratorContents( elements, it );
    }

    @Test
    public void combineOneIteratorWithTwoElements_expectSameIteratorBack() {
        Object[] elements = {1,2};
        Iterator  a = Arrays.asList(elements).iterator();
        Iterator it = IteratorUtils.combine( a );

        assertTrue( it == a );
        assertIteratorContents( elements, it );
    }

    @Test
    public void combineTwoIteratorWithTwoElementsEach_expectAnIteratorBackWithFourElementsInOrder() {
        Object[] elements1 = {1,2};
        Object[] elements2 = {3,4};
        Object[] elements  = {1,2,3,4};

        Iterator  a = Arrays.asList(elements1).iterator();
        Iterator  b = Arrays.asList(elements2).iterator();

        Iterator it = IteratorUtils.combine( a, b );

        assertFalse( it == a );
        assertIteratorContents( elements, it );
    }

    private void assertIteratorContents( Object[] expected, Iterator it ) {
        for ( int i=0; i<expected.length; i++ ) {
            assertTrue( String.format("index[%s]: expected a next value",i), it.hasNext() );

            Object a = expected[i];
            Object b = it.next();

            assertTrue( String.format("index[%s]: (%s) != %s",i,a,b), a == b );
        }

        assertFalse( it.hasNext() );
    }
}
