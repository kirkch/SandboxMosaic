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
with no ?: to skip over the negative numbers
    3.61ns per call
    2.83ns per call
    2.82ns per call
    4.62ns per call
    2.78ns per call
    2.86ns per call

with ?: to skip over negative numbers when given a positive number
(most values will be positive, and in the lower end)
    3.72ns per call
    2.99ns per call
    3.53ns per call
    3.01ns per call
    2.84ns per call
    2.83ns per call

using if block to select between going forwards or backwards from zero;
most numbers will cluster around zero thus this speeds up the common cases
however when only using positive numbers then there is apx a cost of 0.3ns
    3.15ns per call
    3.28ns per call
    3.31ns per call
    3.12ns per call
    3.28ns per call
    3.16ns per call
*/
    @Benchmark
    public long charactersLengthOf_positiveNumbers( int numIterations ) {
        long v = 0;
        for ( int i=0; i<numIterations; i++ ) {
            v += MathUtils.charactersLengthOf(i);
        }

        return v;
    }

/*

scanning all numbers
    18.40ns per call
    15.19ns per call
    14.87ns per call
    16.03ns per call
    15.50ns per call
    15.37ns per call

using ?: to skip negative numbers
    9.59ns per call
    8.69ns per call
    9.84ns per call
    9.83ns per call
    8.13ns per call
    9.21ns per call

if statement to select direction of scan for neg/pos numbers

    8.07ns per call
    7.61ns per call
    7.70ns per call
    7.22ns per call
    7.41ns per call
    7.29ns per call
*/
    @Benchmark
    public long charactersLengthOf_postiveAndNegativeNumbers( int numIterations ) {
        long v = 0;
        for ( int i=0; i<numIterations; i++ ) {
            v += MathUtils.charactersLengthOf(i);
            v += MathUtils.charactersLengthOf(-i);
        }

        return v;
    }

/*
scan byte boundaries neg to positive
    3.11ns per call
    2.52ns per call
    2.90ns per call
    2.79ns per call
    2.85ns per call
    2.49ns per call

scan byte boundaries positive to negative
    3.19ns per call
    3.08ns per call
    3.09ns per call
    3.14ns per call
    3.12ns per call
    3.29ns per call
*/
    @Benchmark
    public long charactersLengthOf_postiveAndNegativeNumbersBytes( int numIterations ) {
        long v = 0;
        for ( int i=0; i<numIterations; i++ ) {
            v += MathUtils.charactersLengthOf((byte) i);
            v += MathUtils.charactersLengthOf((byte) -i);
        }

        return v;
    }


/*
scan byte boundaries neg to positive
    1.45ns per call
    1.68ns per call
    1.75ns per call
    1.88ns per call
    1.74ns per call
    1.68ns per call

scan byte boundaries positive to negative
    1.79ns per call
    1.75ns per call
    1.71ns per call
    1.70ns per call
    1.66ns per call
    1.68ns per call

NB above shows now real change for scan order; however using them both
from BytesWriter.writeByteAsNumber and scanning positive to negative is faster
in aggregate.
*/
    @Benchmark
    public long charactersLengthOf_postive( int numIterations ) {
        long v = 0;
        for ( int i=0; i<numIterations; i++ ) {
            v += MathUtils.charactersLengthOf((byte) i);
        }

        return v;
    }
}
