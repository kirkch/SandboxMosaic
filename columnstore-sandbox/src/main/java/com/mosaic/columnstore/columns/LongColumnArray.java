package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongList;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.CellExplanations;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.LongFunction1;

import java.util.BitSet;


/**
 * An LongColumn backed by an int array.
 */
public class LongColumnArray implements LongColumn {

    private final String                columnName;
    private final LongList              list       = new LongList();
    private final BitSet                isSet      = new BitSet();
    private final LongFunction1<String> formatter;

    public LongColumnArray( String columnName ) {
        this( columnName, LongFunction1.DEFAULT_LONG_FORMATTER );
    }

    public LongColumnArray( String columnName, LongFunction1<String> formatter ) {
        this.columnName = columnName;
        this.formatter  = formatter;
    }


    public String getColumnName() {
        return columnName;
    }

    public boolean isSet( long row ) {
        QA.isInt( row, "row" );

        return isSet.get( (int) row );
    }

    public long get( long row ) {
        QA.isInt( row, "row" );

        return list.get((int) row);
    }

    public void set( long row, long value ) {
        QA.isInt( row, "row" );

        int i = (int) row;

        isSet.set( i );
        list.set( i, value );
    }

    public long rowCount() {
        return list.size();
    }

    public CellExplanation<Long> explain( long row ) {
        QA.isInt( row, "row" );

        int i = (int) row;

        if ( isSet(i) ) {
            long v = list.get(i);

            return CellExplanations.cellValue(columnName, row, v, formatter.toFunction1() );
        } else {
            return null;
        }
    }

}
