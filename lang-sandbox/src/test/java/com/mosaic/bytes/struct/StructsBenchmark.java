package com.mosaic.bytes.struct;

import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.OffHeapBytes;
import com.mosaic.bytes.struct.examples.trades.Trade;
import com.mosaic.bytes.struct.examples.trades.Trades;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class StructsBenchmark {

    private static final long NUM_RECORDS = 2*1000*1000;

    private Bytes bytes  = new OffHeapBytes( Trade.SIZE_BYTES * (NUM_RECORDS + 1) );
    private Trades trades = new Trades( bytes );
    private Trade  trade  = new Trade();


    @Before
    public void init() {
        trades.allocateNewRecords( NUM_RECORDS );


        for ( int i = 0; i < NUM_RECORDS; i++ ) {
            trades.getInto( trade, i );

            trade.setTradeId(i);
            trade.setClientId(1);
            trade.setVenueCode(33);
            trade.setInstrumentCode(22);

            trade.setPrice(i);
            trade.setQuantity(i);

            trade.setSide((i & 1) == 0 ? 'B' : 'S');
        }
    }

    @After
    public void tearDown() {
        bytes.release();
    }


/*
In order, on heap
    4.97ms per query
    4.98ms per query
    4.98ms per query
    4.57ms per query
    4.58ms per query

    6.66ms per query
    6.46ms per query
    6.79ms per query
    7.01ms per query
    6.79ms per query

in order off heap
    5.37ms per query  // before Structs (went direct to Bytes)
    5.11ms per query
    5.13ms per query
    4.57ms per query
    5.20ms per query

    6.95ms per query // after Structs -- most of the cost is in an extra addition; as we have the choice to use or to not
    6.79ms per query //                  use then this cost is acceptable
    7.14ms per query
    6.84ms per query
    7.90ms per query
     */

    @Benchmark( value=5, batchCount=5, units="query" )
    public long selectEachRecordInOrder() {
        long buyCost  = 0;
        long sellCost = 0;


        for (int i = 0; i < NUM_RECORDS; i++) {
            trades.getInto( trade, i );

            if (trade.getSide() == 'B') {
                buyCost  += (trade.getPrice() * trade.getQuantity());
            } else {
                sellCost += (trade.getPrice() * trade.getQuantity());
            }
        }

        return buyCost + sellCost;
    }

}
