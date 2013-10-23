package com.mosaic.utils;

import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Nullable;

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

}
