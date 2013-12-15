package com.mosaic.collections;

import com.mosaic.lang.functional.Function0;
import com.mosaic.utils.ArrayUtils;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import net.java.quickcheck.generator.Generators;
import net.java.quickcheck.generator.PrimitiveGenerators;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class IdentitySetBenchmark {
    private static int NUM_ELEMENTS = 20;

    private Object[] objects = ArrayUtils.newArray( NUM_ELEMENTS, new Function0() {
        public Object invoke() {
            return new Object();
        }
    });

    /*
    100 elements
     9.69ns per call
     8.20ns per call
     8.26ns per call
     8.26ns per call

     4 elements
     27.12ns per call
    19.53ns per call
    8.53ns per call
    8.78ns per call
     */
    @Benchmark( value=10000, batchCount=4, durationResultMultiplier = 1.0/(20*4))
    public void hashset() {
        doBenchmark( new HashSet() );
    }

    /*
    100 elements (vector)

    8.26ns per call
    7.67ns per call
    7.50ns per call
    7.47ns per call

    4 elements (vector)
    63.58ns per call
    66.00ns per call
    32.11ns per call
    30.32ns per call

    4 elements (array)
    3.06ns per call
    3.44ns per call
    2.99ns per call
    2.99ns per call
    2.99ns per call
    2.95ns per call


    20 elements (array)
    5.21ns per call
    5.13ns per call
    4.88ns per call
    4.94ns per call
    5.05ns per call
    6.04ns per call
     */
    @Benchmark( value=100000, batchCount=6, durationResultMultiplier = 1.0/(20*4))
    public void identityHashSet() {
        doBenchmark( new IdentitySet() );
    }

    private void doBenchmark(Set set) {
        for ( Object o : objects ) {
            set.add( o );
        }

        for ( Object o : objects ) {
            set.contains(o);
        }

        for ( Object o : objects ) {
            set.contains(o);
        }

        for ( Object o : objects ) {
            set.contains(o);
        }
    }

}
