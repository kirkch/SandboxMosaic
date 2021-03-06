package com.mosaic.columnstore.aggregates;

import com.mosaic.columnstore.LongColumn;
import com.mosaic.columnstore.columns.LongColumnFormula1;
import com.mosaic.lang.system.SystemX;


/**
 *
 */
public class SumLastNLongColumn extends LongColumnFormula1 {

    private final long numSamples;

    public SumLastNLongColumn( SystemX system, String columnName, String description, String opLabel, LongColumn source, int numSamples ) {
        super( system, columnName, description, opLabel, source, numSamples );

        this.numSamples = numSamples;
    }

    public SumLastNLongColumn( SystemX system, LongColumn source, int numSamples ) {
        super(
            system,
            "Sum " + numSamples + " " + source.getColumnName(),                           // column name
            "Sum of the previous "+numSamples+" set values from "+source.getColumnName(), // description
            "SUM" + numSamples,                                                           // op label
            source,
            numSamples
        );

        this.numSamples = numSamples;
    }

    protected long get( long row, LongColumn col ) {
        long numCellsSummed = 0;
        long sum            = 0;

        while ( row >= 0 && numCellsSummed < numSamples ) {
            if ( col.isSet(row) ) {
                numCellsSummed += 1;
                sum            += col.get( row );
            }

            row--;
        }

        return sum;
    }

}
