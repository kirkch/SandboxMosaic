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

    public static <T> List<T> asList( Iterable<T> it ) {
        List<T> list = new ArrayList();

        for ( T v : it ) {
            list.add( v );
        }

        return list;
    }

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

    public static <B> List<B> map( CharSequence str, Function1<Character,B> mappingFunction ) {
        List<B> results = new ArrayList( str.length() );

        for ( int i=0; i<str.length(); i++ ) {
            results.add( mappingFunction.invoke(str.charAt(i)) );
        }

        return results;
    }

    public static <A,B> List<B> map( Collection<A> list, Function1<A,B> mappingFunction ) {
        List<B> results = new ArrayList( list.size() );

        for ( A v : list ) {
            results.add( mappingFunction.invoke(v) );
        }

        return results;
    }

    public static <A,B> List<B> map( Iterable<A> list, Function1<A,B> mappingFunction ) {
        List<B> results = new ArrayList();

        for ( A v : list ) {
            results.add( mappingFunction.invoke(v) );
        }

        return results;
    }

    public static <T> List<T> flatten( List<T>...lists ) {
        return flatten( Arrays.asList(lists) );
    }

    public static <T, C extends List<T>> List<T> flatten( List<C> list ) {
        List<T> results = new ArrayList(list.size()*2);

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
    public static <T,V> V fold( Iterable<T> list, V firstValue, Function2<V, T, V> op ) {
        V valueSoFar = firstValue;

        for ( T e : list ) {
            valueSoFar = op.invoke(valueSoFar, e);
        }

        return valueSoFar;
    }

    public static <T,V> V fold( T[] array, V firstValue, Function2<V, T, V> op ) {
        return fold( Arrays.asList(array), firstValue, op );
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



    /**
     * Groups all of the elements of collection that match the predicate into
     * the first list of the returned Pair.  Everything else goes into the second.
     */
    public static <T> Tuple2<List<T>, List<T>> partition( Iterable<T> collection, Function1<T, Boolean> predicate ) {
        List<T> matches = new ArrayList<T>();
        List<T> misses  = new ArrayList<T>();

        for ( T v : collection ) {
            if ( predicate.invoke(v) ) {
                matches.add(v);
            } else {
                misses.add(v);
            }
        }

        return new Tuple2( matches, misses );
    }

    public static <T> T[] toArray( Class<T> type, List<T> list ) {
        int numElements = list.size();

        T[] array = ArrayUtils.newArray( type, numElements );

        int i=0;
        for ( T e : list ) {
            array[i++] = e;
        }

        return array;
    }
}
