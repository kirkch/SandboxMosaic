package com.mosaic.lang.reflect;

import com.mosaic.lang.Immutable;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import static com.mosaic.lang.Validate.notNull;

/**
 *
 */
public class JavaConstructor<T> implements Serializable, Immutable {
    private static final long serialVersionUID = 1287987032600L;


    public static <T> JavaConstructor<T> toJavaConstructor(Constructor<T> constructor) {
        return new JavaConstructor<T>( constructor );
    }


    private Constructor<T> constructor;

    protected JavaConstructor() {}
    public JavaConstructor(Constructor<T> constructor) {
        notNull( constructor, "constructor" );

        this.constructor = constructor;
    }

    public T newInstance( Object...args ) {
        try {
            return constructor.newInstance(args);
        } catch ( Exception e ) {
            throw ReflectionException.recast(e);
        }
    }
}
