package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.CellExplanation;
import com.mosaic.io.codecs.DoubleCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;

import java.util.Arrays;


/**
 *
 */
public class DoubleColumnArray extends BaseDoubleColumn {

    private final String     columnName;
    private final String     description;

    private final DoubleCodec codec;

    private double[]  cells;
    private boolean[] isSet;


    public DoubleColumnArray( String columnName, String description, int size ) {
        this( columnName, description, size, DoubleCodec.DOUBLE2DP_CODEC );
    }

    public DoubleColumnArray( String columnName, String description, int size, DoubleCodec codec ) {
        this.columnName  = columnName;
        this.description = description;
        this.codec       = codec;

        this.cells = new double[size];
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

    public double get( long row ) {
        QA.isInt( row, "row" );

        return cells[(int) row];
    }

    public void set( long row, double value ) {
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
            double v = get(row);

            getCodec().encode( v, out );
        }
    }

    public DoubleCodec getCodec() {
        return codec;
    }

    private String getFormattedValue( long row ) {
        int   i = Backdoor.safeDowncast( row );
        double v = cells[i];

        UTF8Builder buf = new UTF8Builder();

        getCodec().encode( v, buf );

        return buf.toString();
    }

}
