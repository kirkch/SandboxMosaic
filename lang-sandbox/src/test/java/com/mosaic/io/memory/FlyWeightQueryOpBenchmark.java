package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class FlyWeightQueryOpBenchmark {

    private static final long NUM_RECORDS = 2*1000*1000;

    private SystemX        system = new DebugSystem();
    private Bytes          bytes  = Bytes.allocOffHeap( TradeFlyWeight.SIZEOF_TRADE * (NUM_RECORDS + 1) );
    private TradeFlyWeight trades = new TradeFlyWeight( system, bytes );

    @Before
    public void init() {
        trades.allocateNewRecords( NUM_RECORDS );


        for ( int i = 0; i < NUM_RECORDS; i++ ) {
            trades.select( i );

            trades.setTradeId(i);
            trades.setClientId(1);
            trades.setVenueCode(33);
            trades.setInstrumentCode(22);

            trades.setPrice(i);
            trades.setQuantity(i);

            trades.setSide((i & 1) == 0 ? 'B' : 'S');
        }
    }

    @After
    public void tearDown() {
        trades.release();
    }


/*
In order, on heap
    4.97ms per query
    4.98ms per query
    4.98ms per query
    4.57ms per query
    4.58ms per query

in order off heap
    5.37ms per query
    5.11ms per query
    5.13ms per query
    4.57ms per query
    5.20ms per query
     */

    @Benchmark( value=5, batchCount=5, units="query" )
    public long selectEachRecordInOrder() {
        long buyCost  = 0;
        long sellCost = 0;


        for (int i = 0; i < NUM_RECORDS; i++) {
            trades.select( i );

            if (trades.getSide() == 'B') {
                buyCost  += (trades.getPrice() * trades.getQuantity());
            } else {
                sellCost += (trades.getPrice() * trades.getQuantity());
            }
        }

        return buyCost + sellCost;
    }

/*
onheap
    6.48ms per query
    5.65ms per query
    5.66ms per query
    5.10ms per query
    6.08ms per query

offheap
    6.10ms per query
    7.52ms per query
    6.62ms per query
    5.65ms per query
    5.08ms per query
 */
    @Benchmark( value=5, batchCount=5, units="query" )
    public long slideToEachRecordInOrder() {
        long buyCost  = 0;
        long sellCost = 0;

        trades.select( 0 );
        do {

            if (trades.getSide() == 'B') {
                buyCost  += (trades.getPrice() * trades.getQuantity());
            } else {
                sellCost += (trades.getPrice() * trades.getQuantity());
            }
        } while ( trades.next() );

        return buyCost + sellCost;
    }

/*
    2.29ms per query
    2.54ms per query
    2.50ms per query
    2.18ms per query
    2.18ms per query
     */
    @Benchmark( value=5, batchCount=5, units="query" )
    public long parallelQuery() {
        return new TradeFlyWeight.TradeQuery().query( trades );
    }

}
