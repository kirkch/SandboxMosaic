package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.system.SystemX;

import static com.mosaic.lang.system.SystemX.*;


/**
 *
 */
public class TradeFlyWeight extends FlyWeightBytes<TradeFlyWeight> {

    private static final int TRADEID_OFFSET        = 0;
    private static final int CLIENTID_OFFSET       = TRADEID_OFFSET        + SIZEOF_LONG;
    private static final int VENUECODE_OFFSET      = CLIENTID_OFFSET       + SIZEOF_LONG;
    private static final int INSTRUMENTCODE_OFFSET = VENUECODE_OFFSET      + SIZEOF_INT;
    private static final int PRICE_OFFSET          = INSTRUMENTCODE_OFFSET + SIZEOF_INT;
    private static final int QUANTITY_OFFSET       = PRICE_OFFSET          + SIZEOF_LONG;
    private static final int SIDE_OFFSET           = QUANTITY_OFFSET       + SIZEOF_LONG;

    public static final int SIZEOF_TRADE = SIDE_OFFSET + SIZEOF_CHAR;



    public TradeFlyWeight( SystemX system, Bytes records ) {
        super( system, records, SIZEOF_TRADE );
    }


    public long getTradeId() {
        return super.readLong( TRADEID_OFFSET );
    }

    public void setTradeId( long tradeId ) {
        super.writeLong( TRADEID_OFFSET, tradeId );
    }


    public long getClientId() {
        return super.readLong( CLIENTID_OFFSET );
    }

    public void setClientId( final long clientId ) {
        super.writeLong( CLIENTID_OFFSET, clientId );
    }


    public int getVenueCode() {
        return super.readInteger( VENUECODE_OFFSET );
    }

    public void setVenueCode( final int venueCode ) {
        super.writeInteger( VENUECODE_OFFSET, venueCode );
    }


    public int getInstrumentCode() {
        return super.readInteger( INSTRUMENTCODE_OFFSET );
    }

    public void setInstrumentCode( final int instrumentCode ) {
        super.writeInteger( INSTRUMENTCODE_OFFSET, instrumentCode );
    }


    public long getPrice() {
        return super.readLong( PRICE_OFFSET );
    }

    public void setPrice( final long price ) {
        super.writeLong( PRICE_OFFSET, price );
    }


    public long getQuantity() {
        return super.readLong( QUANTITY_OFFSET );
    }

    public void setQuantity( final long quantity ) {
        super.writeLong( QUANTITY_OFFSET, quantity );
    }


    public char getSide() {
        return super.readCharacter( SIDE_OFFSET );
    }

    public void setSide( final char side ) {
        super.writeCharacter( SIDE_OFFSET, side );
    }



    public static class TradeQuery extends FlyWeightQueryOp<TradeFlyWeight,Long> {
        protected Long doQuery( TradeFlyWeight trades, long fromInc, long toExc ) {
            long buyCost  = 0;
            long sellCost = 0;

            for ( long i=fromInc; i<toExc; i++ ) {
                trades.select( i );

                if (trades.getSide() == 'B') {
                    buyCost  += (trades.getPrice() * trades.getQuantity());
                } else {
                    sellCost += (trades.getPrice() * trades.getQuantity());
                }
            }

            return buyCost + sellCost;
        }

        protected Long doMerge( Long r1, Long r2 ) {
            return r1 + r2;
        }
    }

}
