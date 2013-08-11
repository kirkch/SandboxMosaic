package com.mosaic.lang.reflect;

import java.lang.reflect.Method;

/**
 *
 */
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
}
