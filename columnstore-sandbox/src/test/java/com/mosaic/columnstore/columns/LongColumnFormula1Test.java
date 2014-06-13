package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.LongColumn;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class LongColumnFormula1Test {

    @Test
    public void testPrePopulateColumn() {
        int rowCount = 1000000;

        LongColumn columnA      = createColumn("A", rowCount);
        LongColumn columnB      = createFormulaColumn( "B", columnA );
        LongColumn columnBCache = new LongColumnArray( "BCache", "cache of B", 10 );

        columnB.prePopulateColumn( columnBCache );


        assertEquals( rowCount, columnBCache.size() );

        for ( int i=0; i<rowCount; i++ ) {
            assertEquals( "row " + i, i*2, columnBCache.get(i) );
        }
    }

    static LongColumn createColumn( String columnName, int rowCount ) {
        LongColumn col = new LongColumnArray(columnName, "desc", rowCount);

        for ( int i=0; i<rowCount; i++ ) {
            col.set(i, i);
        }

        return col;
    }

    static LongColumn createFormulaColumn( String columnName, LongColumn sourceColumn ) {
        return new LongColumnFormula1(columnName,sourceColumn.getColumnName()+"*2","DOUBLE", sourceColumn) {
            protected long get( long row, LongColumn col ) {
                return col.get(row)*2;
            }
        };
    }

}
