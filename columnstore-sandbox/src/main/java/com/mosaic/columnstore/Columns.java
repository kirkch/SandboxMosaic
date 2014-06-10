package com.mosaic.columnstore;

import com.mosaic.columnstore.columns.FloatColumnArray;
import com.mosaic.columnstore.columns.IntColumnArray;
import com.mosaic.columnstore.columns.LongColumnArray;
import com.mosaic.columnstore.columns.ObjectColumnArray;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.text.UTF8;


/**
 *
 */
public class Columns {

    private static final UTF8 SEPARATOR = new UTF8( ", " );


    private Column[] columns;

    public Columns( Column...columns ) {
        this.columns = columns;
    }

    public void writeAsCSVTo( CharacterStream out ) {
        writeCSVHeaderTo( out );

        for ( long row=0; row<rowCount(); row++ ) {
            if ( !isBlankRow(row) ) {
                writeRowAsCSVTo( out, row );
            }
        }
    }

    public boolean isBlankRow( long row ) {
        for ( Column col : columns ) {
            if ( col.isSet(row) ) {
                return false;
            }
        }

        return true;
    }

    private void writeCSVHeaderTo( CharacterStream out ) {
        out.writeString( "rowId" );

        for ( Column col : columns ) {
            out.writeUTF8( SEPARATOR );

            out.writeString( col.getColumnName() );
        }

        out.newLine();
    }

    public void writeRowAsCSVTo( CharacterStream out, long row ) {
        out.writeLong( row );

        for ( Column col : columns ) {
            out.writeUTF8( SEPARATOR );
            col.writeValueTo( out, row );
        }

        out.newLine();
    }

    public long rowCount() {
        long count = 0;

        for ( Column col : columns ) {
            count = Math.max(count,col.size());
        }

        return count;
    }

    public static IntColumn newIntColumn( String columnName, String description, int...values ) {
        IntColumn col = new IntColumnArray( columnName, description, values.length );

        for ( int i=0; i<values.length; i++ ) {
            col.set(i, values[i]);
        }

        return col;
    }

    public static LongColumn newLongColumn( String columnName, String description, long...values ) {
        LongColumn col = new LongColumnArray( columnName, description, values.length );

        for ( int i=0; i<values.length; i++ ) {
            col.set(i, values[i]);
        }

        return col;
    }

    public static FloatColumn newFloatColumn( String columnName, String description, float...values ) {
        FloatColumn col = new FloatColumnArray( columnName, description, values.length );

        for ( int i=0; i<values.length; i++ ) {
            col.set(i, values[i]);
        }

        return col;
    }

    public static <T> ObjectColumn<T> newObjectColumn( String columnName, String description, T...values ) {
        ObjectColumn<T> col = new ObjectColumnArray<>( columnName, description, values.length );

        for ( int i=0; i<values.length; i++ ) {
            col.set(i, values[i]);
        }

        return col;
    }
}
