package com.mosaic.columnstore.aggregates;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.Columns;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.MapUtils;
import org.junit.Test;

import static com.mosaic.columnstore.ColumnTestUtils.assertReferencedCellsEquals;
import static org.junit.Assert.assertEquals;


/**
 *
 */
public class AverageGapLongColumnTest {

    private SystemX system = new DebugSystem();


    @Test
    public void averageThreeRows() {
        LongColumn high = Columns.newLongColumn(system, "high", "", 10, 12, 13, 12, 9 );
        LongColumn low  = Columns.newLongColumn(system, "low",  "",  7, 10, 10, 19, 5 );

        AverageGapLongColumn stdDevCol = new AverageGapLongColumn( system, "avg daily range", high, low, 3 );


        CellExplanation explanation = stdDevCol.explain( 3 );

        long expectedAvg = ((19-12) + (13-10) + (12-10))/3;

        assertEquals( expectedAvg+"", explanation.getFormattedValue() );
        assertEquals( "avggap(col1_cells,col2_cells)", explanation.toString() );
        assertReferencedCellsEquals(
            explanation,
            MapUtils.<String,LongSet>asMap(
                "high", LongSet.createLongSet(1,2,3),
                "low", LongSet.createLongSet(1,2,3)
            )
        );
    }

    @Test
    public void averageTwoRows() {
        LongColumn high = Columns.newLongColumn(system, "high", "", 10, 12, 13, 12, 9 );
        LongColumn low  = Columns.newLongColumn(system, "low",  "",  7, 10, 10, 19, 5 );

        AverageGapLongColumn stdDevCol = new AverageGapLongColumn( system, "avg daily range", high, low, 2 );


        CellExplanation explanation = stdDevCol.explain( 3 );

        long expectedAvg = ((19-12) + (13-10))/2;

        assertEquals( expectedAvg+"", explanation.getFormattedValue() );
        assertEquals( "avggap(col1_cells,col2_cells)", explanation.toString() );
        assertReferencedCellsEquals(
            explanation,
            MapUtils.<String,LongSet>asMap(
                "high", LongSet.createLongSet(2,3),
                "low", LongSet.createLongSet(2,3)
            )
        );
    }

    @Test
    public void averageThreeRowsWhenOnlyOneIsAvailable() {
        LongColumn high = Columns.newLongColumn(system, "high", "", 10 );
        LongColumn low  = Columns.newLongColumn(system, "low",  "",  7 );

        AverageGapLongColumn stdDevCol = new AverageGapLongColumn( system, "avg daily range", high, low, 3 );


        CellExplanation explanation = stdDevCol.explain( 3 );

        assertEquals( "3", explanation.getFormattedValue() );
        assertEquals( "avggap(col1_cells,col2_cells)", explanation.toString() );
        assertReferencedCellsEquals(
            explanation,
            MapUtils.<String,LongSet>asMap(
                "high", LongSet.createLongSet(0),
                "low", LongSet.createLongSet(0)
            )
        );
    }

    @Test
    public void averageThreeRowsWhenNoneIsAvailable() {
        LongColumn high = Columns.newLongColumn(system, "high", "" );
        LongColumn low  = Columns.newLongColumn(system, "low",  "" );

        AverageGapLongColumn stdDevCol = new AverageGapLongColumn( system, "avg daily range", high, low, 3 );


        CellExplanation explanation = stdDevCol.explain( 3 );

        assertEquals( "0", explanation.getFormattedValue() );
        assertEquals( "avggap(col1_cells,col2_cells)", explanation.toString() );
        assertReferencedCellsEquals(
            explanation,
            MapUtils.<String,LongSet>asMap(
                "high", LongSet.createLongSet(),
                "low", LongSet.createLongSet()
            )
        );
    }

}
