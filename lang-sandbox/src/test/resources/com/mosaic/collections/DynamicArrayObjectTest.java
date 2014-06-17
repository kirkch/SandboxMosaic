package com.mosaic.collections;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 */
@SuppressWarnings("unchecked")
public class DynamicArrayObjectTest {

    // GIVEN EMPTY LIST

    @Test
    public void givenEmptyList_getSize_expectZero() {
        DynamicArrayObject list = new DynamicArrayObject();

        assertEquals( 0, list.size() );
    }

    @Test
    public void givenEmptyList_getFirstElement_expectNull() {
        DynamicArrayObject list = new DynamicArrayObject();

        assertNull( list.get( 0 ) );
    }

    @Test
    public void givenEmptyList_getSecondElement_expectNull() {
        DynamicArrayObject list = new DynamicArrayObject();

        assertNull( list.get( 1 ) );
    }

    @Test
    public void givenEmptyList_getTwentiethElement_expectNull() {
        DynamicArrayObject list = new DynamicArrayObject();

        assertNull( list.get( 19 ) );
    }

    @Test
    public void givenEmptyList_toArray_expectEmptyArray() {
        DynamicArrayObject list = new DynamicArrayObject();

        assertEquals( 0, list.toArray(String.class).length );
    }


// GIVEN NON EMPTY LIST

    @Test
    public void setFirstElement_expectSizeOne() {
        DynamicArrayObject list = new DynamicArrayObject();

        list.set( 0, "42" );

        assertEquals( 1, list.size() );
    }

    @Test
    public void setFirstElement_toArray_expectSingleElementArray() {
        DynamicArrayObject<String> list = new DynamicArrayObject<>();

        list.set( 0, "42" );

        String[] array = list.toArray( String.class );
        assertEquals( 1, array.length );
        assertEquals( "42", array[0] );
    }

    @Test
    public void setThirdElement_expectSizeThree() {
        DynamicArrayObject list = new DynamicArrayObject();

        list.set( 2, "42" );

        assertEquals( 3, list.size() );
    }

    @Test
    public void setThirdElement_toArray_expectSizeThree() {
        DynamicArrayObject<String> list = new DynamicArrayObject<>();

        list.set( 2, "42" );

        String[] array = list.toArray( String.class );
        assertEquals( 3, array.length );
        assertNull( array[0] );
        assertNull( array[1] );
        assertEquals( "42", array[2] );
    }

    @Test
    public void setThirdElement_fetchFirstElement_expectNull() {
        DynamicArrayObject list = new DynamicArrayObject();

        list.set( 2, "42" );

        assertNull( list.get( 0 ) );
    }

    @Test
    public void setThirdElement_fetchThirdElement_expectValue() {
        DynamicArrayObject list = new DynamicArrayObject();

        list.set( 2, "42" );

        assertEquals( "42", list.get(2) );
    }


// CAUSE LIST TO RESIZE

    @Test
    public void givenListWithFirstHundredElementsSet_ensureAllAreSet() {
        DynamicArrayObject list = new DynamicArrayObject();

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
        DynamicArrayObject list = new DynamicArrayObject();

        for ( int i=0; i<100; i++ ) {
            list.set( i, -i );
        }

        list.clear();

        assertEquals( 0, list.size() );

        for ( int i=0; i<100; i++ ) {
            assertNull( list.get(i) );
        }
    }

}
