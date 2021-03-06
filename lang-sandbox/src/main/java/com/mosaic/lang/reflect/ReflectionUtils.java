package com.mosaic.lang.reflect;

import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.text.UTF8;
import com.mosaic.utils.ArrayUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ReflectionUtils {

    // TODO move to using MethodHandle instead (it is faster)
    private static final Method CLONE_METHOD = ReflectionUtils.findFirstMethodByName( Object.class, "clone" );

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
            return Backdoor.throwException( e );
        }
    }

    public static <T> T invoke( Object instance, Method method, Object...args ) {
        try {
            return (T) method.invoke( instance, args );
        } catch ( Throwable e ) {
            return Backdoor.throwException( e );
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

    public static Method findFirstMethodByName( Class c, String methodName ) {
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

    public static <T> T invokePrivateMethod( Object o, String methodName, Object...args ) {
        Class c = o.getClass();

        Method m = findFirstMethodByName(c, methodName);
        m.setAccessible( true );

        return invoke( o, m, args );
    }

    public static Class findClass( String className ) {
        try {
            return Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            return Backdoor.throwException( e );
        }
    }

    /**
     * Returns the class that invoked the method that called this method.  In other words, the method
     * who called this method wants to know who invoked it.<p/>
     *
     * For example:<p/>
     *
     * A.main called B.foo<p/>
     *
     * within B.foo, it wants to know who called it.  So it calls ReflectionUtils.getCallersClass,
     * and it returns A.
     *
     *
     */
    public static Class getCallersClass() {
        try {
            return Class.forName( new Exception().getStackTrace()[2].getClassName() );
        } catch ( ClassNotFoundException e ) {
            return Backdoor.throwException( e );
        }
    }

    public static <T> T invokePrivateStaticMethod( Class clazz, String methodName, Object...args ) {
        Class[] types = toTypes(args);

        try {
            Method m = clazz.getDeclaredMethod( methodName, types );

            m.setAccessible( true );

            return (T) m.invoke( null, args );
        } catch ( Exception ex ) {
            return Backdoor.throwException( ex );
        }
    }

    public static Method getPrivateMethod( Class clazz, String methodName, Class...argTypes ) {
        try {
            Method m = clazz.getDeclaredMethod( methodName, argTypes );

            m.setAccessible( true );

            return m;
        } catch ( Exception ex ) {
            return Backdoor.throwException( ex );
        }
    }

    public static <T> T invokeStaticMethod( Method m, Object...args ) {
        try {
            return (T) m.invoke( null, args );
        } catch ( Exception ex ) {
            return Backdoor.throwException( ex );
        }
    }

    private static Class[] toTypes( Object[] args ) {
        return ArrayUtils.map( Class.class, args, a -> a == null ? null : a.getClass() );
    }
}
