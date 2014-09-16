package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.BooleanColumn;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.ColumnTestUtils;
import com.mosaic.columnstore.Columns;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.MapUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class AndBooleanColumnTest {

    private SystemX system = new DebugSystem();


    @Test
    public void givenFF_expectF() {
        BooleanColumn col1 = Columns.newBooleanColumn( system, "col1", "d", false, false, false );
        BooleanColumn col2 = Columns.newBooleanColumn( system, "col2", "d", false, false, false );

        BooleanColumn andColumn = new AndBooleanColumn( system, col1, col2 );

        assertEquals( true, andColumn.isSet(1) );
        assertEquals( false, andColumn.get(1) );


        ColumnTestUtils.assertReferencedCellsEquals( andColumn.explain( 1 ),
            MapUtils.<String, LongSet>asMap(
                "col1", LongSet.createLongSet(1),
                "col2", LongSet.createLongSet()
            )
        );
    }

    @Test
    public void givenTF_expectF() {
        BooleanColumn col1 = Columns.newBooleanColumn( system, "col1", "d", false, true, false );
        BooleanColumn col2 = Columns.newBooleanColumn( system, "col2", "d", false, false, false );

        BooleanColumn andColumn = new AndBooleanColumn( system, col1, col2 );

        assertEquals( true, andColumn.isSet(1) );
        assertEquals( false, andColumn.get(1) );


        ColumnTestUtils.assertReferencedCellsEquals( andColumn.explain( 1 ),
            MapUtils.<String, LongSet>asMap(
                "col1", LongSet.createLongSet(1),
                "col2", LongSet.createLongSet(1)
            )
        );
    }

    @Test
    public void givenFT_expectF() {
        BooleanColumn col1 = Columns.newBooleanColumn( system, "col1", "d", false, false, false );
        BooleanColumn col2 = Columns.newBooleanColumn( system, "col2", "d", false, true, false );

        BooleanColumn andColumn = new AndBooleanColumn( system, col1, col2 );

        assertEquals( true, andColumn.isSet(1) );
        assertEquals( false, andColumn.get(1) );


        ColumnTestUtils.assertReferencedCellsEquals( andColumn.explain( 1 ),
            MapUtils.<String, LongSet>asMap(
                "col1", LongSet.createLongSet(1),
                "col2", LongSet.createLongSet()
            )
        );
    }

    @Test
    public void givenTT_expectT() {
        BooleanColumn col1 = Columns.newBooleanColumn( system, "col1", "d", false, true, false );
        BooleanColumn col2 = Columns.newBooleanColumn( system, "col2", "d", false, true, false );

        BooleanColumn andColumn = new AndBooleanColumn( system, col1, col2 );

        assertEquals( true, andColumn.isSet(1) );
        assertEquals( true, andColumn.get(1) );


        ColumnTestUtils.assertReferencedCellsEquals( andColumn.explain( 1 ),
            MapUtils.<String, LongSet>asMap(
                "col1", LongSet.createLongSet(1),
                "col2", LongSet.createLongSet(1)
            )
        );
    }

}
