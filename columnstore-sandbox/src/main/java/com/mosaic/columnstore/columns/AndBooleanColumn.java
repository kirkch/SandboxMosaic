package com.mosaic.columnstore.columns;

import com.mosaic.columnstore.BooleanColumn;


/**
 *
 */
public class AndBooleanColumn extends BooleanColumnFormula2 {

    public AndBooleanColumn( BooleanColumn col1, BooleanColumn col2 ) {
        super(
            col1.getColumnName() + " AND " + col2.getColumnName(),
            col1.getColumnName() + " AND " + col2.getColumnName(),
            "AND",
            col1,
            col2,
            2
        );
    }

    protected boolean isSet( long row, BooleanColumn col1, BooleanColumn col2 ) {
        return col1.isSet(row) && col1.isSet(row);
    }

    protected boolean get( long row, BooleanColumn col1, BooleanColumn col2 ) {
        return col1.get(row) && col2.get(row);
    }

}
