package com.mosaic.lang;

import java.util.Iterator;

/**
 * A data source that pulls data as it needs them one at a time. This class provides the interface for supporting
 * the Java for loop.
 */
public abstract class PullDataSource<T> implements Iterable<T>, Iterator<T> {

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return PullDataSource.this.hasNext();
            }

            @Override
            public T next() {
                return PullDataSource.this.next();
            }

            @Override
            public void remove() {
                PullDataSource.this.remove();
            }
        };
    }

}
