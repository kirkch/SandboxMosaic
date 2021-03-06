package com.mosaic.lang.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *
 */
@SuppressWarnings("unchecked")
public class MethodRef<T> {

    public static <T> MethodRef<T> create( Class<T> c, String methodName, Class...argTypes ) {
        try {
            Method m = c.getMethod( methodName, argTypes );

            return new MethodRef( m );
        } catch ( NoSuchMethodException e ) {
            throw ReflectionException.recast( e );
        }
    }


    private Method method;

    public MethodRef( Method m ) {
        method = m;
    }

    public Object invokeAgainst( T o, Object...args ) {
        try {
            return method.invoke( o, args );
        } catch ( Exception e ) {
            throw ReflectionException.recast( e, "args = " + Arrays.asList(args) );
        }
    }

    public boolean isParamType( Class c, int i ) {
        return method.getParameterTypes()[i].equals( c );
    }

    public String toString() {
        return method.getName();
    }

}
