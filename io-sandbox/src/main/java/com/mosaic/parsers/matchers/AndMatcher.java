package com.mosaic.parsers.matchers;

import com.mosaic.lang.function.Function1;
import com.mosaic.parsers.BaseMatcher;
import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;

import java.nio.CharBuffer;

/**
 *
 */
public class AndMatcher extends BaseMatcher {

    private Matcher[] children;
    private int nextChildIndex;

    public AndMatcher( Matcher... children ) {
        if ( children == null || children.length == 0 ) {
            throw new IllegalArgumentException("AndMatcher does not support having no child matchers");
        }

        this.children = children;
    }

    public MatchResult match( CharBuffer buf, boolean isEOS ) {
        nextChildIndex = 1;

        return MatchResult.continuation( children[0], childContinuation );
    }


    private Function1<MatchResult,MatchResult> childContinuation = new Function1<MatchResult,MatchResult>() {
        public MatchResult invoke( MatchResult result ) {
            if ( result.isMatch() && nextChildIndex < children.length ) {
                Matcher nextMatcher = children[nextChildIndex];
                nextChildIndex += 1;

                return MatchResult.continuation(nextMatcher, childContinuation);
            }

            return result;
        }
    };

}
