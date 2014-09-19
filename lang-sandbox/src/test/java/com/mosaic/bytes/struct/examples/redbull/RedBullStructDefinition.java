package com.mosaic.bytes.struct.examples.redbull;


import com.mosaic.bytes.struct.BooleanField;
import com.mosaic.bytes.struct.FloatField;
import com.mosaic.bytes.struct.IntField;
import com.mosaic.bytes.struct.StructRegistry;


/**
 *
 */
class RedBullStructDefinition {

    static final StructRegistry structRegistry = new StructRegistry();

    static final BooleanField hasWingsField    = structRegistry.registerBoolean();
    static final IntField     ageField         = structRegistry.registerInteger();
    static final FloatField   weightField      = structRegistry.registerFloat();

}
