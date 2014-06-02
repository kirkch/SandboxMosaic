package com.mosaic.collections;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;


/**
 *
 */
public class LongSetTest {

    @Test
    public void givenEmptySet_containsValuesMinus100To100_expectFalse() {
        LongSet set = LongSet.createLongSet(10);

        for ( int i=-100; i<=100; i++ ) {
            assertFalse( set.contains(i) );
        }
    }

    @Test
    public void givenSetContainingMinus10And42_containsValuesMinus100To100_expectTrueForMinus10And42() {
        LongSet set = LongSet.createLongSet(10);

        set.add( -10 );
        set.add( 42 );

        for ( int i=-100; i<=100; i++ ) {
            if ( i == -10 || i == 42 ) {
                assertTrue( i+"", set.contains(i) );
            } else {
                assertFalse( i+"", set.contains(i) );
            }
        }
    }

    @Test
    public void givenSetContainingMinus10And42_appendToBuffer() {
        LongSet set = LongSet.createLongSet(10);

        set.add( -10 );
        set.add( 42 );

        StringBuilder buf = new StringBuilder();
        set.appendTo( buf, ", " );

        assertEquals( "-10, 42", buf.toString() );
    }

    @Test
    public void givenSetContaining10to40IncWhichWillCauseTheSetToGrow_containsValuesMinus100To100_expectTrueFor10To40Inc() {
        LongSet set = LongSet.createLongSet(10);

        for ( int i=10; i<=40; i++ ) {
            set.add(i);
        }

        for ( int i=-100; i<=100; i++ ) {
            if ( i >= 10 && i <= 40 ) {
                assertTrue( i+"", set.contains(i) );
            } else {
                assertFalse( i+"", set.contains(i) );
            }
        }
    }

    @Test
    public void givenEmptySet_expectEmptyIterator() {
        LongSet set = LongSet.createLongSet(10);


        LongIterator it = set.iterator();

        assertFalse( it.hasNext() );
        assertEquals( -1, it.index() );

        try {
            it.next();
        } catch ( IndexOutOfBoundsException e ) {
            assertEquals( "hasNext() returned false", e.getMessage() );
        }
    }

    @Test
    public void givenSetContaining10to40IncWhichWillCauseTheSetToGrow_retrieveValuesUsingIterator() {
        LongSet set = LongSet.createLongSet(10);
        Set<Long> expectedValues = new HashSet <>();

        for ( int i=10; i<=40; i++ ) {
            set.add(i);
            expectedValues.add((long) i);
        }

        LongIterator it = set.iterator();
        int expectedIndex = 0;
        while ( it.hasNext() ) {
            long v = it.next();

            assertTrue( expectedValues.remove(v) );
            assertEquals( expectedIndex, it.index() );

            expectedIndex++;
        }
    }



}
