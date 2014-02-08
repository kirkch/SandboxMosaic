package com.mosaic.collections;

import com.mosaic.lang.Validate;

import java.util.AbstractSet;
import java.util.Iterator;


/**
 * Stores a set of unique objects.  Unique is defined by object identity (==).
 */
@SuppressWarnings("unchecked")
public class IdentitySet<T> extends AbstractSet<T> {

    private Object[] array;
    private int contentsCount;


    public IdentitySet() {
        this(10);
    }

    public IdentitySet( int size ) {
        array = new Object[size];
    }

    public void clear() {
        for ( int i=0; i<contentsCount; i++ ) {
            array[i] = null;
        }

        contentsCount = 0;
    }

    public boolean add( T v ) {
        for ( int i=0; i<contentsCount; i++ ) {
            if ( array[i] == v ) {
                return false;
            }
        }

        if ( contentsCount == array.length ) {
            Object[] newArray = new Object[array.length*2];

            System.arraycopy( array, 0, newArray, 0, array.length );

            array = newArray;
        }

        array[contentsCount++] = v;

        return true;
    }

    public boolean contains( Object v ) {
        for ( int i=0; i<contentsCount; i++ ) {
            if ( array[i] == v ) {
                return true;
            }
        }

        return false;
    }

    public boolean remove( Object o ) {
        for ( int i=0; i<contentsCount; i++ ) {
            if ( array[i] == o ) {
                removeElementAt(i);

                return true;
            }
        }

        return false;
    }

    private void removeElementAt(int i) {
        if ( i == contentsCount-1 ) {
            array[i] = null;
        } else {
            System.arraycopy( array, i+1, array, i, contentsCount-i-1 );
            array[contentsCount-1] = null;
        }

        contentsCount--;
    }


    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int pos = 0;


            public boolean hasNext() {
                return pos < contentsCount;
            }

            public T next() {
                Validate.isTrue( hasNext(), "the iterator is empty, call hasNext() first" );

                return (T) array[pos++];
            }

            public void remove() {
                removeElementAt(pos);
            }
        };
    }

    public int size() {
        return contentsCount;
    }

}
