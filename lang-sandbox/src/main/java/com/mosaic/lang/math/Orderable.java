package com.mosaic.lang.math;

import com.mosaic.lang.Immutable;

import java.io.Serializable;

/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class Orderable <T extends Orderable> implements Comparable<T>, Serializable, Immutable {
    private static final long serialVersionUID = 3657918383685576118L;

    public T max( T b ) {
        return this.compareTo(b) >= 0 ? (T) this : b;
    }

    public T min( T b ) {
        return this.compareTo(b) <= 0 ? (T) this : b;
    }

    public boolean isGT( T b ) {
        return this.compareTo(b) > 0;
    }

    public boolean isGTE( T b ) {
        return this.compareTo(b) >= 0;
    }

    public boolean isLT( T b ) {
        return this.compareTo(b) < 0;
    }

    public boolean isLTE( T b ) {
        return this.compareTo(b) <= 0;
    }
}
