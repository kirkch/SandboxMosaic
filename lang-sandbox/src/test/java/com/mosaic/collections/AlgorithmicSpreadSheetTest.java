package com.mosaic.collections;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 */
public class AlgorithmicSpreadSheetTest {

    @Test
    public void givenEmptySpreadSheet_fetchCell_expectNull() {
        AlgorithmicSpreadSheet table = new AlgorithmicSpreadSheet();

        assertNull( table.get(0, 0) );
    }

    @Test
    public void givenEmptySpreadSheet_setCellValue_fetchCellExpectValue() {
        AlgorithmicSpreadSheet table = new AlgorithmicSpreadSheet();

        table.setCell( 0, 0, 101 );
        assertEquals( 101, table.get( 0, 0 ) );
    }

    @Test
    public void givenEmptySpreadSheet_setCellValue_fetchADifferentCellExpectNull() {
        AlgorithmicSpreadSheet table = new AlgorithmicSpreadSheet();

        table.setCell( 0, 0, 101 );
        assertNull( table.get( 1, 0 ) );
    }

    @Test
    public void givenEmptySpreadSheet_setColumnFunction_fetchCellExpectValueReturnedByFunction() {
        AlgorithmicSpreadSheet table = new AlgorithmicSpreadSheet();

        table.setColumnCalculation( 1, new AlgorithmicSpreadSheet.CellCalculation<Integer>() {
            public Integer calculateValue( AlgorithmicSpreadSheet spreadSheet, int row, int col ) {
                return row*col;
            }
        } );

        assertEquals( null, table.get(1, 0) );
        assertEquals( 0, table.get(0, 1) );
        assertEquals( 1, table.get(1, 1) );
        assertEquals( 2, table.get(2, 1) );
    }


    // todo optimise by cacheing calculated values, left out initially as cacheing involves
    // working through cache invalidation
}
