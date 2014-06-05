package com.mosaic.columnstore.aggregates;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.columnstore.columns.LongColumnFormula1;

import java.util.Map;


/**
 *
 */
public class MovingAverageLongColumn extends LongColumnFormula1 {

    private final long numSamples;

    public MovingAverageLongColumn( LongColumn source, int numSamples ) {
        super(
            source.getColumnName() + " MA" + numSamples,                                        // column name
            "The average value of the next "+numSamples+" values from "+source.getColumnName(), // description
            "MA" + numSamples,                                                                  // op label
            source,
            numSamples
        );

        this.numSamples = numSamples;
    }

    protected long get( long row, LongColumn col ) {
        long n     = 0;
        long count = 0;

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
