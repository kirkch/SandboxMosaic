package com.mosaic.columnstore.columns;

import com.mosaic.collections.DynamicList;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.CellExplanations;
import com.mosaic.columnstore.Column;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.Function1;


/**
 * A basic column.  Stores an object each row.  All data is stored in a fairly traditional and
 * simple way.  Uses an array under the hood, so only supports 2^31 rows.
 */
public class ColumnOnHeap<T> implements Column<T> {

    private final String               columnName;
    private final DynamicList<T>       list       = new DynamicList<>();
    private final Function1<T, String> formatter;

    public ColumnOnHeap( String columnName ) {
        this( columnName, CellExplanations.<T>defaultFormatter() );
    }

    public ColumnOnHeap( String columnName, Function1<T, String> formatter ) {
        this.columnName = columnName;
        this.formatter  = formatter;
    }


    public String getColumnName() {
        return columnName;
    }

    public boolean isSet( long row ) {
        QA.isInt( row, "row" );

        return list.get((int) row) != null;
    }

    public T get( long row ) {
        QA.isInt( row, "row" );

        return list.get((int) row);
    }

    public void set( long row, T value ) {
        QA.isInt( row, "row" );

        list.set( (int) row, value );
    }

    public long rowCount() {
        return list.size();
    }

    public CellExplanation<T> explain( long row ) {
        QA.isInt( row, "row" );

        T v = list.get( (int) row );
        return v == null ? null : CellExplanations.cellValue(columnName, row, v, formatter);
    }

}
