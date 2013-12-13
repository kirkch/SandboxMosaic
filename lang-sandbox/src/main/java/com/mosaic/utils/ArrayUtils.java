package com.mosaic.utils;

import com.mosaic.lang.Factory;
import com.mosaic.lang.functional.Function0;
import com.mosaic.lang.functional.Function1;

import java.lang.reflect.Array;

/**
 *
 */
public class ArrayUtils {

    /**
     * Create a new array with each element filled with a new instance of T created by the factory.
     */
    public static <T> T[] fill( Class<T> elementType, int arrayLength, Factory<T> elementFactory ) {
        T[] array = (T[]) Array.newInstance( elementType, arrayLength );

        for ( int i=0; i<arrayLength; i++ ) {
            array[i] = elementFactory.create();
        }

        return array;
    }

    /**
     * Creates a new array containing the result of calling mapFunction on each element of the original array.
     */
    public static <A,B> B[] map( A[] array, Function1<A,B> mapFunction ) {
        int      arrayLength = array.length;
        Object[] newArray    = new Object[arrayLength];

        for ( int i=0; i<arrayLength; i++ ) {
            newArray[i] = mapFunction.invoke( array[i] );
        }

        return (B[]) newArray;
    }

    /**
     * Replace each element of the array with the result of calling mapFunction with the previous element value.
     */
    public static <T> T[] mapInline( T[] array, Function1<T,T> mapFunction ) {
        int arrayLength = array.length;

        for ( int i=0; i<arrayLength; i++ ) {
            array[i] = mapFunction.invoke( array[i] );
        }

        return array;
    }

    public static String makeString( Object[] array, String seperator ) {
        StringBuilder buf = new StringBuilder(100);

        for ( int i=0; i<array.length; i++ ) {
            if ( i > 0 ) {
                buf.append( seperator );
            }

            buf.append( array[i] );
        }

        return buf.toString();
    }

    public static Object[] newArray( int len, Function0 factory ) {
        Object[] array = new Object[len];

        for ( int i=0; i<len; i++ ) {
            array[i] = factory.invoke();
        }

        return array;
    }
    
}
