package com.mosaic.columnstore.aggregates;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.columnstore.columns.LongColumnArray;
import org.junit.Test;

import static com.mosaic.columnstore.ColumnTestUtils.assertReferencedCellsEquals;
import static org.junit.Assert.assertEquals;


/**
 *
 */
public class SumLastNLongColumnTest {

    // EMPTY SOURCE COLUMN, CALC SUM3

    @Test
    public void givenEmptySourceColumn_expectExplanationOfSUM3ForAnyRow_toBeZeroWithNoTouchedCells() {
        LongColumn            price = new LongColumnArray( "cost", "d", 0 );
        SumLastNLongColumn sum3   = new SumLastNLongColumn( price, 3 );

        assertEquals( 0, sum3.size() );

        for ( long i=0; i<100; i++ ) {
            CellExplanation explanation = sum3.explain(i);

            assertEquals( "0", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost" );
            assertEquals( "SUM3(cost)", explanation.toString() );
        }
    }


// SINGLE ROW SOURCE, CALC SUM3

    @Test
    public void givenSourceWithSingleRowAt10_expectSUM3OfAnyRowBelow10ToBeZero() {
        LongColumn            price = new LongColumnArray( "cost", "d", 11 );
        SumLastNLongColumn sum3  = new SumLastNLongColumn( "colName", "desc", "OP", price, 3 );

        price.set( 10, 100 );

        assertEquals( 11, sum3.size() );

        for ( long i=0; i<9; i++ ) {
            CellExplanation explanation = sum3.explain( i );

            assertEquals( "0", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost" );
            assertEquals( "OP(cost)", explanation.toString() );
        }
    }

    @Test
    public void givenSourceWithSingleRowAt10_expectSUM3OfAnyRowGTE10_toBeValue() {
        LongColumn            price = new LongColumnArray( "cost", "d", 11 );
        SumLastNLongColumn sum3  = new SumLastNLongColumn( price, 3 );

        price.set( 10, 100 );

        for ( long i=10; i<100; i++ ) {
            CellExplanation explanation = sum3.explain( i );

            assertEquals( "100", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost", 10 );
            assertEquals( "SUM3(cost[10])", explanation.toString() );
        }
    }


// TWO ROW SOURCE, CALC SUM3

    @Test
    public void givenSourceWithRowsAt10And12_expectSUM3OfAnyRowBelow10ToBeZero() {
        LongColumn            price = new LongColumnArray( "cost", "d", 13 );
        SumLastNLongColumn sum3  = new SumLastNLongColumn( price, 3 );

        price.set( 10, 4 );
        price.set( 12, 6 );

        assertEquals( 13, sum3.size() );

        for ( long i=0; i<9; i++ ) {
            CellExplanation explanation = sum3.explain( i );

            assertEquals( "0", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost" );
            assertEquals( "SUM3(cost)", explanation.toString() );
        }
    }

    @Test
    public void givenSourceWithRowsAt10And12_expectSUM3OfAnyRowBetween10And11_toBeEqualToFirstValue() {
        LongColumn            price = new LongColumnArray( "cost", "d", 13 );
        SumLastNLongColumn sum3  = new SumLastNLongColumn( price, 3 );

        price.set( 10, 4 );
        price.set( 12, 6 );

        for ( long i=10; i<11; i++ ) {
            CellExplanation explanation = sum3.explain( i );

            assertEquals( "4", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost", 10 );
            assertEquals( "SUM3(cost[10])", explanation.toString() );
        }
    }

    @Test
    public void givenSourceWithRowsAtRow12_expectSUM3OfAnyRowFrom12OnwardsToBeTheSumOfTheTwoValues() {
        LongColumn            price = new LongColumnArray( "cost", "d", 13 );
        SumLastNLongColumn sum3  = new SumLastNLongColumn( price, 3 );

        price.set( 10, 4 );
        price.set( 12, 6 );

        for ( long i=12; i<100; i++ ) {
            CellExplanation explanation = sum3.explain( i );

            assertEquals( "10", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost", 10, 12 );
            assertEquals( "SUM3(cost[10,12])", explanation.toString() );
        }
    }


// FOUR ROW SOURCE, CALC SUM3

    @Test
    public void givenSourceFourRows_expectSUM3OfFourthRow_toEqualSumOfLastThreeRows() {
        LongColumn            price = new LongColumnArray( "cost", "d", 15 );
        SumLastNLongColumn sum3  = new SumLastNLongColumn( price, 3 );

        price.set( 10, 1 );
        price.set( 12, 2 );
        price.set( 13, 3 );
        price.set( 14, 4 );

        CellExplanation explanation = sum3.explain( 15 );

        assertEquals( "9", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 14,13,12 );
        assertEquals( "SUM3(cost[12,13,14])", explanation.toString() );
    }

    @Test
    public void givenSourceFourRows_expectSUM3OfThirdRow_toEqualSumOfLastThreeRows() {
        LongColumn            price = new LongColumnArray( "cost", "d", 15 );
        SumLastNLongColumn sum3  = new SumLastNLongColumn( price, 3 );

        price.set( 10, 1 );
        price.set( 12, 2 );
        price.set( 13, 3 );
        price.set( 14, 4 );

        CellExplanation explanation = sum3.explain( 13 );

        assertEquals( "6", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 13,12,10 );
        assertEquals( "SUM3(cost[10,12,13])", explanation.toString() );
    }

    @Test
    public void givenSourceFourRows_expectSUM3OfRow11_toEqualRow10Only() {
        LongColumn            price = new LongColumnArray( "cost", "d", 15 );
        SumLastNLongColumn sum3  = new SumLastNLongColumn( price, 3 );

        price.set( 10, 1 );
        price.set( 12, 2 );
        price.set( 13, 3 );
        price.set( 14, 4 );

        CellExplanation explanation = sum3.explain( 11 );

        assertEquals( "1", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 10 );
        assertEquals( "SUM3(cost[10])", explanation.toString() );
    }

}
