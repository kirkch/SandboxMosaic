package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.ComparisonResult;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
@SuppressWarnings("unchecked")
public class FlyWeightQuickSortOpTest {

    private static final FlyWeightComparator<RedBullFlyWeight> comparator = new FlyWeightComparator<RedBullFlyWeight>() {
        public ComparisonResult compare( RedBullFlyWeight f, long a, long b ) {
            int ageA = f.select( a ).getAge();
            int ageB = f.select( b ).getAge();

            return ComparisonResult.compare( ageB, ageA ); // age DESCENDING;  will reverse the order of the records
        }
    };


    private DebugSystem      system    = new DebugSystem();
    private Bytes            bytes     = Bytes.allocOnHeap( 1024 );
    private RedBullFlyWeight flyweight = new RedBullFlyWeight( system, bytes );


    @Test
    public void inplaceQuickSort() {
        for ( int i=1; i<200; i++ ) {
            inplaceQuickSortTest(i);
        }
    }


    private void inplaceQuickSortTest( int length ) {
        flyweight.clearAll();
        allocateAndPopulateBulls( length );

        FlyWeightQuickSortOp op = new FlyWeightQuickSortOp( comparator );
        op.execute( flyweight );


        for ( int i=0; i<length; i++ ) {
            flyweight.select( i );
            assertSelectedRecordEqualsAlgorithmicExpectationOf( length-i-1 );
        }
    }


    private long allocateAndPopulateBulls( int numRecords ) {
        QA.isGTZero( numRecords, "numRecords" );

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

    private void assertSelectedRecordEqualsAlgorithmicExpectationOf( long expectation ) {
        assertEquals( expectation%2 == 0, flyweight.getHasWings() );
        assertEquals( expectation, flyweight.getAge() );
        assertEquals( 4.13f*(expectation+1), flyweight.getWeight(), 0.001 );
    }
}
