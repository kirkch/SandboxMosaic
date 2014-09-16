package com.mosaic.columnstore.aggregates;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.columnstore.columns.LongColumnArray;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import static com.mosaic.columnstore.ColumnTestUtils.assertReferencedCellsEquals;
import static org.junit.Assert.assertEquals;


/**
 *
 */
public class StandardDeviationLongColumnTest {

    private SystemX system = new DebugSystem();


// EMPTY SOURCE COLUMN, CALC STDDEV

    @Test
    public void givenEmptySourceColumn_expectExplanationOfStdDev3ForAnyRow_toBeZeroWithNoTouchedCells() {
        LongColumn                  price  = new LongColumnArray( system, "cost", "d", 0 );
        StandardDeviationLongColumn stdDev = new StandardDeviationLongColumn( system, price, 3 );

        assertEquals( 0, stdDev.size() );

        for ( long i=0; i<100; i++ ) {
            CellExplanation explanation = stdDev.explain(i);

            assertEquals( "0", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost" );
            assertEquals( "stddev(cells)", explanation.toString() );
        }
    }


// SINGLE ROW SOURCE, CALC MA3

    @Test
    public void givenSourceWithSingleRowAt10_expectStdDev3OfAnyRowBelow10ToBeZero() {
        LongColumn                  price  = new LongColumnArray( system, "cost", "d", 11 );
        StandardDeviationLongColumn stdDev = new StandardDeviationLongColumn( system, price, 3 );

        price.set( 10, 100 );

        assertEquals( 11, stdDev.size() );

        for ( long i=0; i<9; i++ ) {
            CellExplanation explanation = stdDev.explain( i );

            assertEquals( "0", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost" );
            assertEquals( "stddev(cells)", explanation.toString() );
        }
    }

    @Test
    public void givenSourceWithSingleRowAt10_expectStdDev3OfAnyRowGTE10_toBeZero() {
        LongColumn                  price  = new LongColumnArray( system, "cost", "d", 11 );
        StandardDeviationLongColumn stdDev = new StandardDeviationLongColumn( system, price, 3 );

        price.set( 10, 100 );

        for ( long i=10; i<100; i++ ) {
            CellExplanation explanation = stdDev.explain( i );

            assertEquals( "0", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost", 10 );
            assertEquals( "stddev(cells)", explanation.toString() );
        }
    }


// TWO ROW SOURCE, CALC MA3


    @Test
    public void givenSourceWithRowsAt10And12_expectStdDev3OfAnyRowBelow10ToBeZero() {
        LongColumn                  price  = new LongColumnArray( system, "cost", "d", 13 );
        StandardDeviationLongColumn stdDev = new StandardDeviationLongColumn( system, price, 3 );

        price.set( 10, 4 );
        price.set( 12, 6 );

        assertEquals( 13, stdDev.size() );

        for ( long i=0; i<9; i++ ) {
            CellExplanation explanation = stdDev.explain( i );

            assertEquals( "0", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost" );
            assertEquals( "stddev(cells)", explanation.toString() );
        }
    }

    @Test
    public void givenSourceWithRowsAt10And12_expectStdDev3OfAnyRowBetween10And11_toBeEqualZero() {
        LongColumn                  price  = new LongColumnArray( system, "cost", "d", 13 );
        StandardDeviationLongColumn stdDev = new StandardDeviationLongColumn( system, price, 3 );

        price.set( 10, 4 );
        price.set( 12, 6 );

        for ( long i=10; i<11; i++ ) {
            CellExplanation explanation = stdDev.explain( i );

            assertEquals( "0", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost", 10 );
            assertEquals( "stddev(cells)", explanation.toString() );
        }
    }

    @Test
    public void givenSourceWithRowsAtRow12_expectStdDev3OfAnyRowFrom12OnwardsToBeTheStdDevOfTheTwoValues() {
        LongColumn                  price  = new LongColumnArray( system, "cost", "d", 13 );
        StandardDeviationLongColumn stdDev = new StandardDeviationLongColumn( system, price, 3 );

        price.set( 10, 4 );
        price.set( 12, 6 );

        for ( long i=12; i<100; i++ ) {
            CellExplanation explanation = stdDev.explain( i );

            assertEquals( "1", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost", 10, 12 );
            assertEquals( "stddev(cells)", explanation.toString() );
        }
    }


// FOUR ROW SOURCE, CALC MA3

    @Test
    public void givenSourceFourRows_expectStdDev3OfFourthRow_toEqualStdDevOfLastThreeRows() {
        LongColumn                  price     = new LongColumnArray( system, "cost", "d", 15 );
        StandardDeviationLongColumn stdDevCol = new StandardDeviationLongColumn( system, price, 3 );

        price.set( 10, -100 );
        price.set( 12, 11 );
        price.set( 13, 5 );
        price.set( 14, 20 );

        CellExplanation explanation = stdDevCol.explain( 15 );

        double mean   = (20+5+11)/3.0;
        double var    = (20-mean)*(20-mean) + (5-mean)*(5-mean) + (11-mean)*(11-mean);
        long   stdDev = Math.round( Math.sqrt(var/3.0) );

        assertEquals( stdDev+"", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 14,13,12 );
        assertEquals( "stddev(cells)", explanation.toString() );
    }

    @Test
    public void givenSourceFourRows_expectStdDev3OfThirdRow_toEqualFirstThreeRows() {
        LongColumn                  price     = new LongColumnArray( system, "cost", "d", 15 );
        StandardDeviationLongColumn stdDevCol = new StandardDeviationLongColumn( system, price, 3 );

        price.set( 10, -100 );
        price.set( 12, 11 );
        price.set( 13, 5 );
        price.set( 14, 20 );

        double mean   = (-100+11+5)/3.0;
        double var    = (-100-mean)*(-100-mean) + (11-mean)*(11-mean) + (5-mean)*(5-mean);
        long   stdDev = Math.round( Math.sqrt(var/3.0) );

        CellExplanation explanation = stdDevCol.explain( 13 );

        assertEquals( stdDev+"", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 13,12,10 );
        assertEquals( "stddev(cells)", explanation.toString() );
    }

}
