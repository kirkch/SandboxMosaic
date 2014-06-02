package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.Column;
import org.junit.Test;

import static org.junit.Assert.*;


/**
*
*/
public class ColumnOnHeapTest {

// EMPTY COLUMN

    @Test
    public void givenEmptyColumn_getColumnName() {
        Column<String> col = new ColumnOnHeap<>( "col1" );

        assertEquals( "col1", col.getColumnName() );
    }

    @Test
    public void givenEmptyColumn_getRowCount_expectZero() {
        Column<String> col = new ColumnOnHeap<>( "col1" );

        assertEquals( 0, col.rowCount() );
    }

    @Test
    public void givenEmptyColumn_askIsSecondRowSet_expectFalse() {
        Column<String> col = new ColumnOnHeap<>( "col1" );

        assertFalse( col.isSet( 1 ) );
    }

    @Test
    public void givenEmptyColumn_getSecondRow_expectNull() {
        Column<String> col = new ColumnOnHeap<>( "col1" );

        assertNull( col.get( 1 ) );
    }

    @Test
    public void givenEmptyColumn_explainSecondRow_expectNull() {
        Column<String> col = new ColumnOnHeap<>( "col1" );

        assertNull( col.explain(1) );
    }


// SINGLE ROW COLUMN

    @Test
    public void givenEmptyColumn_setSecondRow_getRowCount_expectTwo() {
        Column<String> col = new ColumnOnHeap<>( "col1" );

        col.set( 1, "foo" );

        assertEquals( 2, col.rowCount() );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsSecondRowSet_expectTrue() {
        Column<String> col = new ColumnOnHeap<>( "col1" );

        col.set( 1, "foo" );

        assertTrue( col.isSet( 1 ) );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsFirstRowSet_expectFalse() {
        Column<String> col = new ColumnOnHeap<>( "col1" );

        col.set( 1, "foo" );

        assertFalse( col.isSet( 0 ) );
    }

    @Test
    public void givenValueInSecondRow_explainSecondRow_expectExplanation() {
        Column<String> col = new ColumnOnHeap<>( "col1" );

        col.set( 1, "foo" );

        CellExplanation explanation = col.explain( 1 );

        assertEquals( "foo", explanation.getFormattedValue() );
        assertTrue( explanation.getReferencedCells().isEmpty() );
        assertEquals( "foo", explanation.toString() );
    }

}
