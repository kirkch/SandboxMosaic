package com.mosaic.lang;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
@SuppressWarnings( "unchecked" )
public class EnhancedIterable_combine_Tests {

    @Test
    public void combineNull_expectException() {
        try {
            EnhancedIterable.combine( (Iterable[]) null );
            fail( "expected IAE" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'iterables' is not allowed to be null", e.getMessage() );
        }
    }

    @Test
    public void combineNullIteratableWithIterator_expectException() {
        try {
            List a = Arrays.asList( 1, 2 );

            EnhancedIterable.combine( a, null );

            fail( "expected IAE" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'iterables[1]' must not be null", e.getMessage() );
        }
    }

    @Test
    public void combineOneEmptyEnhancedIterable_expectSameIteratorBack() {
        EnhancedIterable  a = EnhancedIterable.wrap(Arrays.asList());
        EnhancedIterable it = EnhancedIterable.combine( a );

        assertTrue( it == a );
        assertIterableContents( new Object[] {}, it );
    }

    @Test
    public void combineOneEmptyIterable_expectEmptyEnhancedIterableBack() {
        List              a = Arrays.asList();
        EnhancedIterable it = EnhancedIterable.combine( a );

        assertTrue( it == EnhancedIterable.EMPTY );
        assertIterableContents( new Object[] {}, it );
    }

    @Test
    public void combineOneEnhancedIteratableWithOneElement_expectSameIteratorBack() {
        Object[] elements = {1};
        EnhancedIterable  a = EnhancedIterable.wrap( Arrays.asList(elements) );
        EnhancedIterable it = EnhancedIterable.combine( a );

        assertTrue( it == a );
        assertIterableContents( elements, it );
    }

    @Test
    public void combineOneIteratableWithOneElement_expectSingleElementBackAsEnhancedIterable() {
        Object[] elements = {1};

        List              a = Arrays.asList(elements);
        EnhancedIterable it = EnhancedIterable.combine( a );

        assertTrue( it != a );
        assertIterableContents( elements, it );
    }

    @Test
    public void combineOneEnhancedIteratorWithTwoElements_expectTwoElementEnhancedIterableBack() {
        Object[] elements = {1,2};

        List              a = Arrays.asList(elements);
        EnhancedIterable it = EnhancedIterable.combine( a );

        assertTrue( it != a );
        assertIterableContents( elements, it );
    }

    @Test
    public void combineTwoIteratorWithTwoElementsEach_expectAnEnhancedIteratorBackWithFourElementsInOrder() {
        Object[] elements1 = {1,2};
        Object[] elements2 = {3,4};
        Object[] elements  = {1,2,3,4};

        EnhancedIterable it = EnhancedIterable.combine( Arrays.asList(elements1), Arrays.asList(elements2) );

        assertIterableContents( elements, it );
    }

    private void assertIterableContents( Object[] expected, EnhancedIterable ei ) {
        Iterator it = ei.iterator();

        for ( int i=0; i<expected.length; i++ ) {
            assertTrue( String.format("index[%s]: expected a next value",i), it.hasNext() );

            Object a = expected[i];
            Object b = it.next();

            assertTrue( String.format("index[%s]: (%s) != %s",i,a,b), a == b );
        }

        assertFalse( it.hasNext() );
    }
}
