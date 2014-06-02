package com.mosaic.columnstore;

import com.mosaic.collections.LongSet;

import java.util.Collections;
import java.util.Map;


/**
 * Captures how a cell in the spreadsheet was calculated.  Used for debugging and review.<p/>
 */
@SuppressWarnings("unchecked")
public class CellExplanation {

    private String               formattedValue;
    private String               equation;
    private Map<String, LongSet> cellReferences;

    public CellExplanation( String formattedValue ) {
        this( formattedValue, formattedValue, Collections.EMPTY_MAP );
    }

    public CellExplanation( String formattedValue, String equation, Map<String,LongSet> cellReferences ) {
        this.formattedValue = formattedValue;
        this.equation       = equation;
        this.cellReferences = cellReferences;
    }

    /**
     * The value of the cell.
     */
    public String getFormattedValue() {
        return formattedValue;
    }

    /**
     * Other cells that were used in the calculation of this cell.  In turn those cells may
     * also have been calculated too.
     */
    public Map<String,LongSet> getReferencedCells() {
        return cellReferences;
    }

    public String toString() {
        return equation;
    }

}
