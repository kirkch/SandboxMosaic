package com.mosaic.columnstore.columns;

import com.mosaic.collections.IntList;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.CellExplanations;
import com.mosaic.columnstore.IntColumn;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.IntFunction1;

import java.util.BitSet;


/**
 * An IntColumn backed by an int array.
 */
public class IntColumnArray implements IntColumn {

    private final String               columnName;
    private final IntList              list       = new IntList();
    private final BitSet               isSet      = new BitSet();
    private final IntFunction1<String> formatter;

    public IntColumnArray( String columnName ) {
        this( columnName, IntFunction1.DEFAULT_INT_FORMATTER );
    }

    public IntColumnArray( String columnName, IntFunction1<String> formatter ) {
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

    public int get( long row ) {
        QA.isInt( row, "row" );

        return list.get((int) row);
    }

    public void set( long row, int value ) {
        QA.isInt( row, "row" );

        int i = (int) row;

        isSet.set( i );
        list.set( i, value );
    }

    public long rowCount() {
        return list.size();
    }

    public CellExplanation<Integer> explain( long row ) {
        QA.isInt( row, "row" );

        int i = (int) row;

        if ( isSet(i) ) {
            int v = list.get(i);

            return CellExplanations.cellValue(columnName, row, v, formatter.toFunction1() );
        } else {
            return null;
        }
    }

}
