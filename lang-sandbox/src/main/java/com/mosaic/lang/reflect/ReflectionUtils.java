package com.mosaic.lang.reflect;

import com.mosaic.lang.text.UTF8;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ReflectionUtils {

    // TODO move to using MethodHandle instead (it is faster)
    private static final Method CLONE_METHOD = ReflectionUtils.findFirstInstanceMethodByName( Object.class, "clone" );

    static {
        CLONE_METHOD.setAccessible( true );
    }

    public static <T extends Cloneable> T clone( T obj ) {
        return (T) invoke( obj, CLONE_METHOD );
    }

    public static <T> Constructor<T> findConstructor( Class<T> clazz, Object...args ) {
        Constructor<T>[] candidates = (Constructor<T>[]) clazz.getConstructors();

        for ( Constructor<T> c : candidates ) {
            Class<?>[] parameterTypes = c.getParameterTypes();

            if ( parameterTypes.length == args.length ) {
                return c;
            }
        }

        throw ReflectionException.create( "Unable to find constructor " + clazz.getSimpleName() + "(" + Arrays.asList( args ) + ")" );
    }

    public static <T> T newInstance( UTF8 clazz, Object...args ) {
        Class c = findClass( clazz.toString() );

        return (T) newInstance( c, args );
    }

    public static <T> T newInstance( Class<T> clazz, Object...args ) {
        try {
            Constructor<T> c = findConstructor( clazz, args );

            return c.newInstance( args );
        } catch ( Throwable e ) {
            throw ReflectionException.recast(e);
        }
    }

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
        Class c = o.getClass();

        while ( c != null ) {
            try {
                Field f = c.getDeclaredField( fieldName );
                f.setAccessible( true );

                return (T) f.get( o );
            } catch ( Exception e ) {
                c = c.getSuperclass();
            }
        }

        throw ReflectionException.recast( new NoSuchFieldException(fieldName) );
    }

    public static Class findClass( String className ) {
        try {
            return Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            throw ReflectionException.recast( e );
        }
    }

    public static Class getCallersClass() {
        try {
            return Class.forName( new Exception().getStackTrace()[2].getClassName() );
        } catch ( ClassNotFoundException e ) {
            throw ReflectionException.recast( e );
        }
    }
}
