package com.mosaic.parsers;

import com.mosaic.lang.reflect.ReflectionUtils;

import java.lang.reflect.Method;
import java.nio.CharBuffer;

/**
 *
 */
public abstract class BaseMatcher implements Matcher {

    private String  name;
    private String  callbackMethodName;
    private boolean requestParentToKeepParsedValue = true;

    private transient Method lazilyCachedCallbackMethod;
    private transient int    bufferIndexFromPreviousCall = -1;

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
            lazilyCachedCallbackMethod = ReflectionUtils.findFirstMethodByName( targetInstance.getClass(), callbackMethodName );

            lazilyCachedCallbackMethod.setAccessible(true);
        }

        return lazilyCachedCallbackMethod;
    }

    public int getBufferIndexFromPreviousCall() {
        return bufferIndexFromPreviousCall;
    }

    public void setBufferIndexFromPreviousCall( int pos ) {
        this.bufferIndexFromPreviousCall = pos;
    }


    public boolean shouldParentKeepParsedValueOnMatch() {
        return requestParentToKeepParsedValue;
    }

    public Matcher keep() {
        this.requestParentToKeepParsedValue = true;

        return this;
    }

    public Matcher skip() {
        this.requestParentToKeepParsedValue = false;

        return this;
    }


    public String toString() {
        return this.callbackMethodName == null ? this.getClass().getSimpleName() : this.callbackMethodName;
    }

}
