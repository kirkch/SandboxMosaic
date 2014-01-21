package com.mosaic.utils;

import com.mosaic.lang.EnhancedIterable;
import com.mosaic.lang.Validate;

import java.util.Iterator;

/**
 *
 */
@SuppressWarnings("unchecked")
public class IteratorUtils {

    public static <T> Iterator<T> combine( final Iterator<T>...iterators ) {
        Validate.noNullElements( iterators, "iterators" );

        switch ( iterators.length ) {
            case 0: return EnhancedIterable.EMPTY.iterator();
            case 1: return iterators[0];
            default:
        }

        return new Iterator() {
            private int iteratorIndex = 0;

            @Override
            public boolean hasNext() {
                while ( !iterators[iteratorIndex].hasNext() && iteratorIndex < (iterators.length-1) ) {
                    iteratorIndex += 1;
                }

                return iterators[iteratorIndex].hasNext();
            }

            @Override
            public Object next() {
                return iterators[iteratorIndex].next();
            }

            @Override
            public void remove() {
                iterators[iteratorIndex].remove();
            }
        };
    }

}
