package com.mosaic.collections;


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;


/**
 * Less keen to throw index out of bound exceptions that java.util.List.
 */
@SuppressWarnings("unchecked")
public class DynamicArrayObject<T> implements Iterable<T> {
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

    public T[] toArray( Class<T> elementType ) {
        T[] array = (T[]) Array.newInstance( elementType, size );

        for ( int i=0; i<size; i++ ) {
            array[i] = contents[i];
        }

        return array;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;

            public boolean hasNext() {
                return index < size;
            }

            public T next() {
                return contents[index++];
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
