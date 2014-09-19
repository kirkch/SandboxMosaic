package com.mosaic.bytes.struct.examples.trades;

import com.mosaic.bytes.Bytes2;
import com.mosaic.bytes.struct.Struct;
import com.mosaic.bytes.struct.Structs;

import static com.mosaic.bytes.struct.examples.trades.TradeLayout.structRegistry;


/**
 *
 */
public class Trades extends Structs<Trade> {

    public Trades( Bytes2 bytes ) {
        this( bytes, 0 );
    }

    public Trades( Bytes2 bytes, long headerSize ) {
        super( headerSize, bytes, structRegistry.sizeBytes() );
    }


    protected Trade createBlankStruct() {
        return new Trade();
    }

    protected Struct toStruct( Trade trade ) {
        return trade.struct;
    }

}
