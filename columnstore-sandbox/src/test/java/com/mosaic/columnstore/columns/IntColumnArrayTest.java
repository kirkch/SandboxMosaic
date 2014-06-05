package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.IntColumn;
import org.junit.Test;

import static org.junit.Assert.*;


/**
*
*/
public class IntColumnArrayTest {

    // EMPTY COLUMN

    @Test
    public void givenEmptyColumn_getColumnName() {
        IntColumn col = new IntColumnArray( "col1", "d" );

        assertEquals( "col1", col.getColumnName() );
    }

    @Test
    public void givenEmptyColumn_getRowCount_expectZero() {
        IntColumn col = new IntColumnArray( "col1", "d" );

        assertEquals( 0, col.rowCount() );
    }

    @Test
    public void givenEmptyColumn_askIsSecondRowSet_expectFalse() {
        IntColumn col = new IntColumnArray( "col1", "d" );

        assertFalse( col.isSet( 1 ) );
    }

    @Test
    public void givenEmptyColumn_getSecondRow_expectZero() {
        IntColumn col = new IntColumnArray( "col1", "d" );

        assertEquals( 0, col.get( 1 ) );
    }

    @Test
    public void givenEmptyColumn_explainSecondRow_expectNull() {
        IntColumn col = new IntColumnArray( "col1", "d" );

        assertNull( col.explain(1) );
    }


// SINGLE ROW COLUMN

    @Test
    public void givenEmptyColumn_setSecondRow_getRowCount_expectTwo() {
        IntColumn col = new IntColumnArray( "col1", "d" );

        col.set( 1, 42 );

        assertEquals( 2, col.rowCount() );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsSecondRowSet_expectTrue() {
        IntColumn col = new IntColumnArray( "col1", "d" );

        col.set( 1, 42 );

        assertTrue( col.isSet(1) );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsFirstRowSet_expectFalse() {
        IntColumn col = new IntColumnArray( "col1", "d" );

        col.set( 1, 42 );

        assertFalse( col.isSet(0) );
    }

    @Test
    public void givenValueInSecondRow_explainSecondRow_expectExplanation() {
        IntColumn col = new IntColumnArray( "col1", "d" );

        col.set( 1, -42 );

        CellExplanation explanation = col.explain(1);

        assertEquals( "-42", explanation.getFormattedValue() );
        assertTrue( explanation.getReferencedCells().isEmpty() );
        assertEquals( "-42", explanation.toString() );
    }

}
