package com.mosaic.columnstore;

import com.mosaic.columnstore.columns.FloatAggregator;


/**
 * This class was an experiment into increasing the reuse of code amongst column formulas.  Unfortunately
 * it was >20% slower.  Kept here as a) it may sometimes be a worth while trade off, b) caching may
 * negate the issue and c) in time hotspot may be able to optimise that 20% away (caused by excessive register
 * copying).
 */
public class FloatColumnUtils {

    public static class AverageAggregator implements FloatAggregator {
        private int     n = 0;
        private float sum = 0;

        public void append( float v ) {
            n   += 1;
            sum += v;
        }

        public float result() {
            return n == 0 ? 0 : sum/n;
        }
    }

    public static float lastN( FloatColumn col, long from, int numSamples, FloatAggregator agg ) {
        long row = from;
        int  n   = 0;

        while ( row >= 0 && n < numSamples ) {
            if ( col.isSet(row) ) {
                agg.append( col.get(row) );

                n++;
            }

            row--;
        }

        return agg.result();
    }

}
