package com.mosaic.utils;

import com.mosaic.lang.Validate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        Validate.isTrue( kvPairs.length % 2 == 0, "kvPairs must be an even length of kv encoded pairs: %s", Arrays.asList(kvPairs) );

        Map result = new HashMap();
        for ( int i=0; i<kvPairs.length; i+=2 ) {
            result.put( kvPairs[i], kvPairs[i+1] );
        }

        return (Map<K,V>) result;
    }

}
