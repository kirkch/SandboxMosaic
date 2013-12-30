package com.mosaic.collections;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * An optimised FILO stack.  Approximately 50x faster than java.util.Stack and
 * 5x faster than java.util.ArrayList.
 */
@SuppressWarnings("unchecked")
public class FastStack<T> {

    private T[] array;
    private int position = 0;

    public FastStack() {
        this(10);
    }

    public FastStack( int initialSize ) {
        this( (Class<T>) Object.class, initialSize );
    }

    public FastStack( Class<T> type, int initialSize ) {
        this.array = (T[]) Array.newInstance(type, initialSize);
    }

    public void push( T o ) {
        if ( position >= array.length ) {
            this.array = Arrays.copyOf(array, array.length*2);
        }

        array[position++] = o;
    }

    public T pop() {
        T o = array[--position];

        array[position] = null;    // NB costs apx .6ns

        return o;
    }

    public T peek() {
        return array[position-1];
    }

    public void clear() {
        for ( int i=0; i<position; i++ ) {   // faster than Arrays.fill (as of Java 1.7.0_21 OSX)
            array[i] = null;
        }

        this.position = 0;
    }

    public boolean isEmpty() {
        return position == 0;
    }

    public boolean hasContents() {
        return !isEmpty();
    }

    public int size() {
        return position;
    }

    public ConsList<T> popAll() {
        ConsList<T> list = ConsList.Nil;

        while ( this.hasContents() ) {
            list = list.cons( this.pop() );
        }

        return list;
    }

}
