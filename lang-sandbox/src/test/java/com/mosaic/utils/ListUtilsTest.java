package com.mosaic.utils;

import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Tuple2;
import com.mosaic.lang.functional.VoidFunction1;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ListUtilsTest {


// partition

    @Test
    public void givenEmptyList_partition_expectEmptyListsBack() {
        List l = new ArrayList();

        Tuple2<List,List> r = ListUtils.partition( l, new Function1() {
            public Boolean invoke( Object arg ) {
                return true;
            }
        });

        assertEquals( 0, r.getFirst().size()  );
        assertEquals( 0, r.getSecond().size() );
    }

    @Test
    public void givenABC_partitionByLowerCase_expectEmptyLHS() {
        List<Character> l = Arrays.asList('A','B','C');

        Tuple2<List<Character>,List<Character>> r = ListUtils.partition( l, new Function1<Character,Boolean>() {
            public Boolean invoke( Character c ) {
                return Character.isLowerCase(c);
            }
        });

        assertEquals( Arrays.<Character>asList(), r.getFirst()  );
        assertEquals( Arrays.asList('A','B','C'), r.getSecond() );
    }

    @Test
    public void givenAbC_partitionByLowerCase_expectEmptyLHS() {
        List<Character> l = Arrays.asList('A','b','C');

        Tuple2<List<Character>,List<Character>> r = ListUtils.partition( l, new Function1<Character,Boolean>() {
            public Boolean invoke( Character c ) {
                return Character.isLowerCase(c);
            }
        });

        assertEquals( Arrays.asList('b'), r.getFirst()  );
        assertEquals( Arrays.asList('A','C'), r.getSecond() );
    }


// permutations

    @Test
    public void givenEmptyList_permute2_expectNoCallbacks() {
        final List<List<Integer>> results = new ArrayList<>();

        ListUtils.permutations( Arrays.<Integer>asList(), 2, new VoidFunction1<List<Integer>>() {
            public void invoke( List<Integer> arg ) {
                results.add(arg);
            }
        } );


        assertTrue( results.isEmpty() );
    }

    @Test
    public void givenOneElement_permute2_expectCallbackWithSingleValue() {
        final List<List<Integer>> results = new ArrayList<>();

        ListUtils.permutations( Arrays.<Integer>asList(1), 2, new VoidFunction1<List<Integer>>() {
            public void invoke( List<Integer> arg ) {
                results.add(arg);
            }
        } );

        List<List<Integer>> expected = Arrays.asList(
            Arrays.asList(1)
        );

        assertEquals( expected, results );
    }

    @Test
    public void givenTwoElements_permute1_expectCallbacks() {
        final List<List<Integer>> results = new ArrayList<>();

        ListUtils.permutations( Arrays.<Integer>asList(1,2), 1, new VoidFunction1<List<Integer>>() {
            public void invoke( List<Integer> arg ) {
                results.add(arg);
            }
        } );

        List<List<Integer>> expected = Arrays.asList(
            Arrays.asList(1),
            Arrays.asList(2)
        );

        assertEquals( expected, results );
    }

    @Test
    public void givenTwoElements_permute2_expectCallbacks() {
        final List<List<Integer>> results = new ArrayList<>();

        ListUtils.permutations( Arrays.<Integer>asList(1,2), 2, new VoidFunction1<List<Integer>>() {
            public void invoke( List<Integer> arg ) {
                results.add(arg);
            }
        } );

        List<List<Integer>> expected = Arrays.asList(
            Arrays.asList(1,2),
            Arrays.asList(2,1)
        );

        assertEquals( expected, results );
    }

    @Test
    public void givenThreeElements_permute2_expectCallbacks() {
        final List<List<Integer>> results = new ArrayList<>();

        ListUtils.permutations( Arrays.<Integer>asList(1,2,3), 2, new VoidFunction1<List<Integer>>() {
            public void invoke( List<Integer> arg ) {
                results.add(arg);
            }
        } );

        List<List<Integer>> expected = Arrays.asList(
            Arrays.asList(1,2),
            Arrays.asList(1,3),
            Arrays.asList(2,1),
            Arrays.asList(2,3),
            Arrays.asList(3,1),
            Arrays.asList(3,2)
        );

        assertEquals( expected, results );
    }

    @Test
    public void givenThreeElements_permute3_expectCallbacks() {
        final List<List<Integer>> results = new ArrayList<>();

        ListUtils.permutations( Arrays.<Integer>asList(1,2,3), 3, new VoidFunction1<List<Integer>>() {
            public void invoke( List<Integer> arg ) {
                results.add(arg);
            }
        } );

        List<List<Integer>> expected = Arrays.asList(
            Arrays.asList(1,2,3),
            Arrays.asList(1,3,2),
            Arrays.asList(2,1,3),
            Arrays.asList(2,3,1),
            Arrays.asList(3,1,2),
            Arrays.asList(3,2,1)
        );

        assertEquals( expected, results );
    }


// forEachReversed

    @Test
    public void givenNull_forEachReversed_expectIllegalArgumentException() {
        List<String> audit = new ArrayList<>();

        try {
            ListUtils.<String>forEachReversed( null, audit::add );
            fail( "Expected IllegalArgumentException" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "'list' must not be null", ex.getMessage() );
        }
    }

    @Test
    public void givenEmptyList_forEachReversed_expectNoCallbacks() {
        List<String> audit = new ArrayList<>();

        ListUtils.<String>forEachReversed( Arrays.asList(), audit::add );

        assertEquals( 0, audit.size() );
    }

    @Test
    public void givenSingleElementList_forEachReversed_expectOneCallback() {
        List<String> audit = new ArrayList<>();

        ListUtils.forEachReversed( Arrays.asList("a"), audit::add );

        List<String> expected = Arrays.asList( "a" );
        assertEquals( expected, audit );
    }

    @Test
    public void givenThreeElementList_forEachReversed_expectThreeCallbacksInReverse() {
        List<String> audit = new ArrayList<>();

        ListUtils.forEachReversed( Arrays.asList("a","b","c"), audit::add );

        List<String> expected = Arrays.asList( "c", "b", "a" );
        assertEquals( expected, audit );
    }

}
