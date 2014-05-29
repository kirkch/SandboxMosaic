package com.mosaic.columnstore;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


/**
 *
 */
@SuppressWarnings({"PrimitiveArrayArgumentToVariableArgMethod", "unchecked", "AssertEqualsBetweenInconvertibleTypes", "UnnecessaryUnboxing"})
public class CellExplanationTest {

    @Test
    public void givenCellWithValue() {
        CellExplanation<String> explanation = CellExplanations.cellValue( "colName", 2, "value" );

        assertEquals( "value", explanation.toString() );
        assertEquals( "colName", explanation.getColumnNameNbl() );
        assertEquals( "value", explanation.getValue() );
        assertTrue( Arrays.equals(new long[] {2}, explanation.getRowIds()) );
        assertNull( explanation.getOperandsNbl() );
        assertNull( explanation.getDescriptiveNameNbl() );
    }

    @Test
    public void givenCellWithValueAndDescriptiveName() {
        CellExplanation<String> explanation = CellExplanations.cellValue( "colName", 2, "value","desc" );

        assertEquals( "value:desc", explanation.toString() );
        assertEquals( "colName", explanation.getColumnNameNbl() );
        assertEquals( "value", explanation.getValue() );
        assertTrue( Arrays.equals(new long[] {2}, explanation.getRowIds()) );
        assertNull( explanation.getOperandsNbl() );
        assertEquals( "desc", explanation.getDescriptiveNameNbl() );
    }

    @Test
    public void givenFormulaConstant() {
        CellExplanation<Double> explanation = CellExplanations.constant( 3.14, "PI" );

        assertEquals( "3.14:PI", explanation.toString() );
        assertNull( explanation.getColumnNameNbl() );
        assertEquals( 3.14, explanation.getValue().doubleValue(), 1e-5 );
        assertTrue( Arrays.equals(new long[] {}, explanation.getRowIds()) );
        assertNull( explanation.getOperandsNbl() );
        assertEquals( "PI", explanation.getDescriptiveNameNbl() );
    }

    @Test
    public void givenMultiplyValues_toString_expectSum() {
        CellExplanation<Double> explanation = CellExplanations.formula(
            3.14*72,
            "multiply",
            CellExplanations.constant(3.14,"Pi"),
            CellExplanations.cellValue("columnName",2,72,"radius")
        );

        assertEquals( "multiply(3.14:Pi,72:radius)", explanation.toString() );
        assertNull( explanation.getColumnNameNbl() );
        assertEquals( 3.14*72, explanation.getValue().doubleValue(), 1e-5 );
        assertTrue( Arrays.equals(new long[] {}, explanation.getRowIds()) );
        assertEquals( 2, explanation.getOperandsNbl().size() );
        assertNull( explanation.getDescriptiveNameNbl() );
    }

}
