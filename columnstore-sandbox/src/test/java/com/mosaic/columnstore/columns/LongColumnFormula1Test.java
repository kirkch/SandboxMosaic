package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.LongColumn;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class LongColumnFormula1Test {

    private SystemX system = new DebugSystem();


    @Test
    public void testPrePopulateColumn() {
        int rowCount = 1000000;

        LongColumn columnA      = createColumn(system, "A", rowCount);
        LongColumn columnB      = createFormulaColumn( system, "B", columnA );
        LongColumn columnBCache = new LongColumnArray( system, "BCache", "cache of B", 10 );

        columnB.prePopulateColumn( columnBCache );


        assertEquals( rowCount, columnBCache.size() );

        for ( int i=0; i<rowCount; i++ ) {
            assertEquals( "row " + i, i*2, columnBCache.get(i) );
        }
    }

    static LongColumn createColumn( SystemX system, String columnName, int rowCount ) {
        LongColumn col = new LongColumnArray(system, columnName, "desc", rowCount);

        for ( int i=0; i<rowCount; i++ ) {
            col.set(i, i);
        }

        return col;
    }

    static LongColumn createFormulaColumn( SystemX system, String columnName, LongColumn sourceColumn ) {
        return new LongColumnFormula1(system, columnName,sourceColumn.getColumnName()+"*2","DOUBLE", sourceColumn) {
            protected long get( long row, LongColumn col ) {
                return col.get(row)*2;
            }
        };
    }

}
