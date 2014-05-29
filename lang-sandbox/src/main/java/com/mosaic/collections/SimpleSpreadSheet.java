package com.mosaic.collections;

import java.util.Arrays;


/**
 * A simple spread sheet is a store of values accessed by a (row,col) pair.  When a cell is
 * accessed for the first time, it holds a null value and never errors with an index out of
 * bounds exception.
 *
 * It has been modelled as a list of lists, indexed first by row and then by column.
 */
public class SimpleSpreadSheet<T> {

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

}
