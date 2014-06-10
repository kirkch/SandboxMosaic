package com.mosaic.columnstore.aggregates;

import com.mosaic.columnstore.FloatColumn;
import com.mosaic.columnstore.columns.FloatColumnFormula1;


/**
 * Generates the average of the last 'n' set cells of the source column.
 */
public class MovingAverageFloatColumn extends FloatColumnFormula1 {

    private final int numSamples;

    public MovingAverageFloatColumn( FloatColumn source, int numSamples ) {
        super( source.getColumnName() + " MA" + numSamples, "desc", "MA" + numSamples, source, numSamples );

        this.numSamples = numSamples;
    }

    protected float get( long row, FloatColumn col ) {
        int   n     = 0;
        float count = 0;

        while ( row >= 0 && n < numSamples ) {
            if ( col.isSet(row) ) {
                n     += 1;
                count += col.get( row );
            }

            row--;
        }

        return n == 0 ? 0 : count/n;
    }

}