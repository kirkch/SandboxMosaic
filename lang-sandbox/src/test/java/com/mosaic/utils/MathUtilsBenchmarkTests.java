package com.mosaic.utils;

import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class MathUtilsBenchmarkTests {


/*

    9.52ns per call
    9.14ns per call
    9.62ns per call
    9.23ns per call
    9.43ns per call
    9.87ns per call
*/
    @Benchmark
    public long charactersLengthOf( int numIterations ) {
        long v = 0;
        for ( int i=0; i<numIterations; i++ ) {
            v += MathUtils.charactersLengthOf(i);
        }

        return v;
    }

}
