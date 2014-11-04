package com.mosaic.bytes.struct;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.struct.examples.redbull.RedBullStruct;
import com.mosaic.utils.ComparatorUtils;
import org.junit.Test;

import static org.junit.Assert.*;


public class StructsArrayTest {

    private Structs<RedBullStruct> structs = StructsArray.allocateOnHeap( 6, RedBullStruct::new );



// EMPTY STRUCTS

    @Test
    public void givenEmptyStructs_callNumRecords_expectZero() {
        assertEquals( 0, structs.numRecords() );
    }

    @Test
    public void givenEmptyStructs_selectZero_expectException() {
        try {
            structs.select( 0 );
            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "Unable to index 0, the collection is empty", ex.getMessage() );
        }
    }


    @Test
    public void givenEmptyStructs_callClearAll_expectNoErrorAndNumRecordsToRemainAtZero() {
        structs.clearAll();

        assertEquals( 0, structs.numRecords() );
    }


// SINGLE RECORD

    @Test
    public void givenStructsWithOneElement_callNumRecords_expectOne() {
        structs.allocateNewRecord();

        assertEquals( 1, structs.numRecords() );
    }

    @Test
    public void givenOneRecord_selectZero_expectRecord() {
        structs.allocateNewRecord();

        RedBullStruct bull = structs.select( 0 );

        assertEquals( 0, bull.getAge() );
        assertEquals( false, bull.getHasWings() );
        assertEquals( 0.0, bull.getWeight(), 0e-3 );
    }

    @Test
    public void givenOneRecord_selectOne_expectException() {
        structs.allocateNewRecord();

        try {
            structs.select( 1 );
            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "Unable to index 1, the max valid index is currently 0", ex.getMessage() );
        }
    }

    @Test
    public void givenOneRecord_selectMinusOne_expectException() {
        structs.allocateNewRecord();

        try {
            structs.select( -1 );
            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "Unable to index -1, the min valid index is currently 0", ex.getMessage() );
        }
    }


// MUTATE SINGLE RECORD

    @Test
    public void givenOneRecord_modifyIt_readItBack() {
        structs.allocateNewRecord();

        RedBullStruct bull = structs.select( 0 );
        bull.setWeight( 1.0f );
        bull.setHasWings( true );
        bull.setAge( 42 );


        assertEquals( 42, bull.getAge() );
        assertEquals( true, bull.getHasWings() );
        assertEquals( 1.0, bull.getWeight(), 0e-3 );
    }

    @Test
    public void givenOneRecord_modifyIt_readItViaSelectInto() {
        structs.allocateNewRecord();

        RedBullStruct bull = structs.select( 0 );
        bull.setWeight( 1.0f );
        bull.setHasWings( true );
        bull.setAge( 42 );


        RedBullStruct bull2 = new RedBullStruct();
        structs.selectInto( bull2, 0 );

        assertEquals( 42, bull2.getAge() );
        assertEquals( true, bull2.getHasWings() );
        assertEquals( 1.0, bull2.getWeight(), 0e-3 );
    }

    @Test
    public void givenOneModifiedRecord_clearAll_expectRecordToHaveBeenBlankedOut() {
        structs.allocateNewRecord();

        RedBullStruct bull = structs.select( 0 );
        bull.setWeight( 1.0f );
        bull.setHasWings( true );
        bull.setAge( 42 );


        structs.clearAll();

        assertEquals( 0, bull.getAge() );
        assertEquals( false, bull.getHasWings() );
        assertEquals( 0, bull.getWeight(), 0e-3 );
    }


// ALLOCATE TWO RECORDS

    @Test
    public void givenStructsWithTwoElement_callNumRecords_expectTwo() {
        structs.allocateNewRecords( 2 );

        assertEquals( 2, structs.numRecords() );
    }

    @Test
    public void givenTwoRecords_selectZero_expectRecord() {
        structs.allocateNewRecord();
        structs.allocateNewRecord();

        assertBullIsClear(0);
    }

    @Test
    public void givenTwoRecords_selectOne_expectRecord() {
        structs.allocateNewRecord();
        structs.allocateNewRecord();

        assertBullIsClear(1);
    }

    @Test
    public void givenTwoRecords_selectTwo_expectException() {
        structs.allocateNewRecord();
        structs.allocateNewRecord();

        try {
            structs.select( 2 );
            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "Unable to index 2, the max valid index is currently 1", ex.getMessage() );
        }
    }

    @Test
    public void givenTwoRecords_selectMinusOne_expectException() {
        structs.allocateNewRecord();
        structs.allocateNewRecord();

        try {
            structs.select( -1 );
            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "Unable to index -1, the min valid index is currently 0", ex.getMessage() );
        }
    }


// ALLOCATE TWO RECORDS AND MODIFY THEM

    @Test
    public void givenTwoRecords_modifyThem_expectToBeAbleToReadThemBack() {
        allocateBulls(2);

        assertBull( 0 );
        assertBull( 1 );
    }

    @Test
    public void givenTwoRecords_modifyThem_readOneViaSelectInto() {
        allocateBulls(2);

        assertBull( 0, new RedBullStruct() );
        assertBull( 1, new RedBullStruct() );
    }

    @Test
    public void givenTwoModifiedRecord_clearAll_expectRecordToHaveBeenBlankedOut() {
        allocateBulls( 2 );


        structs.clearAll();

        structs.allocateNewRecords( 2 );
        assertBullIsClear( 0 );
    }

    @Test
    public void givenTwoModifiedRecord_clearAll_expectExceptionWhenTryingToFetchPreviousRecord() {
        allocateBulls( 2 );


        structs.clearAll();

        try {
            structs.select( 0 );

            fail( "expected exception" );
        } catch ( IndexOutOfBoundsException ex ) {
            assertEquals( "Unable to index 0, the collection is empty", ex.getMessage() );
        }
    }


// ALLOCATE MORE RECORDS THAN CAPACITY, EXPECT IT TO GROW

    @Test
    public void allocateMoreRecordsThanCapacity() {
        allocateBulls(10);

        assertEquals( 10, structs.numRecords() );

        for ( int i=0; i<10; i++ ) {
            assertBull( i );
        }
    }

    @Test
    public void allocateMoreRecordsThanCapacity_useIteratorToConfirmResults() {
        allocateBulls(10);

        long i = 0;
        for ( RedBullStruct bull : structs ) {
            assertBull( i++, bull );
        }
    }


// CLEAR RECORDS

    @Test
    public void allocateRecords_clearAll_expectNumRecordsToReturnZero() {
        allocateBulls(10);

        structs.clearAll();

        assertEquals( 0, structs.numRecords() );
    }

    @Test
    @SuppressWarnings("UnusedDeclaration")
    public void allocateRecords_clearAll_expectIteratorToFindNoRecords() {
        allocateBulls(10);

        structs.clearAll();

        for ( RedBullStruct bull : structs ) {
            fail( "there should be no structs available" );
        }
    }


// SORT

    @Test
    public void allocateRecords_sort() {
        allocateBulls( 3 );

        structs.sort( (a,b) -> ComparatorUtils.compareDesc(a.getAge(), b.getAge()) );

        assertEquals( 2, structs.select(0).getAge() );
        assertEquals( 1, structs.select(1).getAge() );
        assertEquals( 0, structs.select( 2 ).getAge() );

        assertEquals( 3, structs.numRecords() );
    }


// PERSISTENCE

    @Test
    public void allocateRecords_reuseBytesInAnotherInstance_expectRecordsToRemain() {
        Bytes bytes = new ArrayBytes( StructsArray.requiredSize(10, RedBullStruct.SIZE_BYTES) );
        structs = new StructsArray<>( bytes, RedBullStruct::new );

        allocateBulls(2);


        structs = new StructsArray<>( bytes, RedBullStruct::new );

        assertBull( 0, new RedBullStruct() );
        assertBull( 1, new RedBullStruct() );
    }



    private void allocateBulls( int numBulls ) {
        long firstIndex = structs.allocateNewRecords( numBulls );

        for ( long i=firstIndex; i<firstIndex+numBulls; i++ ) {
            allocateBull(i);
        }
    }

    private void allocateBull( long i ) {
        RedBullStruct bull = structs.select(i);

        bull.setWeight( i+0.1f );
        bull.setHasWings( i % 2 == 0 );
        bull.setAge( (int) i );
    }



    private void assertBull( long i ) {
        assertBull( i, (int) i, i % 2 == 0, i+0.1f );
    }

    private void assertBull( long i, RedBullStruct view ) {
        assertBull( i, (int) i, i % 2 == 0, i+0.1f, view );
    }

    private void assertBullIsClear( long i ) {
        assertBull( i, 0, false, 0f );
    }

    private void assertBull( long i, int expectedAge, boolean expectedHasWings, float expectedWeight ) {
        RedBullStruct bull = structs.select( i );

        assertEquals( expectedAge, bull.getAge() );
        assertEquals( expectedWeight, bull.getWeight(), 0e-3 );
        assertEquals( expectedHasWings, bull.getHasWings() );
    }

    private void assertBull( long i, int expectedAge, boolean expectedHasWings, float expectedWeight, RedBullStruct bull ) {
        structs.selectInto( bull, i );

        assertEquals( expectedAge, bull.getAge() );
        assertEquals( expectedHasWings, bull.getHasWings() );
        assertEquals( expectedWeight, bull.getWeight(), 0e-3 );
    }

}