package com.mosaic.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 *
 */
public class PropertyUtils {

    public static Map<String, String> processProperties( Properties props ) {
        Map<String,String> processedProps = stripComments(props);

        return Collections.unmodifiableMap( processedProps );
    }

    private static Map<String, String> stripComments( Properties props ) {
        Map<String,String> out = new HashMap<>( props.size()*2 );

        Set<Map.Entry<Object,Object>> entries = props.entrySet();
        for ( Map.Entry<Object,Object> e : entries ) {
            String value = e.getValue().toString();
            String withoutComment = StringUtils.upto( value, '#' ).trim();

            out.put( e.getKey().toString(), withoutComment );
        }

        return out;
    }

}
