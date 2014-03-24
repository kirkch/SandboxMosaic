package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.ComparisonResult;
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


        flyweight.select(0);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(0);

        flyweight.select(1);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        flyweight.select(2);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(2);

        assertOutOfBoundsIndexIsNotSelectable(3);
    }

    @Test
    public void iterateOverMultipleRecords() {
        allocateAndPopulateBulls( 3 );


        flyweight.select(0);
        assertSelectedRecordEqualsAlgorithmicExpectationOf( 0 );

        assertTrue( flyweight.next() );
        assertSelectedRecordEqualsAlgorithmicExpectationOf( 1 );

        assertTrue( flyweight.next() );
        assertSelectedRecordEqualsAlgorithmicExpectationOf( 2 );

        assertFalse( flyweight.next() );
    }

    @Test
    public void clearAll() {
        allocateAndPopulateBulls( 3 );

        flyweight.clearAll();


        assertOutOfBoundsIndexIsNotSelectable(0);
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


        flyweight.select(0);
        assertSelectedRecordEqualsAlgorithmicExpectationOf( 0 );

        flyweight.select(1);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        flyweight.select(2);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(0);

        assertOutOfBoundsIndexIsNotSelectable(3);
    }

    @Test
    public void givenNonEmptyFlyweight_copySelectedRecordToBackwards_expectMove() {
        allocateAndPopulateBulls( 3 );

        flyweight.select( 1 );
        flyweight.copySelectedRecordTo( 0 );


        flyweight.select(0);
        assertSelectedRecordEqualsAlgorithmicExpectationOf( 1 );

        flyweight.select(1);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        flyweight.select(2);
        assertSelectedRecordEqualsAlgorithmicExpectationOf( 2 );

        assertOutOfBoundsIndexIsNotSelectable(3);
    }

    @Test
    public void givenNonEmptyFlyweight_moveSelectedRecordOverItself_expectNoChange() {
        allocateAndPopulateBulls( 3 );

        flyweight.select( 1 );
        flyweight.copySelectedRecordTo( 1 );


        flyweight.select(0);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(0);

        flyweight.select(1);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        flyweight.select(2);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(2);

        assertOutOfBoundsIndexIsNotSelectable(3);
    }

    @Test
    public void copySelectedRecordTo_bytes() {
        allocateAndPopulateBulls( 3 );

        Bytes buf = Bytes.allocOnHeap( 200 );

        // do move
        flyweight.select( 2 );
        flyweight.copySelectedRecordTo( buf, 3 );


        assertEquals( true, buf.readBoolean( 3 ) );
        assertEquals( 2, buf.readInteger( 3 + 1 ) );
        assertEquals( 4.13f*3, buf.readFloat( 3 + 5 ), 0.0001 );
    }

    @Test
    public void copySelectedRecordTo_fromBytes() {
        allocateAndPopulateBulls( 3 );

        Bytes buf = Bytes.allocOnHeap( 200 );

        flyweight.select( 1 );
        flyweight.copySelectedRecordTo( buf, 3 );

        flyweight.select( 2 );
        flyweight.copySelectedRecordFrom( buf, 3 );


        flyweight.select(0);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(0);

        flyweight.select(1);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        flyweight.select(2);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        assertOutOfBoundsIndexIsNotSelectable(3);
    }

    @Test
    public void swapRecords() {
        allocateAndPopulateBulls( 3 );

        Bytes buf = Bytes.allocOnHeap( 3+9 );

        flyweight.select( 1 );
        flyweight.swapRecords( 1, 2, buf, 3 );


        flyweight.select(0);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(0);

        flyweight.select(1);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(2);

        flyweight.select(2);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        assertOutOfBoundsIndexIsNotSelectable(3);
    }


    @Test
    public void inplaceQuickSort() {
        allocateAndPopulateBulls( 3 );

        flyweight.inplaceQuickSort( new FlyWeightComparator<RedBullFlyWeight>() {
            public ComparisonResult compare( RedBullFlyWeight f, long a, long b ) {
                int ageA = f.select(a).getAge();
                int ageB = f.select(b).getAge();

                return ComparisonResult.compare(ageB, ageA); // age DESCENDING;  will reverse the order of the records
            }
        } );


        flyweight.select(0);
        assertSelectedRecordEqualsAlgorithmicExpectationOf( 2 );

        flyweight.select(1);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(1);

        flyweight.select(2);
        assertSelectedRecordEqualsAlgorithmicExpectationOf(0);

        assertOutOfBoundsIndexIsNotSelectable(3);




        for ( int i=1; i<200; i++ ) {
            inplaceQuickSortTest(i);
        }
    }


    private void dumpAges() {
        for ( long i=0;i<flyweight.getRecordCount(); i++ ) {
            System.out.print(" ");
            System.out.print(flyweight.select(i).getAge());
        }
        System.out.println(" ");
    }


    private void inplaceQuickSortTest( int length ) {
        flyweight.clearAll();
        allocateAndPopulateBulls( length );

        flyweight.inplaceQuickSort( new FlyWeightComparator<RedBullFlyWeight>() {
            public ComparisonResult compare( RedBullFlyWeight f, long a, long b ) {
                int ageA = f.select(a).getAge();
                int ageB = f.select(b).getAge();

                return ComparisonResult.compare(ageB, ageA); // age DESCENDING;  will reverse the order of the records
            }
        } );


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

    private void assertOutOfBoundsIndexIsNotSelectable( long i) {
        try {
            flyweight.select(i);

            fail( "expected out of bounds exception" );
        } catch ( IndexOutOfBoundsException e ) {
            assertEquals( i+" is >= the number of records available ("+flyweight.getRecordCount()+")", e.getMessage() );
        }
    }
}
