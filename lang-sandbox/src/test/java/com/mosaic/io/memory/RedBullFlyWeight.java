package com.mosaic.io.memory;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.system.SystemX;

import static com.mosaic.lang.system.SystemX.*;


/**
 * Used for testing of FlyWeight.
 */
public class RedBullFlyWeight extends FlyWeightBytes<RedBullFlyWeight> {

    private static final int HASWINGS_OFFSET = 0;
    private static final int AGE_OFFSET      = HASWINGS_OFFSET  + SIZEOF_BOOLEAN;
    private static final int WEIGHT_OFFSET   = AGE_OFFSET + SIZEOF_INT;

    public static final int SIZEOF_BULL   = WEIGHT_OFFSET + SIZEOF_FLOAT;



    public RedBullFlyWeight( SystemX system, Bytes records ) {
        super( system, records, SIZEOF_BULL );
    }

    public RedBullFlyWeight( SystemX system, long startFromOffset, Bytes records ) {
        super( system, startFromOffset, records, SIZEOF_BULL );
    }


    public boolean getHasWings() {
        return super.readBoolean( HASWINGS_OFFSET );
    }

    public void setHasWings( boolean hasWings ) {
        super.writeBoolean( HASWINGS_OFFSET, hasWings );
    }

    public int getAge() {
        return super.readInteger( AGE_OFFSET );
    }

    public void setAge( int age ) {
        super.writeInteger( AGE_OFFSET, age );
    }

    public float getWeight() {
        return super.readFloat( WEIGHT_OFFSET );
    }

    public void setWeight( float weight ) {
        super.writeFloat( WEIGHT_OFFSET, weight );
    }


    public String toString() {
        return Integer.toString( getAge() );
    }
}

