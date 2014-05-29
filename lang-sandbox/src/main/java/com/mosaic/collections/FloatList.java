package com.mosaic.collections;

import java.util.Arrays;


/**
 *
 */
@SuppressWarnings("ConstantConditions")
public class FloatList {

    private float[] contents = new float[10];
    private int size;


    public int size() {
        return size;
    }

    public float get( int i ) {
        return size > i ? contents[i] : 0;
    }

    public void set( int i, float v ) {
        int contentsLength = contents.length;

        if ( i >= contentsLength ) {
            int newLength = Math.max( contentsLength *2, i+1 );

            contents = Arrays.copyOf( this.contents, newLength );
        }

        contents[i] = v;
        size        = Math.max( size, i+1 );
    }

    public void clear() {
        size = 0;

        Arrays.fill( contents, 0 );
    }

}
