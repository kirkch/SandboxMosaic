package com.mosaic.bytes.struct.examples.redbull;

import com.mosaic.bytes.struct.Struct;


/**
 * Used for testing of FlyWeight.
 */
public class RedBullStruct {

    Struct struct;

    public RedBullStruct() {
        this( RedBullStructDefinition.structRegistry.createNewStruct() );
    }

    public RedBullStruct( Struct struct ) {
        this.struct = struct;
    }


    public boolean getHasWings() {
        return RedBullStructDefinition.hasWingsField.get( struct );
    }

    public void setHasWings( boolean hasWingsFlag ) {
        RedBullStructDefinition.hasWingsField.set( struct, hasWingsFlag );
    }

    public int getAge() {
        return RedBullStructDefinition.ageField.get( struct );
    }

    public void setAge( int age ) {
        RedBullStructDefinition.ageField.set( struct, age );
    }

    public float getWeight() {
        return RedBullStructDefinition.weightField.get(struct);
    }

    public void setWeight( float weight ) {
        RedBullStructDefinition.weightField.set( struct, weight );
    }


    public String toString() {
        return Integer.toString( getAge() );
    }

}

