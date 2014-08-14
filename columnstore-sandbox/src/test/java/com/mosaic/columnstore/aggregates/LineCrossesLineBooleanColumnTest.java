package com.mosaic.columnstore.aggregates;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.BooleanColumn;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.Columns;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.columnstore.columns.LongColumnArray;
import com.mosaic.utils.MapUtils;
import org.junit.Test;

import static com.mosaic.columnstore.ColumnTestUtils.assertReferencedCellsEquals;
import static org.junit.Assert.*;


/**
 *
 */
public class LineCrossesLineBooleanColumnTest {


    @Test
    public void givenEmptySourceColumns_expectIsSetToReturnFalse() {
        LongColumn    a      = new LongColumnArray("a","desc", 3);
        LongColumn    b      = new LongColumnArray("b","desc", 3);
        BooleanColumn signal = new LineCrossesLineBooleanColumn( "signal", "desc", "a^b", a, b );

        assertEquals( 3, signal.size() );

        assertFalse( signal.isSet(0) );
        assertFalse( signal.isSet(1) );
        assertFalse( signal.isSet(2) );

        CellExplanation explanation = signal.explain(1);

        assertEquals( "false", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, MapUtils.<String,LongSet>asMap("a", LongSet.createLongSet(1), "b", LongSet.createLongSet(1)) );
        assertEquals( "a^b(a[1], b[1])", explanation.toString() );
    }

    @Test
    public void givenTwoLinesThatDoNotCross() {
        LongColumn    a      = Columns.newLongColumn( "a", "desc", 1, 1, 1 );
        LongColumn    b      = Columns.newLongColumn("b","desc", 2, 2, 2);
        BooleanColumn signal = new LineCrossesLineBooleanColumn( "signal", "desc", "a^b", a, b );

        assertEquals( 3, signal.size() );

        assertFalse( signal.isSet( 0 ) );
        assertTrue( signal.isSet( 1 ) );
        assertTrue( signal.isSet( 2 ) );

        assertFalse( signal.get(1) );
        assertFalse( signal.get(2) );

        CellExplanation explanation = signal.explain(1);

        assertEquals( "false", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, MapUtils.<String,LongSet>asMap("a", LongSet.createLongSet(0,1), "b", LongSet.createLongSet(0,1)) );
        assertEquals( "a^b(a[0,1], b[0,1])", explanation.toString() );
    }

    @Test
    public void givenTwoLinesThatDoCross() {
        LongColumn    a      = Columns.newLongColumn( "a", "desc", 1, 2, 1 );
        LongColumn    b      = Columns.newLongColumn("b","desc", 2, 1, 2);
        BooleanColumn signal = new LineCrossesLineBooleanColumn( "signal", "desc", "a^b", a, b );

        assertEquals( 3, signal.size() );

        assertFalse( signal.isSet( 0 ) );
        assertTrue( signal.isSet( 1 ) );
        assertTrue( signal.isSet( 2 ) );

        assertTrue( signal.get(1) );
        assertFalse( signal.get(2) );

        CellExplanation explanation = signal.explain(1);

        assertEquals( "true", explanation.getFormattedValue() );
        assertReferencedCellsEquals( explanation, MapUtils.<String,LongSet>asMap("a", LongSet.createLongSet(0,1), "b", LongSet.createLongSet(0,1)) );
        assertEquals( "a^b(a[0,1], b[0,1])", explanation.toString() );
    }


    @Test
    public void givenLinesThatCrossANDLineAHasAGap() {
        LongColumn    a      = new LongColumnArray("a","desc", 10);
        LongColumn    b      = new LongColumnArray("b","desc", 10);
        BooleanColumn signal = new LineCrossesLineBooleanColumn( "signal", "desc", "a^b", a, b );

        a.set( 0, 1 );
        a.set( 2, 3 );
        a.set( 3, 4 );
        a.set( 4, 5 );


        b.set( 0, 2 );
        b.set( 1, 2 );
        b.set( 2, 2 );
        b.set( 3, 2 );
        b.set( 4, 2 );


        assertEquals( 10, signal.size() );

        assertFalse( signal.isSet(0) );
        assertFalse( signal.isSet(1) );
        assertTrue( signal.isSet(2) );
        assertTrue( signal.isSet(3) );
        assertTrue( signal.isSet(4) );

        assertTrue( signal.get(2) );
        assertFalse( signal.get(3) );
        assertFalse( signal.get(4) );

        CellExplanation explanation = signal.explain(2);

        assertEquals( "true", explanation.getFormattedValue() );
        assertEquals( "a^b(a[0,2], b[1,2])", explanation.toString() );
        assertReferencedCellsEquals( explanation, MapUtils.<String,LongSet>asMap("a", LongSet.createLongSet(0,2), "b", LongSet.createLongSet(1,2)) );
    }
}
