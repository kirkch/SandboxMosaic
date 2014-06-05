package com.mosaic.columnstore;

import com.mosaic.io.streams.CharacterStream;


/**
 *
 */
public interface Column {

    public String getColumnName();
    public String getDescription();

    public boolean isSet( long row );

    /**
     * How many rows are in this column.  Starts from row zero and goes through to rowCount-1.
     */
    public long rowCount();

    public CellExplanation explain( long row );

    public void writeValueTo( CharacterStream out, long row );

}