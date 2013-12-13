package com.mosaic.collections;

import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Test;
import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.CombinedGenerators;
import net.java.quickcheck.generator.PrimitiveGenerators;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
@SuppressWarnings("unchecked")
@RunWith(JUnitMosaicRunner.class)
public class IdentitySetTest {

    @SuppressWarnings("UnusedDeclaration")
    private Generator<List<Integer>> GEN = CombinedGenerators.lists( PrimitiveGenerators.integers(-100,100), 0, 1000 );

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
    public void randomInputComparisonWithHashSet( List<Integer> input ) {
        set.clear();
        d(input);
    }

    @Test
    public void randomInputComparisonWithHashSet2( ) {

        d(
                Arrays.asList(
                        -87, -95, -2, -78, 18,
                        21, 83, -81, 8, 84, 9,
                        -56, -45, 89, -78, -73
                ));
    }


    private void d(List<Integer> input) {
        Set comparisonSet = new HashSet();

        assertEquals( comparisonSet.size(), set.size() );

        for ( Integer v : input ) {
            set.add(v);
            comparisonSet.add(v);

            assertEquals( "Failed for input '"+input+"' " + v, comparisonSet.size(), set.size() );
        }

        assertEquals( comparisonSet, set );
    }

}
