package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.io.codecs.LongCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;

import java.util.Arrays;


/**
* An LongColumn backed by an int array.
*/
public class LongColumnArray extends BaseLongColumn {

    private final String           columnName;
    private final String           description;

    private final LongCodec        codec;

    private long[]    cells;
    private boolean[] isSet;

    public LongColumnArray( String columnName, String description, long size ) {
        this( columnName, description, size, LongCodec.LONG2DP_CODEC );
    }

    public LongColumnArray( String columnName, String description, long size, LongCodec codec ) {
        QA.isInt( size, "size" );

        this.columnName  = columnName;
        this.description = description;
        this.codec       = codec;

        this.cells = new long[(int) size];
        this.isSet = new boolean[(int) size];
    }


    public String getColumnName() {
        return columnName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSet( long row ) {
        QA.isInt( row, "row" );

        return isSet.length > row && isSet[ (int) row ];
    }

    public long get( long row ) {
        QA.isInt( row, "row" );

        return cells[(int) row];
    }

    public void set( long row, long value ) {
        QA.isInt( row, "row" );

        int i = (int) row;

        isSet[i] = true;
        cells[i] = value;
    }

    public void unset( long row ) {
        QA.isInt( row, "row" );

        int i = (int) row;

        isSet[i] = false;
        cells[i] = 0L;
    }

    public long size() {
        return cells.length;
    }

    public void resizeIfNecessary( long newSize ) {
        if ( size() < newSize ) {
            QA.isInt( newSize, "newSize" );

            this.cells = Arrays.copyOf( cells, (int) newSize );
            this.isSet = Arrays.copyOf( isSet, (int) newSize );
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
            long v = get(row);

            getCodec().encode( v, out );
        }
    }

    public LongCodec getCodec() {
        return codec;
    }

    private String getFormattedValue( long row ) {
        int  r = Backdoor.safeDowncast( row );
        long v = cells[r];

        UTF8Builder buf = new UTF8Builder();

        getCodec().encode( v, buf );

        return buf.toString();
    }

}
