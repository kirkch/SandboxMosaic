package com.mosaic.columnstore;

import com.mosaic.io.codecs.BooleanCodec;


/**
 *
 */
public interface BooleanColumn extends Column {

    public boolean get( long row );
    public void set( long row, boolean value );

    /**
     * The codec to use for reading/writing this column.
     */
    public BooleanCodec getCodec();

    public void prePopulateColumn( final BooleanColumn destinationColumn );

}
