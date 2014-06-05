package com.mosaic.columnstore.aggregates;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.IntColumn;
import com.mosaic.columnstore.columns.IntColumnFormula1;

import java.util.Map;


/**
 *
 */
public class MovingAverageIntColumn extends IntColumnFormula1 {

    private final int numSamples;

    public MovingAverageIntColumn( IntColumn source, int numSamples ) {
        super(
            source.getColumnName() + " MA" + numSamples,                                        // column name
            "The average value of the next "+numSamples+" values from "+source.getColumnName(), // description
            "MA" + numSamples,                                                                  // op label
            source,
            numSamples
        );

        this.numSamples = numSamples;
    }

    protected int get( long row, IntColumn col ) {
        int n     = 0;
        int count = 0;

        while ( row >= 0 && n < numSamples ) {
            if ( col.isSet(row) ) {
                n     += 1;
                count += col.get( row );
            }

            row--;
        }

        return n == 0 ? 0 : count/n;
    }

    protected String toEquation( Map<String, LongSet> touchedCells ) {
        return "sum(cellValues)/numberOfCells";
    }
}
