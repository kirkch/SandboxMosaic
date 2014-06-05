package com.mosaic.columnstore.columns;

import com.mosaic.collections.FloatList;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.FloatColumn;
import com.mosaic.io.codecs.FloatCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;

import java.util.BitSet;


/**
 *
 */
public class FloatColumnArray implements FloatColumn {

    private final String     columnName;
    private final String     description;

    private final FloatList  list       = new FloatList();
    private final BitSet     isSet      = new BitSet();
    private final FloatCodec codec;


    public FloatColumnArray( String columnName, String description ) {
        this( columnName, description, FloatCodec.FLOAT2DP_CODEC );
    }

    public FloatColumnArray( String columnName, String description, FloatCodec codec ) {
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

    public void unset( long row ) {
        QA.isInt( row, "row" );

        int i = (int) row;

        isSet.set( i, false );
        list.set( i, 0.0f );
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
            float v = get(row);

            getCodec().encode( v, out );
        }
    }

    public FloatCodec getCodec() {
        return codec;
    }

    private String getFormattedValue( long row ) {
        int   i = Backdoor.safeDowncast(row);
        float v = list.get(i);

        UTF8Builder buf = new UTF8Builder();

        getCodec().encode( v, buf );

        return buf.toString();
    }

}
