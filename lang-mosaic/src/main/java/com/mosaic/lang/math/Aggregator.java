package com.mosaic.lang.math;

/**
 * Represents a mathematical computation that can combine zero to many values into a single end result.
 */
public abstract class Aggregator<T,R> {
    public abstract void append( T v );

    public Aggregator<T,R> appendAll( Iterable<T> values ) {
        for (T v : values) {
            append(v);
        }

        return this;
    }

    public abstract R getResult();
}
