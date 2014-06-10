package com.mosaic.columnstore.aggregates;

import com.mosaic.columnstore.FloatColumn;
import com.mosaic.columnstore.columns.FloatColumnArray;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.Before;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class MovingAverageFloatColumnBenchmark {
    private final int ROW_COUNT = 2000;

    private final FloatColumn              price = new FloatColumnArray( "cost", "d", ROW_COUNT );
    private final MovingAverageFloatColumn ma100 = new MovingAverageFloatColumn( price, 100 );

    @Before
    public void setup() {
        for ( long i=0; i<ROW_COUNT; i++ ) {
            price.set(i, i+1);
        }
    }

/*
   - 520us: lastN(new AverageAggregator());  hotspot was unable to inline AverageAggregator :(

   current approach:
    402478.00ns per call
    402433.00ns per call
    404638.00ns per call
    403893.00ns per call
    402694.00ns per call
    404544.00ns per call
     */

    @Benchmark(1000)
    public float ma100Benchmark() {
        long rowCount = ma100.size();
        float count = 0;

        for ( long i=0; i<rowCount; i++ ) {
            count += ma100.get( i );
        }

        return count;
    }

}
