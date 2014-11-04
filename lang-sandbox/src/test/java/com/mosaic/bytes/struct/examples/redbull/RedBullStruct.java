package com.mosaic.bytes.struct.examples.redbull;

import com.mosaic.bytes.ArrayBytes;
import com.mosaic.bytes.Bytes;
import com.mosaic.bytes.struct.Struct;

import static com.mosaic.bytes.struct.examples.redbull.RedBullStructDefinition.*;


/**
 * Used for testing of FlyWeight.
 */
public class RedBullStruct extends Struct {

    public static final long SIZE_BYTES = RedBullStructDefinition.structRegistry.sizeBytes();


    public static RedBullStruct allocateOnHeap() {
        RedBullStruct struct = new RedBullStruct();

        Bytes bytes = new ArrayBytes( struct.sizeBytes() );
        struct.setBytes( bytes, 0, struct.sizeBytes() );

        return struct;
    }

    public RedBullStruct() {
        super( RedBullStructDefinition.structRegistry );
    }


    public boolean getHasWings() {
        return hasWingsField.get(this);
    }

    public void setHasWings( boolean hasWingsFlag ) {
        hasWingsField.set( this, hasWingsFlag );
    }

    public int getAge() {
        return ageField.get( this );
    }

    public void setAge( int age ) {
        ageField.set( this, age );
    }

    public float getWeight() {
        return weightField.get(this);
    }

    public void setWeight( float weight ) {
        weightField.set( this, weight );
    }


    public String toString() {
        return Integer.toString( getAge() );
    }

}

