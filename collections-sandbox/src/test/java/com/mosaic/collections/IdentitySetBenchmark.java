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
    private Object[] objects = ArrayUtils.newArray( 100, new Function0() {
        public Object invoke() {
            return new Object();
        }
    });

    /*
     9.69ns per call
     8.20ns per call
     8.26ns per call
     8.26ns per call
     */
    @Benchmark( value=10000, batchCount=4, durationResultMultiplier = 1.0/(100*4))
    public void hashset() {
        doBenchmark( new HashSet() );
    }

    /*
    631.75ns per call
    255.00ns per call
    294.00ns per call
    221.75ns per call

     552.50ns per call
    192.00ns per call
    192.25ns per call
    106.50ns per call

     */
    @Benchmark( value=10000, batchCount=4, durationResultMultiplier = 1.0/(100*4))
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
