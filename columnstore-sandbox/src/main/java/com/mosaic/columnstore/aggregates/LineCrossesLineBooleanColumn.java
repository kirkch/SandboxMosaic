package com.mosaic.columnstore.aggregates;

import com.mosaic.columnstore.LongColumn;
import com.mosaic.columnstore.columns.LongLong2BooleanFormula;


/**
 *
 */
public class LineCrossesLineBooleanColumn extends LongLong2BooleanFormula {

    private LongColumn sourceColumn1;
    private LongColumn sourceColumn2;


    public LineCrossesLineBooleanColumn( String columnName, String description, String opName, LongColumn sourceColumn1, LongColumn sourceColumn2 ) {
        super( columnName, description, opName, sourceColumn1, sourceColumn2 );

        this.sourceColumn1 = sourceColumn1;
        this.sourceColumn2 = sourceColumn2;
    }

    protected boolean isSet( long row, LongColumn col1, LongColumn col2 ) {
        return col1.isSet(row) && col1.isSet(row)
            && hasPrevious(row, col1) && hasPrevious(row, col1);
    }

    protected boolean get( long row, LongColumn col1, LongColumn col2 ) {
        long a1 = previousValue( row, col1 );
        long a2 = col1.get(row);

        long b1 = previousValue( row, col2 );
        long b2 = col2.get(row);


        return a1 < b1 && a2 >= b2;
    }

    private long previousValue( long row, LongColumn col ) {
        long i = row-1;

        while ( i >= 0 ) {
            if ( col.isSet(i) ) {
                return col.get(i);
            }

            i--;
        }

        return 0;
    }

    private boolean hasPrevious( long row, LongColumn col ) {
        long i = row-1;

        while ( i >= 0 ) {
            if ( col.isSet(i) ) {
                return true;
            }

            i--;
        }

        return false;
    }

}
