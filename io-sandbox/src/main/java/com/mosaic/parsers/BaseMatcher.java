package com.mosaic.parsers;

import com.mosaic.lang.reflect.ReflectionUtils;

import java.lang.reflect.Method;
import java.nio.CharBuffer;

/**
 *
 */
public abstract class BaseMatcher implements Matcher {

    private String name;
    private String callbackMethodName;

    private transient Method lazilyCachedCallbackMethod;

    public Matcher withName( String name ) {
        this.name = name;

        return this;
    }

    public Matcher withCallback( String callbackMethodName ) {
        this.callbackMethodName = callbackMethodName;

        return this;
    }


    public String getName() {
        return name;
    }

    public String getCallbackMethodName() {
        return callbackMethodName;
    }

    public MatchResult match( String str, boolean isEOS ) {
        return match(CharBuffer.wrap(str), isEOS);
    }


    public Method resolveCallbackMethod( Object targetInstance ) {
        if ( lazilyCachedCallbackMethod == null && callbackMethodName != null ) {
            lazilyCachedCallbackMethod = ReflectionUtils.findFirstInstanceMethodByName(targetInstance.getClass(), callbackMethodName);

            lazilyCachedCallbackMethod.setAccessible(true);
        }

        return lazilyCachedCallbackMethod;
    }

}
