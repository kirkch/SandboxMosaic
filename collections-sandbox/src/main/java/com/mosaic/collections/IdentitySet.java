package com.mosaic.collections;

import com.mosaic.lang.Validate;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Stores a set of unique objects.  Unique is defined by object identity (==).
 */
@SuppressWarnings("unchecked")
public class IdentitySet<T> extends AbstractSet<T> {

    private Object[] array;
    private int contentsCount;
    private int watermark;
    private int bitmask;

    public IdentitySet() {
        this(16);
    }

    private IdentitySet( int initialSize ) {
        initialSize = Math.max( initialSize, 128 );

        array     = new Object[initialSize];
        watermark = initialSize / 4 - 1;   // 2, 4, 8
        bitmask   = 2*initialSize - 1;
    }

    public void clear() {
        Arrays.fill(array, null);

        contentsCount = 0;
    }

    public boolean add( T v ) {
        int hash = v.hashCode();
        int i    = hash & bitmask;


        return addAt(v, i);
    }

    private boolean addAt( Object v, int i ) {
        int max = array.length;
        while ( i < max) {
            Object current = array[i];
            if ( current == null ) {
                array[i] = v;

                contentsCount++;

                if ( contentsCount >= watermark ) {
                    growArray();
                }

                return true;
            } else if ( current == v ) {
                return false;
            }

            i++;
        }

        return addAt(v, 0);
    }

    public boolean contains( Object v ) {
        int hash = v.hashCode();
        int i = hash & bitmask;

        while ( i < array.length ) {
            if ( array[i] == null ) {
                return false;
            } else if ( array[i] == v ) {
                return true;
            }

            i++;
        }

        i=0;
        while ( i < array.length ) {
            if ( array[i] == null ) {
                return false;
            } else if ( array[i] == v ) {
                return true;
            }

            i++;
        }

        return false;
    }


    private void growArray() {
        Object[] oldArray = array;
        Object[] newArray = new Object[array.length*2];

        array = newArray;
        contentsCount = 0;
        watermark = newArray.length * 3 / 4 - 1;

        for ( int i=0; i<oldArray.length; i++ ) {
            if ( oldArray[i] != null ) {
                add((T) oldArray[i]);
            }
        }
    }



    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int pos = 0;
            private int returnedCount = 0;

            public boolean hasNext() {
                return returnedCount < contentsCount;
            }

            public T next() {
                while ( array[pos] == null ) {
                    pos++;
                }

                returnedCount++;

                return (T) array[pos];
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public int size() {
        return contentsCount;
    }

}
