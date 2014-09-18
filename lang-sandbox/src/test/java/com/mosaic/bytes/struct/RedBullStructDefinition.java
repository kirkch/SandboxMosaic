package com.mosaic.bytes.struct;


/**
 *
 */
class RedBullStructDefinition {

    static final StructRegistry structRegistry = new StructRegistry();

    static final BooleanField hasWingsField = structRegistry.registerBoolean();
    static final IntField     ageField      = structRegistry.registerInteger();
    static final FloatField   weightField   = structRegistry.registerFloat();


}
