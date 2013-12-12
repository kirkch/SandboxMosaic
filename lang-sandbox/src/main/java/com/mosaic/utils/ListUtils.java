package com.mosaic.utils;

import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Function2;
import com.mosaic.lang.functional.Nullable;
import com.mosaic.lang.functional.Tuple2;

import java.util.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ListUtils {

    public static <T> Nullable<T> firstMatch( List<T> list, Function1<T,Boolean> predicateFunction ) {
        for ( T e : list ) {
            if ( predicateFunction.invoke(e) ) {
                return Nullable.createNullable(e);
            }
        }

        return Nullable.NULL;
    }

    public static <A,B> Nullable<B> matchAndMapFirstResult( List<A> list, Function1<A,Nullable<B>> matchAndMapFunction ) {
        for ( A e : list ) {
            Nullable<B> r = matchAndMapFunction.invoke(e);

            if ( r.isNotNull() ) {
                return r;
            }
        }

        return Nullable.NULL;
    }

    public static <A,B> List<B> map( List<A> list, Function1<A,B> mappingFunction ) {
        List<B> results = new ArrayList<B>( list.size() );

        for ( A v : list ) {
            results.add( mappingFunction.invoke(v) );
        }

        return results;
    }

    public static <T, C extends List<T>> List<T> flatten( List<C> list ) {
        List<T> results = new ArrayList<T>(list.size()*2);

        if ( list != null ) {
            for ( List<T> sublist : list ) {
                results.addAll( sublist );
            }
        }

        return results;
    }

    public static <T> List<T> filter( List<T> list, Function1<T,Boolean> predicate ) {
        if ( list != null && list.isEmpty() ) {
            return list;
        }

        List<T> results = new ArrayList<T>( list.size() );

        if ( list != null ) {
            for ( T v : list ) {
                if ( predicate.invoke(v) ) {
                    results.add(v);
                }
            }
        }

        return results;
    }

    public static <T> List<T> filterNot( List<T> list, Function1<T,Boolean> predicate ) {
        if ( list != null && list.isEmpty() ) {
            return list;
        }

        List<T> results = new ArrayList<T>( list.size() );

        if ( list != null ) {
            for ( T v : list ) {
                if ( !predicate.invoke(v) ) {
                    results.add(v);
                }
            }
        }

        return results;
    }

    /**
     * Perform op on every value within the list starting with the specified
     * firstValue.  For example, given the list [1,2,3] and the firstValue 0 then
     * the result would be: 0 op 1 op 2 op 3.  If the list was [], then the result
     * would be the first value, which is 0 in this example.
     */
    public static <T,V> V fold( List<T> list, V firstValue, Function2<V, T, V> op ) {
        V valueSoFar = firstValue;

        for ( T e : list ) {
            valueSoFar = op.invoke(valueSoFar, e);
        }

        return valueSoFar;
    }

    public static <T,G> Map<G,List<T>> groupBy( List<T> list, Function1<T,G> groupByValueFunction ) {
        Map<G,List<T>> result = new HashMap();

        if ( list != null ) {
            for ( T v : list ) {
                G groupedBy = groupByValueFunction.invoke(v);

                MapUtils.appendToListElement( result, groupedBy, v );
            }
        }

        return result;
    }

}
