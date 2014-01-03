package com.mosaic.lang.reflect;

import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public final class ReflectionException extends RuntimeException {
    public static RuntimeException recast( Throwable e, String msg ) {
        return new ReflectionException( e, msg );
    }

    public static RuntimeException recast( Throwable e ) {
        if ( e instanceof RuntimeException ) {
            return (RuntimeException) e;
        } else if ( e instanceof InvocationTargetException ) {
            return recast( e.getCause() );
        } else {
            return new ReflectionException( e );
        }
    }

    private ReflectionException( Throwable e ) {
        super( e );
    }

    private ReflectionException( Throwable e, String msg ) {
        super( msg, e );
    }
}
