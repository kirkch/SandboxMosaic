package com.mosaic.parsers.matchers;


import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;

import java.lang.reflect.Method;
import java.nio.CharBuffer;

/**
 *
 */
public class NullMatcher implements Matcher {
    public static final Matcher INSTANCE = new NullMatcher();


    private static MatchResult NULL_MATCH = MatchResult.matched(0, null);

    private NullMatcher() {}

    public MatchResult match(CharBuffer buf, boolean isEOS) {
        return NULL_MATCH;
    }

    @Override
    public MatchResult match(String str, boolean isEOS) {
        return null;
    }

    public Matcher withName(String name) {
        throw new UnsupportedOperationException("immutable");
    }

    public Matcher withCallback(String callbackMethodName) {
        throw new UnsupportedOperationException("immutable");
    }

    public String getName() {
        return null;
    }

    public String getCallbackMethodName() {
        return null;
    }

    @Override
    public Method resolveCallbackMethod(Object targetInstance) {
        return null;
    }

}
