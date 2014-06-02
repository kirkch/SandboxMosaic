package com.mosaic.collections;


import java.util.Arrays;


/**
 * Less keen to throw index out of bound exceptions that java.util.List.
 */
@SuppressWarnings("unchecked")
public class DynamicArrayObject<T> {
    private T[] contents;
    private int size;

    public DynamicArrayObject() {
        this(10);
    }

    public DynamicArrayObject( int initalListSize ) {
        this.contents = (T[]) new Object[initalListSize];
    }


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
