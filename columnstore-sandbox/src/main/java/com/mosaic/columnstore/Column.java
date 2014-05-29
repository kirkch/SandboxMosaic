package com.mosaic.columnstore;

/**
 *
 */
public interface Column<T> {

    public boolean isSet( long row );
    public T get( long row );
    public void set( long row, T value );

    /**
     * How many rows are in this column.  Starts from row zero and goes through to rowCount-1.
     */
    public long rowCount();

    /**
     * Captures how the value in a specific cell was calculated.
     */
    public CellExplanation<T> explain( long row );

    public String getColumnName();
}
