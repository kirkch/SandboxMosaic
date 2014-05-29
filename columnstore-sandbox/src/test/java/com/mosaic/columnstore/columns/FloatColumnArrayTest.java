package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.FloatColumn;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 */
public class FloatColumnArrayTest {

    // EMPTY COLUMN

    @Test
    public void givenEmptyColumn_getColumnName() {
        FloatColumn col = new FloatColumnArray( "col1" );

        assertEquals( "col1", col.getColumnName() );
    }

    @Test
    public void givenEmptyColumn_getRowCount_expectZero() {
        FloatColumn col = new FloatColumnArray( "col1" );

        assertEquals( 0, col.rowCount() );
    }

    @Test
    public void givenEmptyColumn_askIsSecondRowSet_expectFalse() {
        FloatColumn col = new FloatColumnArray( "col1" );

        assertFalse( col.isSet(1) );
    }

    @Test
    public void givenEmptyColumn_getSecondRow_expectZero() {
        FloatColumn col = new FloatColumnArray( "col1" );

        assertEquals( 0, col.get(1), 1e-5 );
    }

    @Test
    public void givenEmptyColumn_explainSecondRow_expectNull() {
        FloatColumn col = new FloatColumnArray( "col1" );

        assertNull( col.explain(1) );
    }


// SINGLE ROW COLUMN

    @Test
    public void givenEmptyColumn_setSecondRow_getRowCount_expectTwo() {
        FloatColumn col = new FloatColumnArray( "col1" );

        col.set( 1, 42 );

        assertEquals( 2, col.rowCount() );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsSecondRowSet_expectTrue() {
        FloatColumn col = new FloatColumnArray( "col1" );

        col.set( 1, 42 );

        assertTrue( col.isSet(1) );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsFirstRowSet_expectFalse() {
        FloatColumn col = new FloatColumnArray( "col1" );

        col.set( 1, 42 );

        assertFalse( col.isSet(0) );
    }

    @Test
    public void givenValueInSecondRow_explainSecondRow_expectExplanation() {
        FloatColumn col = new FloatColumnArray( "col1" );

        col.set( 1, 42.1f );

        CellExplanation<Float> explanation = col.explain(1);

        assertEquals( "col1", explanation.getColumnNameNbl() );
        assertEquals( 1, explanation.getRowIds().length );
        assertEquals( 1, explanation.getRowIds()[0] );
        assertNull( explanation.getOperandsNbl() );
        assertNull( explanation.getOpNameNbl() );
        assertEquals( 42.1f, explanation.getValue().floatValue(), 1e-5 );
    }

}
