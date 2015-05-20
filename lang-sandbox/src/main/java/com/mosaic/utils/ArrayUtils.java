package com.mosaic.utils;

import com.mosaic.lang.Factory;
import com.mosaic.lang.functional.Function0;
import com.mosaic.lang.functional.Function1;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 */
@SuppressWarnings("unchecked")
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
    public static <A,B> Object[] map( A[] array, Function1<A,B> mapFunction ) {
        int      arrayLength = array.length;
        Object[] newArray    = new Object[arrayLength];

        for ( int i=0; i<arrayLength; i++ ) {
            newArray[i] = mapFunction.invoke( array[i] );
        }

        return newArray;
    }

    /**
     * Creates a new array containing the result of calling mapFunction on each element of the original array.
     */
    public static <A,B> B[] map( Class<B> type, A[] array, Function1<A,B> mapFunction ) {
        int arrayLength = array.length;
        B[] newArray    = (B[]) Array.newInstance( type, arrayLength );

        for ( int i=0; i<arrayLength; i++ ) {
            newArray[i] = mapFunction.invoke( array[i] );
        }

        return newArray;
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

    public static String toString( Object[] array, String seperator ) {
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

    public static <T> T[] newArray( Class<T> elementType, int length ) {
        return (T[]) Array.newInstance( elementType, length );
    }

    public static <T> T[] flatten( T[]...arrays ) {
        int totalLength = sumLengths(arrays);
        T[] result      = newArray( (Class<T>) arrays[0].getClass().getComponentType(), totalLength );

        int i = 0;
        for ( T[] a : arrays ) {
            for ( int j=0; j<a.length; j++ ) {
                result[i++] = a[j];
            }
        }

        return result;
    }

    private static <T>int sumLengths( T[][] arrays ) {
        int l = 0;

        for ( T[] a : arrays ) {
            l += a.length;
        }

        return l;
    }

}
