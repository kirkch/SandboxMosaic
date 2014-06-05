package com.mosaic.columnstore.columns;

import com.mosaic.collections.DynamicArrayObject;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.ObjectColumn;
import com.mosaic.io.codecs.ObjectCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;


/**
* A basic column.  Stores an object each row.  All data is stored in a fairly traditional and
* simple way.  Uses an array under the hood, so only supports 2^31 rows.
*/
@SuppressWarnings("unchecked")
public class ObjectColumnArray<T> implements ObjectColumn<T> {

    private final String                columnName;
    private final String                description;
    private final DynamicArrayObject<T> list       = new DynamicArrayObject<>();
    private final ObjectCodec<T>        codec;

    public ObjectColumnArray( String columnName, String description ) {
        this( columnName, description, ObjectCodec.TOSTRING_FORMATTING_CODEC );
    }

    public ObjectColumnArray( String columnName, String description, ObjectCodec<T> codec ) {
        this.columnName  = columnName;
        this.description = description;
        this.codec       = codec;
    }


    public String getColumnName() {
        return columnName;
    }

    public String getDescription() {
        return description;
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

    public void writeValueTo( CharacterStream out, long row ) {
        if ( isSet(row) ) {
            T v = get(row);

            getCodec().encode( v, out );
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
