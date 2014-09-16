package com.mosaic.columnstore;

import com.mosaic.columnstore.columns.BooleanColumnArray;
import com.mosaic.columnstore.columns.BooleanColumnFormula1;
import com.mosaic.columnstore.columns.DoubleColumnArray;
import com.mosaic.columnstore.columns.FloatColumnArray;
import com.mosaic.columnstore.columns.IntColumnArray;
import com.mosaic.columnstore.columns.LongColumnArray;
import com.mosaic.columnstore.columns.ObjectColumnArray;
import com.mosaic.io.streams.CharacterStream;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Long2BooleanFunction;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.UTF8;
import com.mosaic.utils.ArrayUtils;
import com.mosaic.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 *
 */
@SuppressWarnings("unchecked")
public class Columns<T extends Column> implements Iterable<T> {

    public static BooleanColumn newBooleanColumn( SystemX system, String columnName, String description, boolean...values ) {
        BooleanColumn col = new BooleanColumnArray( system, columnName, description, values.length );

        for ( int i=0; i<values.length; i++ ) {
            col.set(i, values[i]);
        }

        return col;
    }

    public static IntColumn newIntColumn( SystemX system, String columnName, String description, int...values ) {
        IntColumn col = new IntColumnArray( system, columnName, description, values.length );

        for ( int i=0; i<values.length; i++ ) {
            col.set(i, values[i]);
        }

        return col;
    }

    public static LongColumn newLongColumn( SystemX system, String columnName, String description, long...values ) {
        LongColumn col = new LongColumnArray( system, columnName, description, values.length );

        for ( int i=0; i<values.length; i++ ) {
            col.set(i, values[i]);
        }

        return col;
    }

    public static FloatColumn newFloatColumn( SystemX system, String columnName, String description, float...values ) {
        FloatColumn col = new FloatColumnArray( system, columnName, description, values.length );

        for ( int i=0; i<values.length; i++ ) {
            col.set(i, values[i]);
        }

        return col;
    }

    public static DoubleColumn newDoubleColumn( SystemX system, String columnName, String description, double...values ) {
        DoubleColumn col = new DoubleColumnArray( system, columnName, description, values.length );

        for ( int i=0; i<values.length; i++ ) {
            col.set(i, values[i]);
        }

        return col;
    }

    public static <T> ObjectColumn<T> newObjectColumn( SystemX system, String columnName, String description, T...values ) {
        ObjectColumn<T> col = new ObjectColumnArray<>( system, columnName, description, values.length );

        for ( int i=0; i<values.length; i++ ) {
            col.set(i, values[i]);
        }

        return col;
    }



    public static <T extends Column> Columns createColumns( Class<T> type, List<T> columns ) {
        return new Columns( ListUtils.toArray(type, columns) );
    }

    public static <T extends Column> Columns createColumns( T[]...columns ) {
        return new Columns( ArrayUtils.flatten(columns) );
    }





    private static final UTF8 SEPARATOR = new UTF8( ", " );


    private ArrayList<T> columns;

    public Columns( T...columns ) {
        this.columns = new ArrayList(columns.length);

        Collections.addAll( this.columns, columns );
    }

    public Columns( ArrayList<T> columns ) {
        this.columns = columns;
    }

    public T getColumn( int index ) {
        return columns.get(index);
    }

    public int numColumns() {
        return columns.size();
    }

    public void addColumn( T col ) {
        this.columns.add( col );
    }

    public int indexOf( String targetColumnName ) {
        for ( int i=0; i<columns.size(); i++ ) {
            T col = columns.get(i);

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
                return index < columns.size();
            }

            public T next() {
                return columns.get(index++);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    // NB so far I have found that going parallel here has made next to no difference..  left the
    // code here to make future comparisons easier...  to be reviewed.
    // probably because we only had three columns at the time? will retry later
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



        ArrayList<T> cachedColumns = (ArrayList<T>) columns.clone();

        for ( int i=0; i<columns.size(); i++ ) {
            T cache = cacheColumnFactory.invoke( columns.get(i) );

            cachedColumns.set(i, cache);

            columns.get(i).prePopulateColumn( cache );
        }

        return new Columns(cachedColumns);
    }

    public static BooleanColumn isGTZero( SystemX system, LongColumn col ) {
        return new BooleanColumnFormula1<LongColumn>(system, col.getColumnName()+" > 0", col.getDescription(), "GTZ", col, 1) {
            protected boolean get( long row, LongColumn col ) {
                return col.get(row) > 0;
            }
        };
    }

    public static BooleanColumn isGT( SystemX system, LongColumn col, final long v ) {
        return new BooleanColumnFormula1<LongColumn>(system, col.getColumnName()+" > 0", col.getDescription(), "GTZ", col, 1) {
            protected boolean get( long row, LongColumn col ) {
                return col.get(row) > v;
            }
        };
    }

    public static BooleanColumn isLTZero( SystemX system, LongColumn col ) {
        return new BooleanColumnFormula1<LongColumn>(system, col.getColumnName()+" > 0", col.getDescription(), "GTZ", col, 1) {
            protected boolean get( long row, LongColumn col ) {
                return col.get(row) < 0;
            }
        };
    }

    public static BooleanColumn isLT( SystemX system, LongColumn col, final long v ) {
        return new BooleanColumnFormula1<LongColumn>(system, col.getColumnName()+" > 0", col.getDescription(), "GTZ", col, 1) {
            protected boolean get( long row, LongColumn col ) {
                return col.get(row) < v;
            }
        };
    }
}
