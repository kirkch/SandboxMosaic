package com.mosaic.columnstore.columns;

import com.mosaic.collections.LongSet;


/**
 *
 */
public interface ColumnAuditor {

    public LongSet getVisitedRows();

}
