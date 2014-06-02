package com.mosaic.columnstore.columns;

import com.mosaic.collections.IntList;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.IntColumn;
import com.mosaic.io.codecs.IntCodec;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;

import java.util.BitSet;


/**
* An IntColumn backed by an int array.
*/
public class IntColumnArray implements IntColumn {

    private final String   columnName;
    private final IntList  list       = new IntList();
    private final BitSet   isSet      = new BitSet();
    private final IntCodec codec;


    public IntColumnArray( String columnName ) {
        this( columnName, IntCodec.INT_CODEC );
    }

    public IntColumnArray( String columnName, IntCodec codec ) {
        this.columnName = columnName;
        this.codec      = codec;
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

    public CellExplanation explain( long row ) {
        if ( isSet(row) ) {
            return new CellExplanation( getFormattedValue(row) );
        } else {
            return null;
        }
    }

    public IntCodec getCodec() {
        return codec;
    }

    private String getFormattedValue( long row ) {
        int r = Backdoor.safeDowncast( row );
        int v = list.get(r);

        UTF8Builder buf = new UTF8Builder();

        getCodec().encode( v, buf );

        return buf.toString();
    }

}
