package com.mosaic.columnstore;

import com.mosaic.io.streams.CharacterStream;


/**
 *
 */
public interface Column {

    public String getColumnName();
    public String getDescription();

    public boolean isSet( long row );

    public void unset( long row );

    /**
     * How many rows are in this column.  Starts from row zero and goes through to rowCount-1.
     */
    public long size();

    public void resizeIfNecessary( long newSize );

    public CellExplanation explain( long row );

    public void writeValueTo( CharacterStream out, long row );

}