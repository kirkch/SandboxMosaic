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


// Alternative approach that was 25% slower.  When hotspot was able to inline AverageAggregator (to reduce
// register to memory copies) then it was just as fast as the current approach.  It is a shame that hotspot
// could not do this without help.   Keeping the code here to re-evaluate later versions of hotspot.
// The util functions are on FloatColumnUtils.
//
// To encourage hotspot to inline AverageAggregator, instantiate it within the get() method.  Passing it
// in as an argument is what is preventing it from making the optimisation.
//    protected float get( long row, FloatColumn col ) {
//        FloatAggregator agg = new AverageAggregator();
//
//        long from = row;
//        int  N    = numSamples;
//
//        return lastN( col, from, N, agg );
//    }

}