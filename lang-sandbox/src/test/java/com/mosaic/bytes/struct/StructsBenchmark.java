package com.mosaic.bytes.struct;

import com.mosaic.bytes.ArrayBytes2;
import com.mosaic.bytes.Bytes2;
import com.mosaic.bytes.struct.examples.trades.Trade;
import com.mosaic.bytes.struct.examples.trades.Trades;
import com.mosaic.io.bytes.Bytes;
import com.mosaic.io.memory.TradeFlyWeight;
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
public class StructsBenchmark {

    private static final long NUM_RECORDS = 2*1000*1000;

//    private SystemX system = new DebugSystem();
    private Bytes2 bytes  = new ArrayBytes2( TradeFlyWeight.SIZEOF_TRADE * (NUM_RECORDS + 1) );
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

    8.00ms per query
    8.06ms per query
    8.04ms per query
    7.48ms per query
    8.20ms per query

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
