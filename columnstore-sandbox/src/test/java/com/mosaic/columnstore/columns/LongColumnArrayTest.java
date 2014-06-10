package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.LongColumn;
import org.junit.Test;

import static org.junit.Assert.*;


/**
*
*/
public class LongColumnArrayTest {

    // EMPTY COLUMN

    @Test
    public void givenEmptyColumn_getColumnName() {
        LongColumn col = new LongColumnArray( "col1", "d", 3 );

        assertEquals( "col1", col.getColumnName() );
    }

    @Test
    public void givenEmptyColumn_getRowCount_expectZero() {
        LongColumn col = new LongColumnArray( "col1", "d", 0 );

        assertEquals( 0, col.size() );
    }

    @Test
    public void givenEmptyColumn_askIsSecondRowSet_expectFalse() {
        LongColumn col = new LongColumnArray( "col1", "d", 2 );

        assertFalse( col.isSet( 1 ) );
    }

    @Test
    public void givenEmptyColumn_getSecondRow_expectZero() {
        LongColumn col = new LongColumnArray( "col1", "d", 2 );

        assertEquals( 0, col.get( 1 ) );
    }

    @Test
    public void givenEmptyColumn_explainSecondRow_expectNull() {
        LongColumn col = new LongColumnArray( "col1", "d", 2 );

        assertNull( col.explain(1) );
    }


// SINGLE ROW COLUMN

    @Test
    public void givenEmptyColumn_setSecondRow_getRowCount_expectTwo() {
        LongColumn col = new LongColumnArray( "col1", "d", 2 );

        col.set( 1, 42 );

        assertEquals( 2, col.size() );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsSecondRowSet_expectTrue() {
        LongColumn col = new LongColumnArray( "col1", "d", 2 );

        col.set( 1, 42 );

        assertTrue( col.isSet(1) );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsFirstRowSet_expectFalse() {
        LongColumn col = new LongColumnArray( "col1", "d", 2 );

        col.set( 1, 42 );

        assertFalse( col.isSet(0) );
    }

    @Test
    public void givenValueInSecondRow_explainSecondRow_expectExplanation() {
        LongColumn col = new LongColumnArray( "col1", "d", 2 );

        col.set( 1, 42 );

        CellExplanation explanation = col.explain(1);

        assertEquals( "42", explanation.getFormattedValue() );
        assertTrue( explanation.getReferencedCells().isEmpty() );
        assertEquals( "42", explanation.toString() );
    }

}
