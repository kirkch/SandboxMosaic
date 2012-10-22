package com.mosaic.utils;

import com.mosaic.lang.Factory;

import java.lang.reflect.Array;

/**
 *
 */
public class ArrayUtils {

    public static <T> T[] fill( Class<T> elementType, int arrayLength, Factory<T> elementFactory ) {
        T[] array = (T[]) Array.newInstance( elementType, arrayLength );

        for ( int i=0; i<arrayLength; i++ ) {
            array[i] = elementFactory.create();
        }

        return array;
    }

}
