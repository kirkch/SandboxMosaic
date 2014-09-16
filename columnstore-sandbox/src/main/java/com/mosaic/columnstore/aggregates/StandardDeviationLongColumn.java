package com.mosaic.columnstore.aggregates;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.columnstore.columns.LongColumnFormula1;
import com.mosaic.lang.system.SystemX;

import java.util.Map;


/**
 *
 */
public class StandardDeviationLongColumn extends LongColumnFormula1 {

    private final long numSamples;

    public StandardDeviationLongColumn( SystemX system, LongColumn source, int numSamples ) {
        super(
            system,
            source.getColumnName() + " StdDev" + numSamples,                                // column name
            "The standard deviation of "+numSamples+" values from "+source.getColumnName(), // description
            "StdDev" + numSamples,                                                          // op label
            source,
            numSamples
        );

        this.numSamples = numSamples;
    }

    protected long get( long row, LongColumn col ) {
        double mean = calcMean(row, col);

        double n   = 0;
        double sum = 0;

        while ( row >= 0 && n < numSamples ) {
            if ( col.isSet(row) ) {
                n     += 1;

                double v = col.get(row) - mean;

                sum += v * v;
            }

            row--;
        }

        if ( n == 0 ) {
            return 0;
        }

        return Math.round( Math.sqrt(sum/n) );
    }


    private double calcMean( long row, LongColumn col ) {
        double n     = 0;
        double count = 0;

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
        return "stddev(cells)";
    }
}
