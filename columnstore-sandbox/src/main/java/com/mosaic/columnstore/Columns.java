package com.mosaic.columnstore;

import com.mosaic.collections.concurrent.ForkJoinTask;
import com.mosaic.columnstore.columns.BooleanColumnArray;
import com.mosaic.columnstore.columns.FloatColumnArray;
import com.mosaic.columnstore.columns.IntColumnArray;
import com.mosaic.columnstore.columns.LongColumnArray;
import com.mosaic.columnstore.columns.ObjectColumnArray;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.Factory;
import com.mosaic.lang.QA;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Long2BooleanFunction;
import com.mosaic.lang.text.UTF8;

import java.util.Iterator;


/**
 *
 */
@SuppressWarnings("unchecked")
public class Columns<T extends Column> implements Iterable<T> {

    public static BooleanColumn newBooleanColumn( String columnName, String description, boolean...values ) {
        BooleanColumn col = new BooleanColumnArray( columnName, description, values.length );

        for ( int i=0; i<values.length; i++ ) {
            col.set(i, values[i]);
        }

        return col;
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







    private static final UTF8 SEPARATOR = new UTF8( ", " );


    private T[] columns;

    public Columns( T...columns ) {
        this.columns = columns;
    }

    public T getColumn( int index ) {
        return columns[index];
    }

    public int numColumns() {
        return columns.length;
    }

    public int indexOf( String targetColumnName ) {
        for ( int i=0; i<columns.length; i++ ) {
            T col = columns[i];

            if ( col.getColumnName().equals(targetColumnName) ) {
                return i;
            }
        }

        return -1;
    }

    public void writeAsCSVTo( CharacterStream out ) {
        writeCSVHeaderTo( out );

        for ( long row=0; row<rowCount(); row++ ) {
            if ( !isBlankRow(row) ) {
                writeRowAsCSVTo( out, row );
            }
        }
    }

    public void writeAsCSVTo( CharacterStream out, Long2BooleanFunction includeRow ) {
        writeCSVHeaderTo( out );

        for ( long row=0; row<rowCount(); row++ ) {
            if ( !isBlankRow(row) && includeRow.invoke(row) ) {
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

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;

            public boolean hasNext() {
                return index < columns.length;
            }

            public T next() {
                return columns[index++];
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    // NB so far I have found that going parallel here has made next to no difference..  left the
    // code here to make future comparisons easier...  to be reviewed.
    // because we only had three columns at the time?
    public Columns<T> prePopulateColumns( final Function1<T,T> cacheColumnFactory ) {
//        final T[] cachedColumns = columns.clone();
//
//        ForkJoinTask job = new ForkJoinTask(0, columns.length) {
//            protected void doJob( long index ) {
//                QA.isInt( index, "index" );
//
//                int i = (int) index;
//
//                T cache = cacheColumnFactory.invoke( columns[i] );
//
//                cachedColumns[i] = cache;
//
//                columns[i].prePopulateColumn( cache );
//            }
//        };
//
//        job.execute();



        T[] cachedColumns = columns.clone();

        for ( int i=0; i<columns.length; i++ ) {
            T cache = cacheColumnFactory.invoke( columns[i] );

            cachedColumns[i] = cache;

            columns[i].prePopulateColumn( cache );
        }

        return new Columns(cachedColumns);
    }

}
