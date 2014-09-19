package com.mosaic.bytes.struct.examples.trades;

import com.mosaic.bytes.struct.Struct;

import static com.mosaic.bytes.struct.examples.trades.TradeLayout.*;


/**
 *
 */
public class Trade {

    public static final long SIZE_BYTES = structRegistry.sizeBytes();


    Struct struct;

    public Trade() {
        this( structRegistry.createNewStruct() );
    }

    public Trade( Struct struct ) {
        this.struct = struct;
    }


    public long getTradeId() {
        return tradeIdField.get( struct );
    }

    public void setTradeId( long newId ) {
        tradeIdField.set( struct, newId );
    }


    public long getClientId() {
        return clientIdField.get( struct );
    }

    public void setClientId( long newId ) {
        clientIdField.set( struct, newId );
    }


    public int getVenueCode() {
        return venueCodeField.get( struct );
    }

    public void setVenueCode( int newCode ) {
        venueCodeField.set( struct, newCode );
    }


    public int getInstrumentCode() {
        return instrumentCodeField.get( struct );
    }

    public void setInstrumentCode( int newCode ) {
        instrumentCodeField.set( struct, newCode );
    }


    public long getPrice() {
        return priceField.get( struct );
    }

    public void setPrice( long newPrice ) {
        priceField.set( struct, newPrice );
    }


    public long getQuantity() {
        return quantityField.get( struct );
    }

    public void setQuantity( long newQuantity ) {
        quantityField.set( struct, newQuantity );
    }


    public char getSide() {
        return sideField.get( struct );
    }

    public void setSide( char newSide ) {
        sideField.set( struct, newSide );
    }

}
