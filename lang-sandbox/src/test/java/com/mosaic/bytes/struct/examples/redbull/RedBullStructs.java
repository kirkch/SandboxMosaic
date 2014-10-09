package com.mosaic.bytes.struct.examples.redbull;

import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.struct.Structs;

import static com.mosaic.bytes.struct.examples.redbull.RedBullStructDefinition.*;


/**
 *
 */
public class RedBullStructs extends Structs<RedBullStruct> {

    public RedBullStructs( Bytes bytes ) {
        this( bytes, 0 );
    }

    public RedBullStructs( Bytes bytes, long headerSize ) {
        super( headerSize, bytes, structRegistry.sizeBytes() );
    }


    protected RedBullStruct createBlankStruct() {
        return new RedBullStruct();
    }

}
