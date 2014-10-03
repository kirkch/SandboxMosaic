package com.mosaic.bytes.struct.examples.redbull;

import com.mosaic.bytes.struct.PersistentStruct;
import com.mosaic.io.filesystemx.FileX;

import static com.mosaic.bytes.struct.examples.redbull.RedBullStructDefinition.*;


/**
 *
 */
public class PersistentRedBull extends PersistentStruct<PersistentRedBull> {

    public PersistentRedBull( FileX dataFile ) {
        super( dataFile, RedBullStructDefinition.structRegistry );
    }


    public boolean getHasWings() {
        return hasWingsField.get(struct);
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