package com.mosaic.bytes.struct;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.struct.examples.redbull.RedBullStruct;
import com.mosaic.bytes.struct.examples.redbull.RedBullStructs;
import com.mosaic.lang.ComparisonResult;
import com.mosaic.lang.QA;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;


/**
 *
 */
public abstract class BaseStructsTestCases {

    protected RedBullStructs redBulls;
    protected RedBullStruct redBull = new RedBullStruct();

    protected abstract RedBullStructs createNewRedBull( long initialSize );

    @Before
    public void setup() {
        redBulls = createNewRedBull(1024);
    }


    @Test
    public void readAndWriteMultipleRecords() {
        allocateAndPopulateBulls( 3 );


        redBulls.getInto( redBull, 0 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 0 );

        redBulls.getInto( redBull, 1 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 1 );

        redBulls.getInto( redBull, 2 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 2 );

        assertOutOfBoundsIndexIsNotselectIntoable(3);
    }

    @Test
    public void iterateOverMultipleRecords() {
        allocateAndPopulateBulls( 3 );


        Iterator<RedBullStruct> it = redBulls.iterator();

        RedBullStruct r1 = it.next();
        assertRedBullEqualsAlgorithmicExpectationOf( r1, 0 );

        RedBullStruct r2 = it.next();
        assertRedBullEqualsAlgorithmicExpectationOf( r2, 1 );

        RedBullStruct r3 = it.next();
        assertRedBullEqualsAlgorithmicExpectationOf( r3, 2 );

        assertFalse( it.hasNext() );
    }

    @Test
    public void clearAll() {
        allocateAndPopulateBulls( 3 );

        redBulls.clearAll();


        assertOutOfBoundsIndexIsNotselectIntoable(0);
    }

    @Test
    public void copySelectedRecordToAnotherLocation() {
        allocateAndPopulateBulls( 3 );

        redBulls.getInto( redBull, 0 );
        redBulls.copy( 0, 2 );


        redBulls.getInto( redBull, 0 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 0 );

        redBulls.getInto( redBull, 1 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 1 );

        redBulls.getInto( redBull, 2 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 0 );

        assertOutOfBoundsIndexIsNotselectIntoable(3);
    }

    @Test
    public void givenNonEmptyRedbull_copyRecordBackwards_expectMove() {
        allocateAndPopulateBulls( 3 );

        redBulls.copy( 1, 0 );

        redBulls.getInto( redBull, 0 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 1 );

        redBulls.getInto( redBull, 1 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 1 );

        redBulls.getInto( redBull, 2 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 2 );

        assertOutOfBoundsIndexIsNotselectIntoable(3);
    }

    @Test
    public void givenNonEmptyRedbull_copyRecordOverItself_expectNoChange() {
        allocateAndPopulateBulls( 3 );

        redBulls.copy( 1, 1 );


        redBulls.getInto( redBull, 0 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 0 );

        redBulls.getInto( redBull, 1 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 1 );

        redBulls.getInto( redBull, 2 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 2 );

        assertOutOfBoundsIndexIsNotselectIntoable(3);
    }

    @Test
    public void copyRecordTo_bytes() {
        allocateAndPopulateBulls( 3 );

        Bytes buf = new ArrayBytes( 200 );

        assertEquals( 9, redBulls.getStructSizeBytes() );
        redBulls.copyTo( 2, buf, 3, 3+redBulls.getStructSizeBytes() );


        assertEquals( true, buf.readBoolean(3, 200) );
        assertEquals( 2, buf.readInt( 3 + 1, 200 ) );
        assertEquals( 4.13f*3, buf.readFloat( 3 + 5, 200 ), 0.0001 );
    }

    @Test
    public void copyRecordFrom_fromBytes() {
        allocateAndPopulateBulls( 3 );

        Bytes buf = new ArrayBytes( 200 );

        redBulls.copyTo( 1, buf, 3, 200 );
        redBulls.copyFrom( 2, buf, 3, 200 );


        redBulls.getInto( redBull, 0 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 0 );

        redBulls.getInto( redBull, 1 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 1 );

        redBulls.getInto( redBull, 2 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 1 );

        assertOutOfBoundsIndexIsNotselectIntoable(3);
    }

    @Test
    public void swapRecords() {
        allocateAndPopulateBulls( 3 );

        Bytes buf = new ArrayBytes( 3+9 );

        redBulls.swapRecords( 1, 2, buf, 3, buf.sizeBytes() );


        redBulls.getInto( redBull, 0 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 0);

        redBulls.getInto( redBull, 1 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 2);

        redBulls.getInto( redBull, 2 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 1);

        assertOutOfBoundsIndexIsNotselectIntoable(3);
    }


    @Test
    public void inplaceQuickSort() {
        allocateAndPopulateBulls( 3 );

        redBulls.sort( ( a, b ) -> ComparisonResult.compare( b.getAge(), a.getAge() ).toInt() ); // a and b reversed to give us descending


        redBulls.getInto( redBull, 0 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull,  2 );

        redBulls.getInto( redBull, 1 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 1);

        redBulls.getInto( redBull, 2 );
        assertRedBullEqualsAlgorithmicExpectationOf( redBull, 0);

        assertOutOfBoundsIndexIsNotselectIntoable(3);




        for ( int i=1; i<200; i++ ) {
            inplaceQuickSortTest(i);
        }
    }


    private void dumpAges() {
        for ( long i=0;i<redBulls.getRecordCount(); i++ ) {
            System.out.print(" ");
            System.out.print( redBulls.getInto( redBull, i ).getAge() );
        }

        System.out.println(" ");
    }


    private void inplaceQuickSortTest( int length ) {
        redBulls.clearAll();
        allocateAndPopulateBulls( length );

        redBulls.sort( (a,b) -> ComparisonResult.compare( b.getAge(), a.getAge() ).toInt() ); // a and b reversed to give us descending


        for ( int i=0; i<length; i++ ) {
            redBulls.getInto( redBull, i );
            assertRedBullEqualsAlgorithmicExpectationOf( redBull,  length-i-1 );
        }
    }


    protected long allocateAndPopulateBulls( int numRecords ) {
        QA.isGTZero( numRecords, "numRecords" );

        // 0-15 header
        // 16-24 rec 0
        // 25-33 rec 1
        // 34-42 rec 2

        long from = redBulls.allocateNewRecords( numRecords );

        for ( long i=from; i<from+numRecords; i++ ) {
            redBulls.getInto( redBull, i );

            redBull.setHasWings( i % 2 == 0 );
            redBull.setAge( (int) i );
            redBull.setWeight( (i + 1) * 4.13f );
        }

        return from;
    }

    private void assertRedBullEqualsAlgorithmicExpectationOf( RedBullStruct r, long expectation ) {
        assertEquals( expectation%2 == 0, r.getHasWings() );
        assertEquals( expectation, r.getAge() );
        assertEquals( 4.13f*(expectation+1), r.getWeight(), 0.001 );
    }

    private void assertOutOfBoundsIndexIsNotselectIntoable( long i) {
        try {
            redBulls.getInto( redBull, i );

            fail( "expected out of bounds exception" );
        } catch ( IndexOutOfBoundsException e ) {
            assertEquals( i+" is >= the number of records available ("+redBulls.getRecordCount()+")", e.getMessage() );
        }
    }

}
