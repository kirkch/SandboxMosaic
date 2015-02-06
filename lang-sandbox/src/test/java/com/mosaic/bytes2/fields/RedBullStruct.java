package com.mosaic.bytes2.fields;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.bytes2.FixedWidthBytesView;


/**
 *
 */
public class RedBullStruct extends FixedWidthBytesView {

    private static final ByteFieldsRegistry2 fieldsRegistry = new ByteFieldsRegistry2();

    private static final BooleanField2 hasWingsField    = fieldsRegistry.registerBoolean();
    private static final IntField2     ageField         = fieldsRegistry.registerInteger();
    private static final FloatField2   weightField      = fieldsRegistry.registerFloat();


    public static long SIZE_BYTES = fieldsRegistry.sizeBytes();



    public RedBullStruct() {
        super( fieldsRegistry );
    }

    public RedBullStruct( Bytes2 bytes ) {
        super( fieldsRegistry, bytes );
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
