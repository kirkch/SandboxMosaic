package com.mosaic.lang;

import com.mosaic.utils.IteratorUtils;

import java.util.Arrays;
import java.util.Iterator;

/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class EnhancedIterable<T> implements java.lang.Iterable<T> {

    public abstract boolean isEmpty();



    public static EnhancedIterable EMPTY = new EnhancedIterable() {
        private final Iterator emptyIterator = new Iterator() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Iterator iterator() {
            return emptyIterator;
        }
    };



    public static <T> EnhancedIterable<T> wrap( final Iterable<T> it ) {
        if ( it instanceof EnhancedIterable ) {
            return (EnhancedIterable<T>) it;
        } else if ( it.iterator().hasNext() == false ) {
            return EnhancedIterable.EMPTY;
        }

        return new EnhancedIterable<T>() {
            @Override
            public boolean isEmpty() {
                return it.iterator().hasNext();
            }

            @Override
            public Iterator<T> iterator() {
                return it.iterator();
            }
        };
    }

    public static <T> EnhancedIterable<T> wrapArray( T[] array ) {
        return wrap( Arrays.asList(array) );
    }

    public static <T> EnhancedIterable<T> wrap( T...elements ) {
        return wrap( Arrays.asList(elements) );
    }


    public static <T> EnhancedIterable<T> combine( final Iterable<T>...iterables ) {
        Validate.noNullElements( iterables, "iterables" );

        switch ( iterables.length ) {
            case 0: return EnhancedIterable.EMPTY;
            case 1: return wrap( iterables[0] );
            default:
        }

        return new EnhancedIterable() {
            @Override
            public boolean isEmpty() {
                for ( Iterable it : iterables ) {
                    if ( it.iterator().hasNext() ) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public Iterator iterator() {
                Iterator[] iterators = new Iterator[iterables.length];

                for ( int i=0; i<iterables.length; i++ ) {
                    iterators[i] = iterables[i].iterator();
                }

                return IteratorUtils.combine( iterators );
            }
        };
    }


}
