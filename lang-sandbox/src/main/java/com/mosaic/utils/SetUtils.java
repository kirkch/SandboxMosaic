package com.mosaic.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 *
 */
public class SetUtils {
    public static final Set EMPTY = unmodifiableSet( new HashSet() );

    public static <T> Set<T> asSet( T...elements ) {
        if ( elements == null ) return null;

        Set<T> s = new HashSet<T>();
        for (T element : elements) {
            s.add( element );
        }

        return s;
    }
    
    public static final <T> Set<T> asImmutableSet( T...elements ) {
        return unmodifiableSet( asSet( elements ) );
    }

    public static <T> Set<T> toSet( Iterator<T> it ) {
        Set set = new HashSet();

        while ( it.hasNext() ) {
            set.add( it.next() );
        }

        return set;
    }


}
