package com.mosaic.lang.reflect;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class MethodCall<T> {

    private MethodRef<T> methodRef;
    private T            obj;
    private Object[]     args;

    public MethodCall( MethodRef<T> methodRef, T obj, Object...args ) {
        this( methodRef, obj, Arrays.asList(args) );
    }

    public MethodCall( MethodRef<T> methodRef, T obj, List args ) {
        this.methodRef = methodRef;
        this.obj       = obj;
        this.args      = args.toArray();
    }

    public Object invoke() {
        return methodRef.invokeAgainst( obj, args );
    }

}
