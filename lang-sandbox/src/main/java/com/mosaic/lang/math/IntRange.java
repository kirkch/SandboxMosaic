package com.mosaic.lang.math;

import java.io.Serializable;

/**
 *
 */
public class IntRange implements Serializable {
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
}
