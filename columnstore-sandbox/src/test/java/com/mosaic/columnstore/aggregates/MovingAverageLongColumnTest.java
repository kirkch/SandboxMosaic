package com.mosaic.columnstore.aggregates;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.columnstore.columns.LongColumnArray;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import java.util.Map;

import static com.mosaic.columnstore.ColumnTestUtils.assertReferencedCellsEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 *
 */
public class MovingAverageLongColumnTest {

    private SystemX system = new DebugSystem();


// EMPTY SOURCE COLUMN, CALC MA3


    @Test
    public void givenEmptySourceColumn_expectExplanationOfMA3ForAnyRow_toBeZeroWithNoTouchedCells() {
        LongColumn              price = new LongColumnArray( system, "cost", "d", 0 );
        MovingAverageLongColumn ma3   = new MovingAverageLongColumn( system, price, 3 );

        assertEquals( 0, ma3.size() );

        for ( long i=0; i<100; i++ ) {
            CellExplanation explanation = ma3.explain(i);

            assertEquals( "0", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost" );
            assertEquals( "sum(cellValues)/numberOfCells", explanation.toString() );
        }
    }


// SINGLE ROW SOURCE, CALC MA3

    @Test
    public void givenSourceWithSingleRowAt10_expectMA3OfAnyRowBelow10ToBeZero() {
        LongColumn              price = new LongColumnArray( system, "cost", "d", 11 );
        MovingAverageLongColumn ma3   = new MovingAverageLongColumn( system, price, 3 );

        price.set( 10, 100 );

        assertEquals( 11, ma3.size() );

        for ( long i=0; i<9; i++ ) {
            CellExplanation explanation = ma3.explain( i );

            assertEquals( "0", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost" );
            assertEquals( "sum(cellValues)/numberOfCells", explanation.toString() );
        }
    }

    @Test
    public void givenSourceWithSingleRowAt10_expectMA3OfAnyRowGTE10_toBeValue() {
        LongColumn              price = new LongColumnArray( system, "cost", "d", 11 );
        MovingAverageLongColumn ma3   = new MovingAverageLongColumn( system, price, 3 );

        price.set( 10, 100 );

        for ( long i=10; i<100; i++ ) {
            CellExplanation explanation = ma3.explain( i );

            assertEquals( "100", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost", 10 );
            assertEquals( "sum(cellValues)/numberOfCells", explanation.toString() );
        }
    }


// TWO ROW SOURCE, CALC MA3


    @Test
    public void givenSourceWithRowsAt10And12_expectMA3OfAnyRowBelow10ToBeZero() {
        LongColumn              price = new LongColumnArray( system, "cost", "d", 13 );
        MovingAverageLongColumn ma3   = new MovingAverageLongColumn( system, price, 3 );

        price.set( 10, 4 );
        price.set( 12, 6 );

        assertEquals( 13, ma3.size() );

        for ( long i=0; i<9; i++ ) {
            CellExplanation explanation = ma3.explain( i );

            assertEquals( "0", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost" );
            assertEquals( "sum(cellValues)/numberOfCells", explanation.toString() );
        }
    }

    @Test
    public void givenSourceWithRowsAt10And12_expectMA3OfAnyRowBetween10And11_toBeEqualToFirstValue() {
        LongColumn              price = new LongColumnArray( system, "cost", "d", 13 );
        MovingAverageLongColumn ma3   = new MovingAverageLongColumn( system, price, 3 );

        price.set( 10, 4 );
        price.set( 12, 6 );

        for ( long i=10; i<11; i++ ) {
            CellExplanation explanation = ma3.explain( i );

            assertEquals( "4", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost", 10 );
            assertEquals( "sum(cellValues)/numberOfCells", explanation.toString() );
        }
    }

    @Test
    public void givenSourceWithRowsAtRow12_expectMA3OfAnyRowFrom12OnwardsToBeTheAvgOfTheTwoValues() {
        LongColumn              price = new LongColumnArray( system, "cost", "d", 13 );
        MovingAverageLongColumn ma3   = new MovingAverageLongColumn( system, price, 3 );

        price.set( 10, 4 );
        price.set( 12, 6 );

        for ( long i=12; i<100; i++ ) {
            CellExplanation explanation = ma3.explain( i );

            assertEquals( "5", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost", 10, 12 );
            assertEquals( "sum(cellValues)/numberOfCells", explanation.toString() );
        }
    }


// FOUR ROW SOURCE, CALC MA3

    @Test
    public void givenSourceFourRows_expectMA3OfFourthRow_toEqualAverageOfLastThreeRows() {
        LongColumn              price = new LongColumnArray( system, "cost", "d", 15 );
        MovingAverageLongColumn ma3   = new MovingAverageLongColumn( system, price, 3 );

        price.set( 10, 1 );
        price.set( 12, 2 );
        price.set( 13, 3 );
        price.set( 14, 4 );

        CellExplanation explanation = ma3.explain( 15 );

        assertEquals( "3", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 14,13,12 );
        assertEquals( "sum(cellValues)/numberOfCells", explanation.toString() );
    }

    @Test
    public void givenSourceFourRows_expectMA3OfThirdRow_toEqualAverageOfLastThreeRows() {
        LongColumn              price = new LongColumnArray( system, "cost", "d", 15 );
        MovingAverageLongColumn ma3   = new MovingAverageLongColumn( system, price, 3 );

        price.set( 10, 1 );
        price.set( 12, 2 );
        price.set( 13, 3 );
        price.set( 14, 4 );

        CellExplanation explanation = ma3.explain( 13 );

        assertEquals( "2", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 13,12,10 );
        assertEquals( "sum(cellValues)/numberOfCells", explanation.toString() );
    }


// LOTS OF DATA

    @Test
    public void given2000Rows_calcMA3ForEachRow() {
        bulkMATest( 2000, 3 );
    }

    @Test
    public void given2000Rows_calcMA5ForEachRow() {
        bulkMATest( 2000, 5 );
    }

    @Test
    public void given2000Rows_calcMA10ForEachRow() {
        bulkMATest( 2000, 10 );
    }

    @Test
    public void given2000Rows_calcMA100ForEachRow() {
        bulkMATest( 2000, 100 );
    }


    private void bulkMATest( long numRows, int numSamples ) {
        LongColumn              price = new LongColumnArray( system, "cost", "d", numRows );
        MovingAverageLongColumn ma3   = new MovingAverageLongColumn( system, price, numSamples );

        for ( long i=0; i<numRows; i++ ) {
            price.set(i, i+1 );
        }

        assertEquals( numRows, price.size() );
        assertEquals( numRows, ma3.size() );

        long m = 0;


        // sum up initial 'n' samples
        for ( long i=0; i<numSamples; i++ ) {
            m += price.get(i);
        }

        // start asserts from after the first 'n' samples
        for ( long i=numSamples; i<numRows; i++ ) {
            m -= price.get(i-numSamples);
            m += price.get(i);

            assertEquals( "row "+i, m/numSamples, ma3.get(i), 1e-5 );
        }
    }

}
