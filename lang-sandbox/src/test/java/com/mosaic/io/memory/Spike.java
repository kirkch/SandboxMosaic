package com.mosaic.io.memory;

import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class Spike {

    @Benchmark
    public long m( int count ) {
        long r = System.currentTimeMillis();
        for ( int i=0; i<count; i++ ) {
            r *= i;
        }

        return r;
    }

    @Benchmark
    public long p( int count ) {
        long r = System.currentTimeMillis();
        for ( int i=1; i<count; i++ ) {
            r *= i;
        }

        return r;
    }

}
