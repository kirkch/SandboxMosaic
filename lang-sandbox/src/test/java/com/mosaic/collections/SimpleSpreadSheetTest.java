package com.mosaic.collections;

import com.softwaremosaic.junit.JUnitMosaic;
import org.junit.Test;

import java.lang.ref.WeakReference;

import static org.junit.Assert.*;


/**
 *
 */
public class SimpleSpreadSheetTest {


// EMPTY TABLE

    @Test
    public void givenAnEmptyTable_callRowCount_expectZero() {
        SimpleSpreadSheet table = new SimpleSpreadSheet();
        
        assertEquals( 0, table.rowCount() );
    }

    @Test
    public void givenAnEmptyTable_callColumnCountForRowZero_expectZero() {
        SimpleSpreadSheet table = new SimpleSpreadSheet();
        
        assertEquals( 0, table.columnCount( 0 ) );
    }

    @Test
    public void givenAnEmptyTable_callColumnCountForRowOne_expectZero() {
        SimpleSpreadSheet table = new SimpleSpreadSheet();

        assertEquals( 0, table.columnCount( 1 ) );
    }

    @Test
    public void givenAnEmptyTable_callColumnCountForRowTwo_expectZero() {
        SimpleSpreadSheet table = new SimpleSpreadSheet();

        assertEquals( 0, table.columnCount( 2 ) );
    }

    @Test
    public void givenAnEmptyTable_getFirstCell_expectNull() {
        SimpleSpreadSheet table = new SimpleSpreadSheet();

        assertNull( table.get(0,0) );
    }


// SETTING COLUMN ON FIRST ROW

    @Test
    public void givenAnEmptyTable_setTheFirstCellOfTheFirstRow_expectRowCountOne() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        table.set( 0, 0, "foo" );

        assertEquals( 1, table.rowCount() );
    }

    @Test
    public void givenAnEmptyTable_setTheFirstCellOfTheFirstRow_retrieveViaGet() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        table.set( 0, 0, "foo" );

        assertEquals( "foo", table.get(0,0) );
    }

    @Test
    public void givenAnEmptyTable_setTheFirstCellOfTheFirstRow_expectColumnCountOfRowZeroToBeOne() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        table.set( 0, 0, "foo" );

        assertEquals( 1, table.columnCount( 0 ) );
    }

    @Test
    public void givenAnEmptyTable_setTheFirstCellOfTheFirstRow_expectColumnCountOfRowOneToBeZero() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        table.set( 0, 0, "foo" );

        assertEquals( 0, table.columnCount( 1 ) );
    }


// SETTING COLUMN ON FIRST ROW

    @Test
    public void givenAnEmptyTable_setTheSecondCellOfTheFirstRow_expectRowCountOne() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        table.set( 0, 1, "foo" );

        assertEquals( 1, table.rowCount() );
    }

    @Test
    public void givenAnEmptyTable_setTheSecondCellOfTheFirstRow_expectColumnCountOfRowZeroToBeTwo() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        table.set( 0, 1, "foo" );

        assertEquals( 2, table.columnCount( 0 ) );
    }

    @Test
    public void givenAnEmptyTable_setTheSecondCellOfTheFirstRow_expectColumnCountOfRowOneToBeZero() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        table.set( 0, 1, "foo" );

        assertEquals( 0, table.columnCount( 1 ) );
    }


// SETTING COLUMN ON SECOND ROW



    @Test
    public void givenAnEmptyTable_setTheSecondCellOfTheSecondRow_expectRowCountOfTwo() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        table.set( 1, 1, "foo" );

        assertEquals( 2, table.rowCount() );
    }

    @Test
    public void givenAnEmptyTable_setTheSecondCellOfTheSecondRow_expectColumnCountOfRowZeroToBeZero() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        table.set( 1, 1, "foo" );

        assertEquals( 0, table.columnCount(0) );
    }

    @Test
    public void givenAnEmptyTable_setTheSecondCellOfTheSecondRow_expectColumnCountOfRowOneToBeTwo() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        table.set( 1, 1, "foo" );

        assertEquals( 2, table.columnCount(1) );
    }


// CLEAR

    @Test
    public void givenANonEmptyTable_clear_expectRowCountToReturnToZero() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        table.set( 1, 1, "foo" );
        table.clear();

        assertEquals( 0, table.rowCount() );
    }

    @Test
    public void givenANonEmptyTable_clear_expectColumnCountToReturnToZero() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        table.set( 1, 1, "foo" );
        table.clear();

        assertEquals( 0, table.columnCount(0) );
        assertEquals( 0, table.columnCount(1) );
        assertEquals( 0, table.columnCount( 2 ) );
    }

    @Test
    @SuppressWarnings({"RedundantStringConstructorCall", "UnusedAssignment"})
    public void givenANonEmptyTable_clear_expectContentsToBecomeGCed() {
        SimpleSpreadSheet<String> table = new SimpleSpreadSheet<>();

        String v = new String("foo");
        table.set( 1, 1, v );
        table.clear();

        WeakReference<String> ref = new WeakReference<>(v);
        v = null;

        JUnitMosaic.spinUntilReleased( "value was not garbage collected", ref );
    }

}
