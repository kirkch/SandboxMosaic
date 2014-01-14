package com.mosaic.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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


    public static <T extends Comparable<T>> List<T> sort( Set<T> set ) {
        List<T> l = new ArrayList<T>( set.size() );

        l.addAll( set );
        Collections.sort(l);

        return l;
    }
}
