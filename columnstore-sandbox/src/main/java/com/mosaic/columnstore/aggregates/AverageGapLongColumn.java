package com.mosaic.columnstore.aggregates;

import com.mosaic.collections.LongSet;
import com.mosaic.columnstore.LongColumn;
import com.mosaic.columnstore.columns.LongColumnFormula2;

import java.util.Map;


/**
 * Averages the distance between two other columns.
 */
public class AverageGapLongColumn extends LongColumnFormula2 {

    private final long numSamples;

    public AverageGapLongColumn( String columnName, LongColumn col1, LongColumn col2, int numSamples ) {
        super(
            columnName,
            "The average gap from the last "+numSamples+" values from "+col1.getColumnName()
            + " and " + col2.getColumnName(),                                                 // description
            "GAP" + numSamples,                                                               // op label
            col1,
            col2,
            numSamples
        );

        this.numSamples = numSamples;
    }

    protected boolean isSet( long row, LongColumn col1, LongColumn col2 ) {
        for ( long i=row; i>0; i++ ) {
            if ( col1.isSet(row) && col2.isSet(row) ) {
                return true;
            }
        }

        return false;
    }

    protected long get( long row, LongColumn high, LongColumn low ) {
        long n   = 0;
        long sum = 0;

        while ( row >= 0 && n < numSamples ) {
            if ( high.isSet(row) && low.isSet(row) ) {
                n     += 1;
                sum += Math.abs( high.get(row) - low.get(row) );
            }

            row--;
        }

        return n == 0 ? 0 : sum/n;
    }

    protected String toEquation( Map<String, LongSet> touchedCells ) {
        return "avggap(col1_cells,col2_cells)";
    }

}
