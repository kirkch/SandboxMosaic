package com.mosaic.columnstore.columns;

import com.mosaic.collections.FloatList;
import com.mosaic.collections.IntList;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.CellExplanations;
import com.mosaic.columnstore.FloatColumn;
import com.mosaic.columnstore.IntColumn;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.FloatFunction1;
import com.mosaic.lang.functional.IntFunction1;

import java.util.BitSet;


/**
 *
 */
public class FloatColumnArray implements FloatColumn {

    private final String                 columnName;
    private final FloatList              list       = new FloatList();
    private final BitSet                 isSet      = new BitSet();
    private final FloatFunction1<String> formatter;

    public FloatColumnArray( String columnName ) {
        this( columnName, FloatFunction1.DEFAULT_FLOAT_FORMATTER );
    }

    public FloatColumnArray( String columnName, FloatFunction1<String> formatter ) {
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

    public float get( long row ) {
        QA.isInt( row, "row" );

        return list.get((int) row);
    }

    public void set( long row, float value ) {
        QA.isInt( row, "row" );

        int i = (int) row;

        isSet.set( i );
        list.set( i, value );
    }

    public long rowCount() {
        return list.size();
    }

    public CellExplanation<Float> explain( long row ) {
        QA.isInt( row, "row" );

        int i = (int) row;

        if ( isSet(i) ) {
            float v = list.get(i);

            return CellExplanations.cellValue( columnName, row, v, formatter.toFunction1() );
        } else {
            return null;
        }
    }

}
