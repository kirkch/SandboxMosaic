package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.io.codecs.ObjectCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;

import java.util.Arrays;


/**
* A basic column.  Stores an object each row.  All data is stored in a fairly traditional and
* simple way.  Uses an array under the hood, so only supports 2^31 rows.
*/
@SuppressWarnings("unchecked")
public class ObjectColumnArray<T> extends BaseObjectColumn<T> {

    private final String         columnName;
    private final String         description;
    private final ObjectCodec<T> codec;

    private T[] cells;


    public ObjectColumnArray( String columnName, String description, long size ) {
        this( columnName, description, size, ObjectCodec.TOSTRING_FORMATTING_CODEC );
    }

    public ObjectColumnArray( String columnName, String description, long size, ObjectCodec<T> codec ) {
        QA.isInt( size, "size" );

        this.columnName  = columnName;
        this.description = description;
        this.codec       = codec;

        this.cells       = (T[]) new Object[(int) size];
    }


    public String getColumnName() {
        return columnName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSet( long row ) {
        QA.isInt( row, "row" );

        return cells.length > row && cells[(int) row] != null;
    }

    public T get( long row ) {
        QA.isInt( row, "row" );

        return cells[(int) row];
    }

    public void set( long row, T value ) {
        QA.isInt( row, "row" );

        cells[(int) row] = value;
    }

    public void unset( long row ) {
        QA.isInt( row, "row" );

        int i = (int) row;

        cells[i] = null;
    }

    public long size() {
        return cells.length;
    }

    public int reserveWidth() {
        return getCodec().reserveWidth();
    }

    public void resizeIfNecessary( long newSize ) {
        if ( size() < newSize ) {
            QA.isInt( newSize, "newSize" );

            this.cells = Arrays.copyOf( cells, (int) newSize );
        }
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
        T   v = cells[r];

        UTF8Builder buf = new UTF8Builder();

        getCodec().encode( v, buf );

        return buf.toString();
    }

}
