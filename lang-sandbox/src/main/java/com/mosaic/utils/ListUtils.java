package com.mosaic.utils;

import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    public static <T> List<T> flatten( List<List<T>> list ) {
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

}
