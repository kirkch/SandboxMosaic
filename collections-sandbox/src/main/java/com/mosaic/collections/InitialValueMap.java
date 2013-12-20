package com.mosaic.collections;

import com.mosaic.lang.functional.Function0;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Factory for creating maps that generate a default value when none exist for
 * a key.
 *
 * It cuts down on the amount of boiler plate similar to:
 *
 * <pre>
 * v = map.get(k);
 * if ( v == null ) {
 *     v = new Something();
 *     map.put(k,v)
 * }
 * </pre>
 */
@SuppressWarnings("unchecked")
public class InitialValueMap {

    public static <K,V> Map<K,Set<V>> identityMapOfSets() {
        return identityMapOfSets(
            new IdentityHashMap<K,Set<V>>(),
            new Function0<Set<V>>() {
                public Set<V> invoke() {
                    return new TreeSet<V>();
                }
            }
        );
    }

    public static <K,V> Map<K,V> identityMapOfSets( Map<K,V> delegate, final Function0<V> defaultValueFactory ) {
        return new DelegatingMap<K,V>( delegate ) {
            public V get(Object key) {
                V v = super.get(key);

                if ( v == null ) {
                    v = defaultValueFactory.invoke();

                    super.put( (K) key, v );
                }

                return v;
            }
        };
    }

}
