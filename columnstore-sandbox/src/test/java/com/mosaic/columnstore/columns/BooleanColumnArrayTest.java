package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.BooleanColumn;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


/**
 *
 */
public class BooleanColumnArrayTest {

    // EMPTY COLUMN

    @Test
    public void givenEmptyColumn_getColumnName() {
        BooleanColumn col = new BooleanColumnArray( "col1", "d", 2 );

        assertEquals( "col1", col.getColumnName() );
    }

    @Test
    public void givenEmptyColumn_getRowCount_expectZero() {
        BooleanColumn col = new BooleanColumnArray( "col1", "d", 0 );

        assertEquals( 0, col.size() );
    }

    @Test
    public void givenEmptyColumn_askIsSecondRowSet_expectFalse() {
        BooleanColumn col = new BooleanColumnArray( "col1", "d", 2 );

        assertFalse( col.isSet(1) );
    }

    @Test
    public void givenEmptyColumn_getSecondRow_expectZero() {
        BooleanColumn col = new BooleanColumnArray( "col1", "d", 2 );

        assertEquals( false, col.get(1) );
    }

    @Test
    public void givenEmptyColumn_explainSecondRow_expectNull() {
        BooleanColumn col = new BooleanColumnArray( "col1", "d", 2 );

        assertNull( col.explain(1) );
    }


// SINGLE ROW COLUMN

    @Test
    public void givenEmptyColumn_setSecondRow_getRowCount_expectTwo() {
        BooleanColumn col = new BooleanColumnArray( "col1", "d", 2 );

        col.set( 1, true );

        assertEquals( 2, col.size() );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsSecondRowSet_expectTrue() {
        BooleanColumn col = new BooleanColumnArray( "col1", "d", 2 );

        col.set( 1, false );

        assertTrue( col.isSet(1) );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsFirstRowSet_expectFalse() {
        BooleanColumn col = new BooleanColumnArray( "col1", "d", 2 );

        col.set( 1, false );

        assertFalse( col.isSet(0) );
    }

    @Test
    public void givenValueInSecondRow_explainSecondRow_expectExplanation() {
        BooleanColumn col = new BooleanColumnArray( "col1", "d", 2 );

        col.set( 1, true );

        CellExplanation explanation = col.explain(1);

        assertEquals( "true", explanation.getFormattedValue() );
        assertTrue( explanation.getReferencedCells().isEmpty() );
        assertEquals( "true", explanation.toString() );
    }
    
}
