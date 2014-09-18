package com.mosaic.bytes.struct;

import static com.mosaic.bytes.struct.RedBullStructDefinition.*;


/**
 * Used for testing of FlyWeight.
 */
public class RedBullStruct {

    Struct struct;

    public RedBullStruct() {
        this( structRegistry.createNewStruct() );
    }

    public RedBullStruct( Struct struct ) {

        this.struct = struct;
    }


    public boolean getHasWings() {
        return hasWingsField.get( struct );
    }

    public void setHasWings( boolean hasWingsFlag ) {
        hasWingsField.set( struct, hasWingsFlag );
    }

    public int getAge() {
        return ageField.get( struct );
    }

    public void setAge( int age ) {
        ageField.set( struct, age );
    }

    public float getWeight() {
        return weightField.get(struct);
    }

    public void setWeight( float weight ) {
        weightField.set( struct, weight );
    }


    public String toString() {
        return Integer.toString( getAge() );
    }

}

