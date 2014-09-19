package com.mosaic.bytes.struct.examples.redbull;

import com.mosaic.bytes.Bytes2;
import com.mosaic.bytes.struct.Struct;
import com.mosaic.bytes.struct.Structs;

import static com.mosaic.bytes.struct.examples.redbull.RedBullStructDefinition.*;


/**
 *
 */
public class RedBullStructs extends Structs<RedBullStruct> {

    public RedBullStructs( Bytes2 bytes ) {
        this( bytes, 0 );
    }

    public RedBullStructs( Bytes2 bytes, long headerSize ) {
        super( headerSize, bytes, structRegistry.sizeBytes() );
    }


    protected RedBullStruct createBlankStruct() {
        return new RedBullStruct();
    }

    protected Struct toStruct( RedBullStruct bull ) {
        return bull.struct;
    }

}
