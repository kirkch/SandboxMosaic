package com.mosaic.columnstore.columns;

import com.mosaic.collections.DynamicArrayLong;
import com.mosaic.columnstore.CellExplanation;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.io.codecs.LongCodec;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.io.streams.UTF8Builder;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;

import java.util.BitSet;


/**
* An LongColumn backed by an int array.
*/
public class LongColumnArray implements LongColumn {

    private final String           columnName;
    private final String           description;

    private final DynamicArrayLong list       = new DynamicArrayLong();
    private final BitSet           isSet      = new BitSet();
    private final LongCodec        codec;

    public LongColumnArray( String columnName, String description ) {
        this( columnName, description, LongCodec.LONG2DP_CODEC );
    }

    public LongColumnArray( String columnName, String description, LongCodec codec ) {
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

    public long get( long row ) {
        QA.isInt( row, "row" );

        return list.get((int) row);
    }

    public void set( long row, long value ) {
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
        long v = list.get(r);

        UTF8Builder buf = new UTF8Builder();

        getCodec().encode( v, buf );

        return buf.toString();
    }

}
