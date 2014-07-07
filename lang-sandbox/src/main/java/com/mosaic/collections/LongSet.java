package com.mosaic.collections;

import com.mosaic.io.streams.PrettyPrinter;
import com.mosaic.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Set containing long primitives.  Avoids auto boxing.
 */
public abstract class LongSet {

    public static LongSet factory( long expectedSize ) {
        return new SmallLongSet();
    }

    public static LongSet createLongSet( long...values ) {
        LongSet set = new SmallLongSet();

        for ( long v : values ) {
            set.add( v );
        }

        return set;
    }

    public abstract long size();

    public abstract void add( long v );

    public abstract boolean contains( long v );

    public abstract LongIterator iterator();

    public void appendTo( StringBuilder buf, String separator ) {
        List<Long> listBuf = toList();

        Collections.sort( listBuf );

        StringUtils.join( buf, listBuf, separator );
    }

    private List<Long> toList() {
        List<Long> listBuf = new ArrayList<>();

        LongIterator it = iterator();
        while ( it.hasNext() ) {
            listBuf.add( it.next() );
        }

        return listBuf;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean hasContents() {
        return size() > 0;
    }


    /**
     * Simple implementation that is fast for small sets, but slow for larger.  It does not scale.
     */
    private static class SmallLongSet extends LongSet {
        private long[] array = new long[10];
        private int    size;

        public long size() {
            return size;
        }

        public void add( long v ) {
            if ( contains(v) ) {
                return;
            }

            resizeIfNecessary();

            array[size++] = v;
        }

        public boolean contains( long v ) {
            for ( int i=0; i<size; i++ ) {
                if ( array[i] == v ) {
                    return true;
                }
            }

            return false;
        }

        public LongIterator iterator() {
            return new LongIterator() {
                private int i;

                public boolean hasNext() {
                    return i < size;
                }

                public long index() {
                    return i-1;
                }

                public long next() {
                    if ( !hasNext() ) {
                        throw new IndexOutOfBoundsException( "hasNext() returned false" );
                    }

                    return array[i++];
                }
            };
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();

            for ( int i=0; i<size; i++ ) {
                if ( i > 0 ) {
                    buf.append( ", " );
                }

                buf.append(array[i]);
            }

            return buf.toString();
        }

        private void resizeIfNecessary() {
            if ( size == array.length ) {
                array = Arrays.copyOf( array, array.length*2 );
            }
        }
    }
}
