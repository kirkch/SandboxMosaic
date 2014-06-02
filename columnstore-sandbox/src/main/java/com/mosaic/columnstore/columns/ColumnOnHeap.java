package com.mosaic.columnstore.columns;

import com.mosaic.collections.DynamicArrayObject;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.Column;
import com.mosaic.io.codecs.ObjectCodec;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;


/**
* A basic column.  Stores an object each row.  All data is stored in a fairly traditional and
* simple way.  Uses an array under the hood, so only supports 2^31 rows.
*/
@SuppressWarnings("unchecked")
public class ColumnOnHeap<T> implements Column<T> {

    private final String                columnName;
    private final DynamicArrayObject<T> list       = new DynamicArrayObject<>();
    private final ObjectCodec<T>        codec;

    public ColumnOnHeap( String columnName ) {
        this( columnName, ObjectCodec.TOSTRING_FORMATTING_CODEC );
    }

    public ColumnOnHeap( String columnName, ObjectCodec<T> codec ) {
        this.columnName = columnName;
        this.codec      = codec;
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

    public CellExplanation explain( long row ) {
        if ( isSet(row) ) {
            return new CellExplanation( getFormattedValue(row) );
        } else {
            return null;
        }
    }

    public ObjectCodec<T> getCodec() {
        return codec;
    }

    private String getFormattedValue( long row ) {
        int r = Backdoor.safeDowncast( row );
        T   v = list.get(r);

        UTF8Builder buf = new UTF8Builder();

        getCodec().encode( v, buf );

        return buf.toString();
    }

}
