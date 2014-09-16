package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.io.codecs.FloatCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;

import java.util.Arrays;


/**
 *
 */
public class FloatColumnArray extends BaseFloatColumn {

    private final SystemX    system;
    private final String     columnName;
    private final String     description;

    private final FloatCodec codec;

    private float[]   cells;
    private boolean[] isSet;


    public FloatColumnArray( SystemX system, String columnName, String description, int size ) {
        this( system, columnName, description, size, FloatCodec.FLOAT2DP_CODEC );
    }

    public FloatColumnArray( SystemX system, String columnName, String description, int size, FloatCodec codec ) {
        this.system      = system;

        this.columnName  = columnName;
        this.description = description;
        this.codec       = codec;

        this.cells = new float[size];
        this.isSet = new boolean[size];
    }


    public String getColumnName() {
        return columnName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSet( long row ) {
        QA.isInt( row, "row" );

        return isSet.length > row && isSet[(int) row];
    }

    public float get( long row ) {
        QA.isInt( row, "row" );

        return cells[(int) row];
    }

    public void set( long row, float value ) {
        QA.isInt( row, "row" );

        int i = (int) row;

        isSet[i] = true;
        cells[i] = value;
    }

    public void unset( long row ) {
        QA.isInt( row, "row" );

        int i = (int) row;

        isSet[i] = false;
        cells[i] = 0.0f;
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
            float v = get(row);

            getCodec().encode( v, out );
        }
    }

    public FloatCodec getCodec() {
        return codec;
    }

    private String getFormattedValue( long row ) {
        int   i = Backdoor.safeDowncast(row);
        float v = cells[i];

        UTF8Builder buf = new UTF8Builder(system);

        getCodec().encode( v, buf );

        return buf.toString();
    }

}
