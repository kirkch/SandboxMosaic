package com.mosaic.columnstore;

/**
 *
 */
public interface FloatColumn {

    public String getColumnName();

    public boolean isSet( long row );
    public float get( long row );
    public void set( long row, float value );

    /**
     * How many rows are in this column.  Starts from row zero and goes through to rowCount-1.
     */
    public long rowCount();

    /**
     * Captures how the value in a specific cell was calculated.
     */
    public CellExplanation<Float> explain( long row );

}
