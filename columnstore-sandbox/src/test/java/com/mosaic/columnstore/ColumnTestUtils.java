package com.mosaic.columnstore;

import com.mosaic.collections.LongIterator;
import com.mosaic.collections.LongSet;
import org.junit.ComparisonFailure;

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

        if ( expectedRowIds.keySet().size() != referencedCells.size() ) {
            throw new ComparisonFailure( "accessed cells do not match", expectedRowIds.toString(), referencedCells.toString() );
        }

        for ( String columnName : expectedRowIds.keySet() ) {
            LongSet actualRowIds = referencedCells.get(columnName);
            LongSet expected     = expectedRowIds.get( columnName );

            assertEquals( expected.size(), actualRowIds.size() );

            LongIterator it = expected.iterator();
            while ( it.hasNext() ) {
                long nextExpectedValue = it.next();

                assertTrue( nextExpectedValue+" is missing from: "+actualRowIds, actualRowIds.contains( nextExpectedValue ) );
            }
        }
    }

}
