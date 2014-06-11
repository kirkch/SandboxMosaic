package com.mosaic.columnstore;

import com.mosaic.collections.LongIterator;
import com.mosaic.collections.LongSet;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 *
 */
public class ColumnTestUtils {

    public static void assertReferencedCellsEquals( CellExplanation explanation, String columnName, long...expectedRowIds ) {
        Map<String,LongSet> referencedCells = explanation.getReferencedCells();

        assertEquals( 1, referencedCells.size() );

        LongSet actualRowIds = referencedCells.get(columnName);
        assertEquals( expectedRowIds.length, actualRowIds.size() );

        for ( long expectedRowId : expectedRowIds ) {
            assertTrue( actualRowIds.contains(expectedRowId) );
        }
    }

    public static void assertReferencedCellsEquals( CellExplanation explanation, Map<String,LongSet> expectedRowIds ) {
        Map<String,LongSet> referencedCells = explanation.getReferencedCells();

        assertEquals( expectedRowIds.keySet().size(), referencedCells.size() );

        for ( String columnName : expectedRowIds.keySet() ) {
            LongSet actualRowIds = referencedCells.get(columnName);
            LongSet expected     = expectedRowIds.get( columnName );

            assertEquals( expected.size(), actualRowIds.size() );

            LongIterator it = expected.iterator();
            while ( it.hasNext() ) {
                assertTrue( actualRowIds.contains(it.next()) );
            }
        }
    }

}
