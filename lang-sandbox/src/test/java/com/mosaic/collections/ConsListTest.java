package com.mosaic.collections;

import com.mosaic.lang.function.Function1;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ConsListTest {

    @Test
    public void givenNil_callHead_expectException() {
        try {
            ConsList.Nil.head();

            Assert.fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            assertEquals("Nil does not have a head value", e.getMessage());
        }
    }

    @Test
    public void givenNil_callTail_expectException() {
        try {
            ConsList.Nil.tail();

            Assert.fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            assertEquals("Nil does not have a tail value", e.getMessage());
        }
    }

    @Test
    public void givenNil_consValue_expectNewListWithValueAndNilTail() {
        ConsList<String> list = ConsList.Nil.cons("a");

        assertConsListEquals( list, "a" );
    }

    @Test
    public void givenNil_consTwoValue_expectNewListWithValueAndSecondValueAsTail() {
        ConsList<String> list = ConsList.Nil.cons("a").cons("b");

        assertConsListEquals( list, "b", "a" );
    }

    @Test
    public void givenTwoIdenticalConsLists_callEquals_expectTrue() {
        ConsList a = ConsList.Nil.cons("a").cons("b").cons("c");
        ConsList b = ConsList.Nil.cons("a").cons("b").cons("c");

        assertTrue(a.equals(b));
    }

    @Test
    public void givenTwoNoneIdenticalConsLists_callEquals_expectFalse() {
        ConsList a = ConsList.Nil.cons("a").cons("b").cons("c");
        ConsList b = ConsList.Nil.cons("a").cons("b");

        assertFalse(a.equals(b));
    }

    @Test
    public void givenAList_mapItsContents_expectConvertedList() {
        ConsList a = ConsList.Nil.cons("a").cons("b").cons("c");
        ConsList b = a.map( new Function1<String,String>() {
            public String invoke(String arg) {
                return arg+arg;
            }
        });

        assertConsListEquals( a, "c", "b", "a" );
        assertConsListEquals( b, "cc", "bb", "aa" );
    }



    private <T> void assertConsListEquals( ConsList<T> list, T...expectedValues ) {
        ConsList<T> listSoFar = list;

        for ( int i=0; i<expectedValues.length; i++ ) {
            T expectedHead = expectedValues[i];

            if ( listSoFar.isEmpty() ) {
                fail( "Encountered Nil after matching " + i + " values.  Expected " + Arrays.asList(expectedValues) + " but was " + list );
            }

            assertEquals("Encountered mismatch at index " + i + " values.  Expected " + Arrays.asList(expectedValues) + " but was " + list, expectedHead, listSoFar.head());

            listSoFar = listSoFar.tail();
        }

        assertTrue( listSoFar.isEmpty() );
    }

}
