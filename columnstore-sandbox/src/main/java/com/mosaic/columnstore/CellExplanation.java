package com.mosaic.columnstore;

import java.util.List;


/**
 * Captures how a cell in the spreadsheet was calculated.  Used for debugging and review.
 */
public interface CellExplanation<T> {

    public String getDescriptiveNameNbl();

    public String getOpNameNbl();
    public String getColumnNameNbl();

    public long[] getRowIds();

    public List<CellExplanation> getOperandsNbl();

    public T getValue();

    public abstract String toString();

}
