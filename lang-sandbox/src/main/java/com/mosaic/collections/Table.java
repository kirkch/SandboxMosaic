package com.mosaic.collections;

import java.util.Arrays;


/**
 * Represents a SpreadSheet.  Modelled as a list of lists, indexed first by row and then by column.
 */
public class Table<T> {

    private DynamicList<DynamicList<T>> rows = new DynamicList<>();


    public void clear() {
        rows.clear();
    }

    public T get( int row, int col ) {
        DynamicList<T> r = rows.get(row);

        return r == null ? null : r.get(col);
    }

    public void set( int row, int column, T value ) {
        DynamicList<T> r = rows.get(row);
        if ( r == null ) {
            r = new DynamicList<>();

            rows.set( row, r );
        }

        r.set( column, value );
    }

    public int rowCount() {
        return rows.size();
    }

    public int columnCount( int row ) {
        DynamicList<T> r = rows.get(row);

        return r == null ? 0 : r.size();
    }


    /**
     * Less keen to throw index out of bound exceptions that java.util.List.
     */
    @SuppressWarnings("unchecked")
    private static class DynamicList<T> {
        private T[] contents = (T[]) new Object[10];
        private int size;


        public int size() {
            return size;
        }

        public T get( int i ) {
            return size > i ? contents[i] : null;
        }

        public void set( int i, T v ) {
            int contentsLength = contents.length;

            if ( i >= contentsLength ) {
                int newLength = Math.max( contentsLength *2, i+1 );

                contents = Arrays.copyOf( this.contents, newLength );
            }

            contents[i] = v;
            size        = Math.max( size, i+1 );
        }

        public void clear() {
            size = 0;

            Arrays.fill( contents, null );
        }
    }
}
