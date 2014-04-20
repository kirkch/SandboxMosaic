package com.mosaic.lang.math;

import java.io.Serializable;
import java.util.Iterator;


/**
 *
 */
public class IntRange implements Serializable, Iterable<Integer> {
    private static final long serialVersionUID = 1290455330488L;

    private int min;
    private int maxExc;

    public IntRange() {}
    public IntRange( int min, int maxExc ) {
        this.min    = min;
        this.maxExc = maxExc;
    }

    public boolean contains( int v ) {
        return min <= v && v < maxExc;
    }

    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int next = min;

            public boolean hasNext() {
                return next < maxExc;
            }

            public Integer next() {
                int v = next;

                next += 1;

                return v;
            }

            public void remove() {
            }
        };
    }
}
