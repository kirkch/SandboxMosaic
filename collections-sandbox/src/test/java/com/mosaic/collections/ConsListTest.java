package com.mosaic.collections;

import com.mosaic.lang.functional.Nullable;
import com.mosaic.lang.functional.Function1;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

            fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            assertEquals("Nil does not have a head value", e.getMessage());
        }
    }

    @Test
    public void givenNil_callTail_expectException() {
        try {
            ConsList.Nil.tail();

            fail("expected UnsupportedOperationException");
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
        assertConsListEquals(b, "cc", "bb", "aa");
    }


// FETCH FIRST MATCH

    @Test
    public void givenNil_callFetchFirstMatch_expectMappingFunctionToNotBeCalledAndNullToBeReturned() {
        final AtomicBoolean wasMappingFunctionInvoked = new AtomicBoolean(false);

        Nullable<Integer> result = ConsList.Nil.fetchFirstMatch( new Function1<String,Boolean>() {
            public Boolean invoke(String arg) {
                wasMappingFunctionInvoked.set(true);

                return false;
            }
        });

        assertTrue(result.isNull());
        assertFalse(wasMappingFunctionInvoked.get());
    }

    @Test
    public void givenNonEmptyList_callFetchFirstMatchAndMappingFunctionToNotMatch_expectMappingFunctionToBeCalledAndNullToBeReturned() {
        final AtomicInteger mappingFunctionCallCount = new AtomicInteger(0);

        ConsList list = ConsList.Nil.cons("123").cons("12");

        Nullable<Integer> result = list.fetchFirstMatch(new Function1<String,Boolean>() {
            public Boolean invoke(String arg) {
                mappingFunctionCallCount.incrementAndGet();

                return false;
            }
        });

        assertTrue(result.isNull());
        assertEquals(2, mappingFunctionCallCount.get());
    }

    @Test
    public void givenNonEmptyList_callfetchFirstMatchAndMappingFunctionToMatchSecond_expectMappingFunctionToBeCalledAndMappedValueReturned() {
        final AtomicInteger mappingFunctionCallCount = new AtomicInteger(0);

        ConsList list = ConsList.Nil.cons("123").cons("12");

        Nullable<String> result = list.fetchFirstMatch(new Function1<String,Boolean>() {
            public Boolean invoke(String arg) {
                mappingFunctionCallCount.incrementAndGet();

                return arg.length() == 3;
            }
        });

        assertEquals("123", result.getValue());
        assertEquals(2, mappingFunctionCallCount.get());
    }

    @Test
    public void givenNonEmptyList_callfetchFirstMatchAndMappingFunctionToMatchFIRST_expectMappingFunctionToBeCalledAndMappedValueReturned() {
        final AtomicInteger mappingFunctionCallCount = new AtomicInteger(0);

        ConsList list = ConsList.Nil.cons("123").cons("12");

        Nullable<String> result = list.fetchFirstMatch(new Function1<String,Boolean>() {
            public Boolean invoke(String arg) {
                mappingFunctionCallCount.incrementAndGet();

                return true;
            }
        });

        assertEquals("12", result.getValue());
        assertEquals(1, mappingFunctionCallCount.get());
    }


// MAP SINGLE VALUE

    @Test
    public void givenNil_callMapSingleValue_expectMappingFunctionToNotBeCalledAndNullToBeReturned() {
        final AtomicBoolean wasMappingFunctionInvoked = new AtomicBoolean(false);

        Nullable<Integer> result = ConsList.Nil.mapSingleValue( new Function1<String,Nullable<Integer>>() {
            public Nullable<Integer> invoke(String arg) {
                wasMappingFunctionInvoked.set(true);

                return Nullable.createNullable(arg.length());
            }
        });

        assertTrue(result.isNull());
        assertFalse(wasMappingFunctionInvoked.get());
    }

    @Test
    public void givenNonEmptyList_callMapSingleValueAndMappingFunctionToNotMatch_expectMappingFunctionToBeCalledAndNullToBeReturned() {
        final AtomicInteger mappingFunctionCallCount = new AtomicInteger(0);

        ConsList list = ConsList.Nil.cons("123").cons("12");

        Nullable<Integer> result = list.mapSingleValue(new Function1<String,Nullable<Integer>>() {
            public Nullable<Integer> invoke(String arg) {
                mappingFunctionCallCount.incrementAndGet();

                return Nullable.NULL;
            }
        });

        assertTrue(result.isNull());
        assertEquals(2, mappingFunctionCallCount.get());
    }

    @Test
    public void givenNonEmptyList_callmapSingleValueAndMappingFunctionToMatchSecond_expectMappingFunctionToBeCalledAndMappedValueReturned() {
        final AtomicInteger mappingFunctionCallCount = new AtomicInteger(0);

        ConsList list = ConsList.Nil.cons("123").cons("12");

        Nullable<Integer> result = list.mapSingleValue(new Function1<String,Nullable<Integer>>() {
            public Nullable<Integer> invoke(String arg) {
                mappingFunctionCallCount.incrementAndGet();

                if ( arg.length() == 3 ) {
                    return Nullable.createNullable(arg.length());
                } else {
                    return Nullable.NULL;
                }
            }
        });

        assertEquals(3, result.getValue().intValue());
        assertEquals(2, mappingFunctionCallCount.get());
    }

    @Test
    public void givenNonEmptyList_callmapSingleValueAndMappingFunctionToMatchFIRST_expectMappingFunctionToBeCalledAndMappedValueReturned() {
        final AtomicInteger mappingFunctionCallCount = new AtomicInteger(0);

        ConsList list = ConsList.Nil.cons("123").cons("12");

        Nullable<Integer> result = list.mapSingleValue(new Function1<String,Nullable<Integer>>() {
            public Nullable<Integer> invoke(String arg) {
                mappingFunctionCallCount.incrementAndGet();

                if ( arg.length() == 2 ) {
                    return Nullable.createNullable(arg.length());
                } else {
                    return Nullable.NULL;
                }
            }
        });

        assertEquals(2, result.getValue().intValue());
        assertEquals(1, mappingFunctionCallCount.get());
    }

    @Test
    public void reverseNil() {
        assertEquals( ConsList.Nil, ConsList.Nil );
    }

    @Test
    public void reverse3Elements() {
        ConsList<Integer> list         = ConsList.Nil.cons(1).cons(2).cons(3);
        ConsList<Integer> reversedList = list.reverse();

        assertEquals( ConsList.Nil.cons(3).cons(2).cons(1), reversedList );
    }


    private <T> void assertConsListEquals( ConsList<T> list, T...expectedValues ) {
        ConsList<T> listSoFar = list;

        for ( int i=0; i<expectedValues.length; i++ ) {
            T expectedHead = expectedValues[i];

            if ( listSoFar.isEmpty() ) {
                fail("Encountered Nil after matching " + i + " values.  Expected " + Arrays.asList(expectedValues) + " but was " + list);
            }

            assertEquals("Encountered mismatch at index " + i + " values.  Expected " + Arrays.asList(expectedValues) + " but was " + list, expectedHead, listSoFar.head());

            listSoFar = listSoFar.tail();
        }

        assertTrue(listSoFar.isEmpty());
    }

}
