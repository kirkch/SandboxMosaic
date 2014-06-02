package com.mosaic.collections;


/**
 * A simple spread sheet is a store of values accessed by a (row,col) pair.  When a cell is
 * accessed for the first time, it holds a null value and never errors with an index out of
 * bounds exception.
 *
 * It has been modelled as a list of lists, indexed first by row and then by column.
 */
public class SimpleSpreadSheet<T> {

    private DynamicArrayObject<DynamicArrayObject<T>> rows = new DynamicArrayObject<>();


    public void clear() {
        rows.clear();
    }

    public T get( int row, int col ) {
        DynamicArrayObject<T> r = rows.get(row);

        return r == null ? null : r.get(col);
    }

    public void set( int row, int column, T value ) {
        DynamicArrayObject<T> r = rows.get(row);
        if ( r == null ) {
            r = new DynamicArrayObject<>();

            rows.set( row, r );
        }

        r.set( column, value );
    }

    public int rowCount() {
        return rows.size();
    }

    public int columnCount( int row ) {
        DynamicArrayObject<T> r = rows.get(row);

        return r == null ? 0 : r.size();
    }

}
