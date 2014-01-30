package com.mosaic.lang.functional;

import java.util.Iterator;

/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class Stream<T> implements Iterable<T> {

    public static Stream EMPTY = new EmptyStream();

    public static <T> Stream<T> empty() {
        return EMPTY;
    }

    /**
     * Delays the creation of the tail until the tail is requested.
     */
    public static <T> Stream<T> create( T head, Function0<Stream<T>> tail ) {
        return new ValueStream( head, tail );
    }



    public abstract boolean isEmpty();

    public abstract T head();

    public abstract Stream<T> tail();


    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Stream<T> next = Stream.this;

            public boolean hasNext() {
                return next.notEmpty();
            }

            public T next() {
                T v = next.head();

                next = next.tail();

                return v;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    public boolean notEmpty() {
        return !isEmpty();
    }

    private static class EmptyStream<T> extends Stream<T> {
        public boolean isEmpty() {
            return true;
        }

        public T head() {
            throw new IllegalStateException( "Empty Stream" );
        }

        public Stream<T> tail() {
            throw new IllegalStateException( "Empty Stream" );
        }
    }

    private static class ValueStream<T> extends Stream<T> {
        private T                    head;
        private Function0<Stream<T>> tailFetcher;

        private transient Stream<T>  tail;

        public ValueStream( T head, Function0<Stream<T>> tail ) {
            this.head        = head;
            this.tailFetcher = tail;
        }

        public boolean isEmpty() {
            return false;
        }

        public T head() {
            return head;
        }

        public Stream<T> tail() {
            if ( tail == null ) {
                tail = tailFetcher.invoke();
            }

            return tail;
        }
    }
}
