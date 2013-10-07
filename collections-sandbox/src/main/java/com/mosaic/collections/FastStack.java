package com.mosaic.collections;

import java.util.Arrays;

/**
 * An optimised FILO stack.  Approximately 50x faster than java.util.Stack and
 * 5x faster than java.util.ArrayList.
 */
public class FastStack {

    private Object[] array;
    private int      position = 0;

    public FastStack() {
        this(10);
    }

    public FastStack( int initialSize ) {
        this.array = new Object[initialSize];
    }

    public void push( Object o ) {
        if ( position >= array.length ) {
            this.array = Arrays.copyOf(array, array.length*2);
        }

        array[position++] = o;
    }

    public Object pop() {
        Object o = array[--position];

        array[position] = null;    // NB costs apx .6ns

        return o;
    }

    public Object peek() {
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

    public int size() {
        return position;
    }

}
