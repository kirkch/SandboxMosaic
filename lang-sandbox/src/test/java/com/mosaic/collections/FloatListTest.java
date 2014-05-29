package com.mosaic.collections;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class FloatListTest {


// GIVEN EMPTY LIST

    @Test
    public void givenEmptyList_getSize_expectZero() {
        FloatList list = new FloatList();

        assertEquals( 0, list.size() );
    }

    @Test
    public void givenEmptyList_getFirstElement_expectZero() {
        FloatList list = new FloatList();

        assertEquals( 0, list.get(0), 1e-5 );
    }

    @Test
    public void givenEmptyList_getSecondElement_expectZero() {
        FloatList list = new FloatList();

        assertEquals( 0, list.get(1), 1e-5 );
    }

    @Test
    public void givenEmptyList_getTwentiethElement_expectZero() {
        FloatList list = new FloatList();

        assertEquals( 0, list.get(19), 1e-5 );
    }


// GIVEN NON EMPTY LIST

    @Test
    public void setFirstElement_expectSizeOne() {
        FloatList list = new FloatList();

        list.set( 0, 42 );

        assertEquals( 1, list.size() );
    }

    @Test
    public void setThirdElement_expectSizeThree() {
        FloatList list = new FloatList();

        list.set( 2, 42 );

        assertEquals( 3, list.size() );
    }

    @Test
    public void setThirdElement_fetchFirstElement_expectZero() {
        FloatList list = new FloatList();

        list.set( 2, 42.1f );

        assertEquals( 0, list.get(0), 1e-5 );
    }

    @Test
    public void setThirdElement_fetchThirdElement_expectValue() {
        FloatList list = new FloatList();

        list.set( 2, 42.1f );

        assertEquals( 42.1f, list.get(2), 1e-5 );
    }


// CAUSE LIST TO RESIZE

    @Test
    public void givenListWithFirstHundredElementsSet_ensureAllAreSet() {
        FloatList list = new FloatList();

        for ( int i=0; i<100; i++ ) {
            list.set( i, -i );
        }

        assertEquals( 100, list.size() );

        for ( int i=0; i<100; i++ ) {
            assertEquals( -i, list.get(i), 1e-5 );
        }
    }

    @Test
    public void givenListWithFirstHundredElementsSet_callClear_expectAllToBeEmpty() {
        FloatList list = new FloatList();

        for ( int i=0; i<100; i++ ) {
            list.set( i, -i );
        }

        list.clear();

        assertEquals( 0, list.size() );

        for ( int i=0; i<100; i++ ) {
            assertEquals( 0, list.get(i), 1e-5 );
        }
    }

}
