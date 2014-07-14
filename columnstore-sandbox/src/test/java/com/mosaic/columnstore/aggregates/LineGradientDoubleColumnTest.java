package com.mosaic.columnstore.aggregates;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.Columns;
import com.mosaic.columnstore.DoubleColumn;
import com.mosaic.columnstore.columns.DoubleColumnArray;
import org.junit.Test;

import static com.mosaic.columnstore.ColumnTestUtils.assertReferencedCellsEquals;
import static org.junit.Assert.assertEquals;


/**
 *
 */
public class LineGradientDoubleColumnTest {


// EMPTY SOURCE COLUMN, CALC MA3

    @Test
    public void givenEmptySourceColumn_expectExplanationForAnyRow_toBeZeroWithNoTouchedCells() {
        int rowCount = 100;

        DoubleColumn price    = new DoubleColumnArray( "cost", "d", rowCount);
        DoubleColumn gradient = new LineGradientDoubleColumn( price, 3 );

        assertEquals( 100, gradient.size() );

        for ( int i=0; i<rowCount; i++ ) {
            CellExplanation explanation = gradient.explain(i);

            assertEquals( "0.00", explanation.getFormattedValue() );
            assertReferencedCellsEquals( explanation, "cost" );
            assertEquals( "GRADIENT(cost)", explanation.toString() );
        }
    }

// ONE POINT SOURCE

    @Test
    public void givenASinglePoint_() {
        DoubleColumn price    = Columns.newDoubleColumn( "cost", "d", 4 );
        DoubleColumn gradient = new LineGradientDoubleColumn( price, 3 );

        assertEquals( 1, gradient.size() );

        CellExplanation explanation = gradient.explain(3);

        assertEquals( "0.00", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 0);
        assertEquals( "GRADIENT(cost[0])", explanation.toString() );
    }

// TWO POINT SOURCE

    @Test
    public void givenATwoPointsGoingUp() {
        DoubleColumn price    = Columns.newDoubleColumn( "cost", "d", 4, 5 );
        DoubleColumn gradient = new LineGradientDoubleColumn( price, 3 );

        assertEquals( 2, gradient.size() );
        assertEquals( 1.0, gradient.get(2), 1e-3 );


        CellExplanation explanation = gradient.explain(2);

        assertEquals( "1.00", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 0, 1);
        assertEquals( "GRADIENT(cost[0,1])", explanation.toString() );
    }

    @Test
    public void givenATwoPointsGoingSideways() {
        DoubleColumn price    = Columns.newDoubleColumn( "cost", "d", 4, 4 );
        DoubleColumn gradient = new LineGradientDoubleColumn( price, 3 );

        assertEquals( 2, gradient.size() );
        assertEquals( 0.0, gradient.get(2), 1e-3 );


        CellExplanation explanation = gradient.explain(2);

        assertEquals( "0.00", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 0, 1);
        assertEquals( "GRADIENT(cost[0,1])", explanation.toString() );
    }

    @Test
    public void givenATwoPointsGoingSlightlyUp() {
        DoubleColumn price    = Columns.newDoubleColumn( "cost", "d", 4, 4.15 );
        DoubleColumn gradient = new LineGradientDoubleColumn( price, 3 );

        assertEquals( 2, gradient.size() );
        assertEquals( 0.15, gradient.get(2), 1e-3 );


        CellExplanation explanation = gradient.explain(2);

        assertEquals( "0.15", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 0, 1);
        assertEquals( "GRADIENT(cost[0,1])", explanation.toString() );
    }

    @Test
    public void givenATwoPointsGoingSlightlyDown() {
        DoubleColumn price    = Columns.newDoubleColumn( "cost", "d", 4, 3 );
        DoubleColumn gradient = new LineGradientDoubleColumn( price, 3 );

        assertEquals( 2, gradient.size() );
        assertEquals( -1.0, gradient.get(2), 1e-3 );


        CellExplanation explanation = gradient.explain(2);

        assertEquals( "-1.00", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 0, 1);
        assertEquals( "GRADIENT(cost[0,1])", explanation.toString() );
    }

// THREE POINT SOURCE

    @Test
    public void givenAThreePointsGoingUp() {
        DoubleColumn price    = Columns.newDoubleColumn( "cost", "d", 4, 5, 6 );
        DoubleColumn gradient = new LineGradientDoubleColumn( price, 3 );

        assertEquals( 3, gradient.size() );
        assertEquals( 1.0, gradient.get(3), 1e-3 );


        CellExplanation explanation = gradient.explain(3);

        assertEquals( "1.00", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 0, 1, 2);
        assertEquals( "GRADIENT(cost[0,1,2])", explanation.toString() );
    }

    @Test
    public void givenAThreePointsGoingSideways() {
        DoubleColumn price    = Columns.newDoubleColumn( "cost", "d", 4.9, 5, 4.9 );
        DoubleColumn gradient = new LineGradientDoubleColumn( price, 3 );

        assertEquals( 3, gradient.size() );
        assertEquals( 0, gradient.get(3), 1e-3 );


        CellExplanation explanation = gradient.explain(3);

        assertEquals( "0.00", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 0, 1, 2);
        assertEquals( "GRADIENT(cost[0,1,2])", explanation.toString() );
    }

    @Test
    public void givenAThreePointsGoingDown() {
        DoubleColumn price    = Columns.newDoubleColumn( "cost", "d", 6, 5, 4 );
        DoubleColumn gradient = new LineGradientDoubleColumn( price, 3 );

        assertEquals( 3, gradient.size() );
        assertEquals( -1.0, gradient.get(3), 1e-3 );


        CellExplanation explanation = gradient.explain(3);

        assertEquals( "-1.00", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 0, 1, 2);
        assertEquals( "GRADIENT(cost[0,1,2])", explanation.toString() );
    }

// 4 point line

    @Test
    public void givenFourPointsGoingDown_expectOnlyThreePointsToBeConsidered() {
        DoubleColumn price    = Columns.newDoubleColumn( "cost", "d", 101, 5, 4, 3 );
        DoubleColumn gradient = new LineGradientDoubleColumn( price, 3 );

        assertEquals( 4, gradient.size() );
        assertEquals( -1.0, gradient.get(4), 1e-3 );


        CellExplanation explanation = gradient.explain(4);

        assertEquals( "-1.00", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 1, 2, 3);
        assertEquals( "GRADIENT(cost[1,2,3])", explanation.toString() );
    }

    @Test
    public void givenFourPointsGoingUpUsingAllFourPoints_expectOnlyThreePointsToBeConsidered() {
        DoubleColumn price    = Columns.newDoubleColumn( "cost", "d", 1, 1.5, 2, 2.5 );
        DoubleColumn gradient = new LineGradientDoubleColumn( price, 4 );

        assertEquals( 4, gradient.size() );
        assertEquals( 0.5, gradient.get(4), 1e-3 );


        CellExplanation explanation = gradient.explain(4);

        assertEquals( "0.50", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, "cost", 0, 1, 2, 3);
        assertEquals( "GRADIENT(cost[0,1,2,3])", explanation.toString() );
    }

}
