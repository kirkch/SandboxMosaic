package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.DebugSystem;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 */
public class FlyWeightTest {

    private DebugSystem      system    = new DebugSystem();
    private Bytes            bytes     = Bytes.allocOnHeap( 1024 );
    private RedBullFlyWeight flyweight = new RedBullFlyWeight( system, bytes );



    @Test
    public void readAndWriteMultipleRecords() {
        allocateAndPopulateBulls( 3 );


        assertTrue( flyweight.select( 0 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(0);

        assertTrue( flyweight.select( 1 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        assertTrue( flyweight.select( 2 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(2);

        assertFalse( flyweight.select( 3 ) );
    }

    @Test
    public void iterateOverMultipleRecords() {
        allocateAndPopulateBulls( 3 );


        assertTrue( flyweight.select( 0 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(0);

        assertTrue( flyweight.next() );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        assertTrue( flyweight.next() );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(2);

        assertFalse( flyweight.next() );
    }

    @Test
    public void clearAll() {
        allocateAndPopulateBulls( 3 );

        flyweight.clearAll();


        assertFalse( flyweight.select(0) );
    }

    @Test
    public void selectedIndex() {
        flyweight.allocateNewRecords( 3 );


        assertEquals( -1, flyweight.selectedIndex() );

        flyweight.select( 0 );
        assertEquals( 0, flyweight.selectedIndex() );

        flyweight.select( 1 );
        assertEquals( 1, flyweight.selectedIndex() );
    }

    @Test
    public void copySelectedRecordToAnotherLocation() {
        allocateAndPopulateBulls( 3 );

        flyweight.select( 0 );
        flyweight.copySelectedRecordTo( 2 );


        assertTrue( flyweight.select( 0 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(0);

        assertTrue( flyweight.select( 1 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        assertTrue( flyweight.select( 2 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(0);

        assertFalse( flyweight.select( 3 ) );
    }

    @Test
    public void givenNonEmptyFlyweight_copySelectedRecordToBackwards_expectMove() {
        allocateAndPopulateBulls( 3 );

        flyweight.select( 1 );
        flyweight.copySelectedRecordTo( 0 );


        assertTrue( flyweight.select( 0 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        assertTrue( flyweight.select( 1 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        assertTrue( flyweight.select( 2 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(2);

        assertFalse( flyweight.select( 3 ) );
    }

    @Test
    public void givenNonEmptyFlyweight_moveSelectedRecordOverItself_expectNoChange() {
        allocateAndPopulateBulls( 3 );

        flyweight.select( 1 );
        flyweight.copySelectedRecordTo( 1 );


        assertTrue( flyweight.select( 0 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(0);

        assertTrue( flyweight.select( 1 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        assertTrue( flyweight.select( 2 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(2);

        assertFalse( flyweight.select( 3 ) );
    }


    @Test
    public void copySelectedRecordTo_bytes() {
        allocateAndPopulateBulls( 3 );

        Bytes buf = Bytes.allocOnHeap( 200 );

        // do move
        flyweight.select( 2 );
        flyweight.copySelectedRecordTo( buf, 3 );


        assertEquals( true, buf.readBoolean(3) );
        assertEquals( 2, buf.readInteger(3+1) );
        assertEquals( 4.13f*3, buf.readFloat(3+5), 0.0001 );
    }

    @Test
    public void copySelectedRecordTo_fromBytes() {
        allocateAndPopulateBulls( 3 );

        Bytes buf = Bytes.allocOnHeap( 200 );

        flyweight.select( 1 );
        flyweight.copySelectedRecordTo( buf, 3 );

        flyweight.select( 2 );
        flyweight.copySelectedRecordFrom( buf, 3 );


        assertTrue( flyweight.select( 0 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(0);

        assertTrue( flyweight.select( 1 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        assertTrue( flyweight.select( 2 ) );
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        assertFalse( flyweight.select( 3 ) );
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
