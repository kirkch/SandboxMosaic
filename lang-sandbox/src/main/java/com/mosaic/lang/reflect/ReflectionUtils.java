package com.mosaic.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ReflectionUtils {

    public static Object invoke( Object instance, Method method, Object...args ) {
        try {
            return method.invoke( instance, args );
        } catch ( Throwable e ) {
            throw ReflectionException.recast(e);
        }
    }

    public static Method findFirstPublicMethodByName( Class c, String methodName ) {
        for ( Method m : c.getMethods() ) {
            if ( m.getName().equals(methodName) ) {
                return m;
            }
        }

        return null;
    }

    public static Method findFirstInstanceMethodByName( Class c, String methodName ) {
        for ( Method m : c.getDeclaredMethods() ) {
            if ( m.getName().equals(methodName) ) {
                return m;
            }
        }

        return null;
    }

    public static <T> T getPrivateField( Object o, String fieldName ) {
        try {
            Field f = o.getClass().getDeclaredField( fieldName );
            f.setAccessible( true );

            return (T) f.get( o );
        } catch ( Exception e ) {
            throw ReflectionException.recast( e );
        }
    }
}
