package com.mosaic.parsers.matchers;

import com.mosaic.lang.functional.Function1;
import com.mosaic.parsers.BaseMatcher;
import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Must match each of the supplied child matchers. At which point each of the
 * childrens parsed values will be returned in a list.  If the child is marked
 * as skippable then its parsed value will be skipped but the matcher itself
 * must still match.
 */
@SuppressWarnings("unchecked")
public class AndMatcher extends BaseMatcher {

    private Matcher[] children;
    private int nextChildIndex;

    private List matchedValues = new ArrayList();

    public AndMatcher( Matcher... children ) {
        if ( children == null || children.length == 0 ) {
            throw new IllegalArgumentException("AndMatcher does not support having no child matchers");
        }

        this.children = children;
    }

    public MatchResult match( CharBuffer buf, boolean isEOS ) {
        nextChildIndex = 1;

        matchedValues.clear();

        return MatchResult.continuation( children[0], childContinuation );
    }


    private Function1<MatchResult,MatchResult> childContinuation = new Function1<MatchResult,MatchResult>() {
        public MatchResult invoke( MatchResult result ) {
            if ( result.isMatch() ) {
                if ( result.hasParsedValue() ) {
                    matchedValues.add( result.getParsedValue() );
                }

                if ( nextChildIndex < children.length ) {
                    Matcher nextMatcher = children[nextChildIndex];
                    nextChildIndex += 1;

                    return MatchResult.continuation(nextMatcher, childContinuation);
                }

                return MatchResult.matched(0, new ArrayList(matchedValues) );
            }

            return result;
        }
    };

}
