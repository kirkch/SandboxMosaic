package com.mosaic.bytes.struct.examples.trades;

import com.mosaic.bytes.struct.*;;


/**
 *
 */
class TradeLayout {

    static final StructRegistry structRegistry = new StructRegistry();

    static final LongField      tradeIdField        = structRegistry.registerLong();
    static final LongField      clientIdField       = structRegistry.registerLong();
    static final IntField       venueCodeField      = structRegistry.registerInteger();
    static final IntField       instrumentCodeField = structRegistry.registerInteger();
    static final LongField      priceField          = structRegistry.registerLong();
    static final LongField      quantityField       = structRegistry.registerLong();
    static final CharacterField sideField           = structRegistry.registerCharacter();

}
