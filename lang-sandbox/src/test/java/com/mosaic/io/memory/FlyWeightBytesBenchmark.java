package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.ComparisonResult;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.DebugSystem;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.After;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class FlyWeightBytesBenchmark {

    private DebugSystem      system    = new DebugSystem();
    private Bytes            bytes     = Bytes.allocOnHeap( 1024 * 1024 );
    private RedBullFlyWeight flyweight = new RedBullFlyWeight( system, bytes );

    @After
    public void tearDown() {
        flyweight.release();
    }

/*
1000 calls with 2000 elements... includes allocation time

    136585.00ns per call
    135236.00ns per call
    131861.00ns per call
    135902.00ns per call
    135011.00ns per call
    133684.00ns per call
     */


    @Benchmark(value = 1000)
    public void mediumQuickSort() {
        allocateAndPopulateBulls( 2000 );

        flyweight.sort( new FlyWeightComparator<RedBullFlyWeight>() {
            public ComparisonResult compare( RedBullFlyWeight f, long a, long b ) {
                int ageA = f.select( a ).getAge();
                int ageB = f.select( b ).getAge();

                return ComparisonResult.compare( ageB, ageA ); // age DESCENDING;  will reverse the order of the records
            }
        } );
    }
/*
1000 calls with 10 elements... includes allocation time

    17002.00ns per call
    17176.00ns per call
    7678.00ns per call
    3396.00ns per call
    3103.00ns per call
    3117.00ns per call
     */


    @Benchmark(value = 1000)
    public void smallQuickSort() {
        allocateAndPopulateBulls( 10 );

        flyweight.sort( new FlyWeightComparator<RedBullFlyWeight>() {
            public ComparisonResult compare( RedBullFlyWeight f, long a, long b ) {
                int ageA = f.select( a ).getAge();
                int ageB = f.select( b ).getAge();

                return ComparisonResult.compare( ageB, ageA ); // age DESCENDING;  will reverse the order of the records
            }
        } );
    }

/*
1000 calls with 20000 elements... includes allocation time

    1.32ms per call
    1.33ms per call
    1.31ms per call
    1.34ms per call
    1.33ms per call
    1.33ms per call
     */


    @Benchmark(value = 1000)
    public void largeQuickSort() {
        allocateAndPopulateBulls( 20000 );

        flyweight.sort( new FlyWeightComparator<RedBullFlyWeight>() {
            public ComparisonResult compare( RedBullFlyWeight f, long a, long b ) {
                int ageA = f.select( a ).getAge();
                int ageB = f.select( b ).getAge();

                return ComparisonResult.compare( ageB, ageA ); // age DESCENDING;  will reverse the order of the records
            }
        } );
    }


    private long allocateAndPopulateBulls( int numRecords ) {
        QA.isGTZero( numRecords, "numRecords" );

        flyweight.clearAll();

        // 0-15 header
        // 16-24 rec 0
        // 25-33 rec 1
        // 34-42 rec 2

        long from = flyweight.allocateNewRecords( numRecords );

        for ( long i=from; i<from+numRecords; i++ ) {
            flyweight.select( i );

            flyweight.setHasWings( i % 2 == 0 );
            flyweight.setAge( (int) i );
            flyweight.setWeight( (i + 1) * 4.13f );
        }

        return from;
    }
}
