package com.mosaic.collections;


import java.util.Arrays;


/**
 * Less keen to throw index out of bound exceptions that java.util.List.
 */
@SuppressWarnings("unchecked")
public class DynamicList<T> {
    private T[] contents = (T[]) new Object[10];
    private int size;


    public int size() {
        return size;
    }

    public T get( int i ) {
        return size > i ? contents[i] : null;
    }

    public void set( int i, T v ) {
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

        Arrays.fill( contents, null );
    }
}
