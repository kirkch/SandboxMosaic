package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.ObjectColumn;
import org.junit.Test;

import static org.junit.Assert.*;


/**
*
*/
public class ColumnOnHeapTest {

// EMPTY COLUMN

    @Test
    public void givenEmptyColumn_getColumnName() {
        ObjectColumn<String> col = new ObjectColumnArray<>( "col1", "d" );

        assertEquals( "col1", col.getColumnName() );
    }

    @Test
    public void givenEmptyColumn_getRowCount_expectZero() {
        ObjectColumn<String> col = new ObjectColumnArray<>( "col1", "d" );

        assertEquals( 0, col.rowCount() );
    }

    @Test
    public void givenEmptyColumn_askIsSecondRowSet_expectFalse() {
        ObjectColumn<String> col = new ObjectColumnArray<>( "col1", "d" );

        assertFalse( col.isSet( 1 ) );
    }

    @Test
    public void givenEmptyColumn_getSecondRow_expectNull() {
        ObjectColumn<String> col = new ObjectColumnArray<>( "col1", "d" );

        assertNull( col.get( 1 ) );
    }

    @Test
    public void givenEmptyColumn_explainSecondRow_expectNull() {
        ObjectColumn<String> col = new ObjectColumnArray<>( "col1", "d" );

        assertNull( col.explain(1) );
    }


// SINGLE ROW COLUMN

    @Test
    public void givenEmptyColumn_setSecondRow_getRowCount_expectTwo() {
        ObjectColumn<String> col = new ObjectColumnArray<>( "col1", "d" );

        col.set( 1, "foo" );

        assertEquals( 2, col.rowCount() );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsSecondRowSet_expectTrue() {
        ObjectColumn<String> col = new ObjectColumnArray<>( "col1", "d" );

        col.set( 1, "foo" );

        assertTrue( col.isSet( 1 ) );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsFirstRowSet_expectFalse() {
        ObjectColumn<String> col = new ObjectColumnArray<>( "col1", "d" );

        col.set( 1, "foo" );

        assertFalse( col.isSet( 0 ) );
    }

    @Test
    public void givenValueInSecondRow_explainSecondRow_expectExplanation() {
        ObjectColumn<String> col = new ObjectColumnArray<>( "col1", "d" );

        col.set( 1, "foo" );

        CellExplanation explanation = col.explain( 1 );

        assertEquals( "foo", explanation.getFormattedValue() );
        assertTrue( explanation.getReferencedCells().isEmpty() );
        assertEquals( "foo", explanation.toString() );
    }

}
