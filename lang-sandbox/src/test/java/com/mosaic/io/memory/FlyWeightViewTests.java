package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class FlyWeightViewTests {

    private DebugSystem      system    = new DebugSystem();
    private Bytes            bytes     = Bytes.allocOnHeap( 1024 );
    private RedBullFlyWeight flyweight = new RedBullFlyWeight( system, bytes );

    @Test
    public void f() {

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
