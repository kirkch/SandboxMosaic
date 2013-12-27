package com.mosaic.utils;

import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Tuple2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

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

}
