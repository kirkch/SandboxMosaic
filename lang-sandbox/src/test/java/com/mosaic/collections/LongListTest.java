package com.mosaic.collections;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class LongListTest {


// GIVEN EMPTY LIST

    @Test
    public void givenEmptyList_getSize_expectZero() {
        LongList list = new LongList();

        assertEquals( 0, list.size() );
    }

    @Test
    public void givenEmptyList_getFirstElement_expectZero() {
        LongList list = new LongList();

        assertEquals( 0, list.get(0) );
    }

    @Test
    public void givenEmptyList_getSecondElement_expectZero() {
        LongList list = new LongList();

        assertEquals( 0, list.get(1) );
    }

    @Test
    public void givenEmptyList_getTwentiethElement_expectZero() {
        LongList list = new LongList();

        assertEquals( 0, list.get(19) );
    }


// GIVEN NON EMPTY LIST

    @Test
    public void setFirstElement_expectSizeOne() {
        LongList list = new LongList();

        list.set( 0, 42 );

        assertEquals( 1, list.size() );
    }

    @Test
    public void setThirdElement_expectSizeThree() {
        LongList list = new LongList();

        list.set( 2, 42 );

        assertEquals( 3, list.size() );
    }

    @Test
    public void setThirdElement_fetchFirstElement_expectZero() {
        LongList list = new LongList();

        list.set( 2, 42 );

        assertEquals( 0, list.get(0) );
    }

    @Test
    public void setThirdElement_fetchThirdElement_expectValue() {
        LongList list = new LongList();

        list.set( 2, 42 );

        assertEquals( 42, list.get(2) );
    }


// CAUSE LIST TO RESIZE

    @Test
    public void givenListWithFirstHundredElementsSet_ensureAllAreSet() {
        LongList list = new LongList();

        for ( int i=0; i<100; i++ ) {
            list.set( i, -i );
        }

        assertEquals( 100, list.size() );

        for ( int i=0; i<100; i++ ) {
            assertEquals( -i, list.get(i) );
        }
    }

    @Test
    public void givenListWithFirstHundredElementsSet_callClear_expectAllToBeEmpty() {
        LongList list = new LongList();

        for ( int i=0; i<100; i++ ) {
            list.set( i, -i );
        }

        list.clear();

        assertEquals( 0, list.size() );

        for ( int i=0; i<100; i++ ) {
            assertEquals( 0, list.get(i) );
        }
    }

    //




}
