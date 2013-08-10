package com.mosaic.parsers.matchers;

import com.mosaic.parsers.BaseMatcher;
import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;

import java.nio.CharBuffer;

/**
 *
 */
public class SeparatedListMatcher extends BaseMatcher {

    public SeparatedListMatcher( Matcher valueMatcher, Matcher separatorMatcher ) {

    }

    public MatchResult match(CharBuffer buf, boolean isEOS) {
        return null;
    }

}
