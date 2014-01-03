package com.mosaic.collections;

import com.mosaic.utils.SetUtils;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Test;
import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.CombinedGenerators;
import net.java.quickcheck.generator.PrimitiveGenerators;
import org.junit.runner.RunWith;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Compares elements using ==.  This version is optimised for speed when working
 * with small sets, the smaller the better ( < 20 ).  If larger, use the JDK
 * HashSet using a custom comparator.
 */
@SuppressWarnings("unchecked")
@RunWith(JUnitMosaicRunner.class)
public class IdentitySetTest {

    @SuppressWarnings("UnusedDeclaration")
    private Generator<List<String>> GEN = CombinedGenerators.lists( PrimitiveGenerators.strings(1, 100), 1, 100 );


    private Set set = new IdentitySet();


    @Test
    public void givenBlankSet_contains_expectFalse() {
        assertFalse(set.contains("foo"));
    }

    @Test
    public void givenBlankSet_addObjectContains_expectTrue() {
        set.add("foo");

        assertTrue(set.contains("foo"));
    }

    @Test
    @SuppressWarnings("RedundantStringConstructorCall")
    public void givenBlankSet_addFooContainsFooButNotTheSameInstance_expectFalse() {
        set.add( "foo" );

        assertFalse(set.contains(new String("foo")));
    }

    @Test( generators = {"GEN"} )
    public void randomInputComparisonWithHashSet( List<String> input ) {
        set.clear();
        d(input);
    }

    @Test
    public void testDelete() {
        set.add("a");
        set.add("b");

        set.remove("a");

        assertEquals(SetUtils.asSet("b"), set);
    }

    @Test
    public void testDelete2() {
        set.add("a");
        set.add("b");

        set.remove("a");
        set.remove("b");

        assertEquals(SetUtils.asSet(), set);
    }

    @Test( memCheck = true, generators = {"GEN"})
    public void FULLFAT_addTwice_removeTwice_compareWithControlJDKHashSet( List input ) {
        set.clear();


        Map controlSet = new IdentityHashMap();

        for ( Object v : input ) {
            controlSet.put( v, v );
        }

        set.addAll( input );
        set.addAll( input );

        assertEquals( controlSet.keySet(), set );


        int expectedSize = set.size();
        for ( Object o : input ) {
            boolean wasDeleted = set.remove(o);

            if ( wasDeleted ) {
                assertEquals( --expectedSize, set.size() );
            }
        }

        assertEquals(0, set.size());

        for ( Object o : input ) {
            boolean wasDeleted = set.remove(o);

            if ( wasDeleted ) {
                assertEquals( --expectedSize, set.size() );
            }
        }

        assertEquals(0, set.size());
    }


//    @Test
//    public void f() {
//        List<String> l = Arrays.asList(
//
//        );
//    }


    private void d(List input) {
        Map comparisonSet = new IdentityHashMap();

        assertEquals( comparisonSet.size(), set.size() );

        for ( Object v : input ) {
            set.add(v);
            comparisonSet.put( v, v );

            assertEquals( "Failed for input '"+input+"' " + v, comparisonSet.size(), set.size() );
        }

        assertEquals(comparisonSet.keySet(), set);
    }



}
