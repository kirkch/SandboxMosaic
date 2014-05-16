package com.mosaic.collections;

import com.softwaremosaic.junit.JUnitMosaic;
import org.junit.Test;

import java.lang.ref.WeakReference;

import static org.junit.Assert.*;


/**
 *
 */
public class TableTest {


// EMPTY TABLE

    @Test
    public void givenAnEmptyTable_callRowCount_expectZero() {
        Table table = new Table();
        
        assertEquals( 0, table.rowCount() );
    }

    @Test
    public void givenAnEmptyTable_callColumnCountForRowZero_expectZero() {
        Table table = new Table();
        
        assertEquals( 0, table.columnCount( 0 ) );
    }

    @Test
    public void givenAnEmptyTable_callColumnCountForRowOne_expectZero() {
        Table table = new Table();

        assertEquals( 0, table.columnCount( 1 ) );
    }

    @Test
    public void givenAnEmptyTable_callColumnCountForRowTwo_expectZero() {
        Table table = new Table();

        assertEquals( 0, table.columnCount( 2 ) );
    }

    @Test
    public void givenAnEmptyTable_getFirstCell_expectNull() {
        Table table = new Table();

        assertNull( table.get(0,0) );
    }


// SETTING COLUMN ON FIRST ROW

    @Test
    public void givenAnEmptyTable_setTheFirstCellOfTheFirstRow_expectRowCountOne() {
        Table<String> table = new Table<>();

        table.set( 0, 0, "foo" );

        assertEquals( 1, table.rowCount() );
    }

    @Test
    public void givenAnEmptyTable_setTheFirstCellOfTheFirstRow_retrieveViaGet() {
        Table<String> table = new Table<>();

        table.set( 0, 0, "foo" );

        assertEquals( "foo", table.get(0,0) );
    }

    @Test
    public void givenAnEmptyTable_setTheFirstCellOfTheFirstRow_expectColumnCountOfRowZeroToBeOne() {
        Table<String> table = new Table<>();

        table.set( 0, 0, "foo" );

        assertEquals( 1, table.columnCount( 0 ) );
    }

    @Test
    public void givenAnEmptyTable_setTheFirstCellOfTheFirstRow_expectColumnCountOfRowOneToBeZero() {
        Table<String> table = new Table<>();

        table.set( 0, 0, "foo" );

        assertEquals( 0, table.columnCount( 1 ) );
    }


// SETTING COLUMN ON FIRST ROW

    @Test
    public void givenAnEmptyTable_setTheSecondCellOfTheFirstRow_expectRowCountOne() {
        Table<String> table = new Table<>();

        table.set( 0, 1, "foo" );

        assertEquals( 1, table.rowCount() );
    }

    @Test
    public void givenAnEmptyTable_setTheSecondCellOfTheFirstRow_expectColumnCountOfRowZeroToBeTwo() {
        Table<String> table = new Table<>();

        table.set( 0, 1, "foo" );

        assertEquals( 2, table.columnCount( 0 ) );
    }

    @Test
    public void givenAnEmptyTable_setTheSecondCellOfTheFirstRow_expectColumnCountOfRowOneToBeZero() {
        Table<String> table = new Table<>();

        table.set( 0, 1, "foo" );

        assertEquals( 0, table.columnCount( 1 ) );
    }


// SETTING COLUMN ON SECOND ROW



    @Test
    public void givenAnEmptyTable_setTheSecondCellOfTheSecondRow_expectRowCountOfTwo() {
        Table<String> table = new Table<>();

        table.set( 1, 1, "foo" );

        assertEquals( 2, table.rowCount() );
    }

    @Test
    public void givenAnEmptyTable_setTheSecondCellOfTheSecondRow_expectColumnCountOfRowZeroToBeZero() {
        Table<String> table = new Table<>();

        table.set( 1, 1, "foo" );

        assertEquals( 0, table.columnCount(0) );
    }

    @Test
    public void givenAnEmptyTable_setTheSecondCellOfTheSecondRow_expectColumnCountOfRowOneToBeTwo() {
        Table<String> table = new Table<>();

        table.set( 1, 1, "foo" );

        assertEquals( 2, table.columnCount(1) );
    }


// CLEAR

    @Test
    public void givenANonEmptyTable_clear_expectRowCountToReturnToZero() {
        Table<String> table = new Table<>();

        table.set( 1, 1, "foo" );
        table.clear();

        assertEquals( 0, table.rowCount() );
    }

    @Test
    public void givenANonEmptyTable_clear_expectColumnCountToReturnToZero() {
        Table<String> table = new Table<>();

        table.set( 1, 1, "foo" );
        table.clear();

        assertEquals( 0, table.columnCount(0) );
        assertEquals( 0, table.columnCount(1) );
        assertEquals( 0, table.columnCount( 2 ) );
    }

    @Test
    @SuppressWarnings({"RedundantStringConstructorCall", "UnusedAssignment"})
    public void givenANonEmptyTable_clear_expectContentsToBecomeGCed() {
        Table<String> table = new Table<>();

        String v = new String("foo");
        table.set( 1, 1, v );
        table.clear();

        WeakReference<String> ref = new WeakReference<>(v);
        v = null;

        JUnitMosaic.spinUntilReleased( "value was not garbage collected", ref );
    }

}
