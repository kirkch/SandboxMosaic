package com.mosaic.columnstore.aggregates;

import com.mosaic.columnstore.FloatColumn;
import com.mosaic.columnstore.columns.FloatColumnArray;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.Before;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class MovingAverageFloatColumnBenchmark {
    private final int ROW_COUNT = 1000000;


    private SystemX system = new DebugSystem();


    private final FloatColumn price       = new FloatColumnArray( system, "cost", "d", ROW_COUNT );
    private final FloatColumn ma100       = new MovingAverageFloatColumn( system, price, 100 );
    private final FloatColumn cachedMA100 = new FloatColumnArray( system, "cost", "d", ROW_COUNT );


    @Before
    public void setup() {
        for ( long i=0; i<ROW_COUNT; i++ ) {
            price.set(i, i+1);
        }
    }

/*
   - 520us: lastN(new AverageAggregator());  hotspot was unable to inline AverageAggregator :(

   serial approach:      402694.00ns per call

    fork join:
    254510.00ns per call
    249453.00ns per call
    247545.00ns per call
    253105.00ns per call
    246061.00ns per call
    247207.00ns per call
     */

    @Benchmark(10)
    public float ma100Benchmark() {
        long rowCount = ma100.size();
        float count = 0;

        for ( long i=0; i<rowCount; i++ ) {
            count += ma100.get( i );
        }

        return count;
    }
/*
ma100
unoptimised serial 127ms
optimised serial   280ms
fork join           54ms

ma5
unoptimised serial   9ms
optimised serial    30ms
fork/join            4ms
 */
    @Benchmark(10)
    public void prepopulateMA100Benchmark() {
        ma100.prePopulateColumn( cachedMA100 );
    }

}
