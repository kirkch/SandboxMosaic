package com.mosaic.lang.reflect;

import com.mosaic.lang.reflect.JavaMethod;

/**
 *
 */
public interface Aspect {
    public Object intercepted( Object wrappedObject, Object[] args, JavaMethod m );
}
