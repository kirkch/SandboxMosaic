package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.Before;
import org.junit.Test;

import static com.mosaic.io.memory.TradeFlyWeight.TradeQuery;
import static org.junit.Assert.assertEquals;


/**
 *
 */
public class FlyWeightQueryOpTest {

    private static final long NUM_RECORDS = 2*1000*1000;

    private SystemX system = new DebugSystem();
    private Bytes bytes  = Bytes.allocOffHeap( TradeFlyWeight.SIZEOF_TRADE * (NUM_RECORDS + 1) );
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

    @Test
    public void compareSingleThreadResultWithMultiThreadResult() {
        TradeQuery query = new TradeQuery();

        long singleThreadResult    = query.doQuery( trades, 0, NUM_RECORDS );
        long multipleThreadsResult = query.query( trades );

        assertEquals( singleThreadResult, multipleThreadsResult );
        assertEquals( 2666664666667000000L, multipleThreadsResult );
    }

}
