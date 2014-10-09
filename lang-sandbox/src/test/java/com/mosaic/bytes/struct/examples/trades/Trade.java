package com.mosaic.bytes.struct.examples.trades;

import com.mosaic.bytes.struct.Struct;

import static com.mosaic.bytes.struct.examples.trades.TradeLayout.*;


/**
 *
 */
public class Trade extends Struct {

    public static final long SIZE_BYTES = structRegistry.sizeBytes();


    public Trade() {
        super( structRegistry );
    }



    public long getTradeId() {
        return tradeIdField.get( this );
    }

    public void setTradeId( long newId ) {
        tradeIdField.set( this, newId );
    }


    public long getClientId() {
        return clientIdField.get( this );
    }

    public void setClientId( long newId ) {
        clientIdField.set( this, newId );
    }


    public int getVenueCode() {
        return venueCodeField.get( this );
    }

    public void setVenueCode( int newCode ) {
        venueCodeField.set( this, newCode );
    }


    public int getInstrumentCode() {
        return instrumentCodeField.get( this );
    }

    public void setInstrumentCode( int newCode ) {
        instrumentCodeField.set( this, newCode );
    }


    public long getPrice() {
        return priceField.get( this );
    }

    public void setPrice( long newPrice ) {
        priceField.set( this, newPrice );
    }


    public long getQuantity() {
        return quantityField.get( this );
    }

    public void setQuantity( long newQuantity ) {
        quantityField.set( this, newQuantity );
    }


    public char getSide() {
        return sideField.get( this );
    }

    public void setSide( char newSide ) {
        sideField.set( this, newSide );
    }

}
