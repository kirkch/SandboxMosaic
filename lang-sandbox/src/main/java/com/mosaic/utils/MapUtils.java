package com.mosaic.utils;

import com.mosaic.lang.QA;
import com.mosaic.lang.QA;

import java.util.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class MapUtils {

    /**
     * Util for creating hash maps.  Offers convenience over type safety.
     *
     * @param kvPairs an array of key/value pairs. For example ["a",1,"b",2] would
     *                create a hash map containing key a mapping to value 1 and
     *                key b mapping to value 2.
     */
    public static <K,V> Map<K,V> asMap( Object...kvPairs ) {
        QA.isTrue( kvPairs.length % 2 == 0, "kvPairs must be an even length of kv encoded pairs: %s", Arrays.asList( kvPairs ) );

        Map result = new HashMap();
        for ( int i=0; i<kvPairs.length; i+=2 ) {
            result.put( kvPairs[i], kvPairs[i+1] );
        }

        return (Map<K,V>) result;
    }

    public static <K,V> Map<K,V> asLinkedMap( Object...kvPairs ) {
        QA.isTrue( kvPairs.length % 2 == 0, "kvPairs must be an even length of kv encoded pairs: %s", Arrays.asList( kvPairs ) );

        Map result = new LinkedHashMap();
        for ( int i=0; i<kvPairs.length; i+=2 ) {
            result.put( kvPairs[i], kvPairs[i+1] );
        }

        return (Map<K,V>) result;
    }

    /**
     * Append the specified V to a list of values under the specified K.  If the
     * key is not in the map, then create a new list for it.
     */
    public static <K,V> void appendToListElement( Map<K,List<V>> map, K k, V v ) {
        List<V> list = map.get(k);

        if ( list == null ) {
            list = new ArrayList();
            map.put(k, list);
        }

        list.add( v );
    }

    public static <K,V> Map<K, V> toTreeMap( Map<K,V> source ) {
        Map<K,V> result = new TreeMap();

        for ( Map.Entry<K,V> e : source.entrySet() ) {
            result.put( e.getKey(), e.getValue() );
        }

        return result;
    }
}
