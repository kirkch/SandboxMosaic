package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.FloatColumn;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 */
public class FloatColumnArrayTest {

    private SystemX system = new DebugSystem();


    // EMPTY COLUMN

    @Test
    public void givenEmptyColumn_getColumnName() {
        FloatColumn col = new FloatColumnArray( system, "col1", "d", 2 );

        assertEquals( "col1", col.getColumnName() );
    }

    @Test
    public void givenEmptyColumn_getRowCount_expectZero() {
        FloatColumn col = new FloatColumnArray( system, "col1", "d", 0 );

        assertEquals( 0, col.size() );
    }

    @Test
    public void givenEmptyColumn_askIsSecondRowSet_expectFalse() {
        FloatColumn col = new FloatColumnArray( system, "col1", "d", 2 );

        assertFalse( col.isSet(1) );
    }

    @Test
    public void givenEmptyColumn_getSecondRow_expectZero() {
        FloatColumn col = new FloatColumnArray( system, "col1", "d", 2 );

        assertEquals( 0, col.get(1), 1e-5 );
    }

    @Test
    public void givenEmptyColumn_explainSecondRow_expectNull() {
        FloatColumn col = new FloatColumnArray( system, "col1", "d", 2 );

        assertNull( col.explain(1) );
    }


// SINGLE ROW COLUMN

    @Test
    public void givenEmptyColumn_setSecondRow_getRowCount_expectTwo() {
        FloatColumn col = new FloatColumnArray( system, "col1", "d", 2 );

        col.set( 1, 42 );

        assertEquals( 2, col.size() );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsSecondRowSet_expectTrue() {
        FloatColumn col = new FloatColumnArray( system, "col1", "d", 2 );

        col.set( 1, 42 );

        assertTrue( col.isSet(1) );
    }

    @Test
    public void givenEmptyColumn_setSecondRow_askIsFirstRowSet_expectFalse() {
        FloatColumn col = new FloatColumnArray( system, "col1", "d", 2 );

        col.set( 1, 42 );

        assertFalse( col.isSet(0) );
    }

    @Test
    public void givenValueInSecondRow_explainSecondRow_expectExplanation() {
        FloatColumn col = new FloatColumnArray( system, "col1", "d", 2 );

        col.set( 1, 42.1f );

        CellExplanation explanation = col.explain(1);

        assertEquals( "42.10", explanation.getFormattedValue() );
        assertTrue( explanation.getReferencedCells().isEmpty() );
        assertEquals( "42.10", explanation.toString() );
    }

}
