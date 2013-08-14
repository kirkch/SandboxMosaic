package com.mosaic.parsers.matchers;

import com.mosaic.lang.function.Function1;
import com.mosaic.parsers.BaseMatcher;
import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Matches (v s*)?.  Useful for repeating lists where the first value differs
 * from the rest.  For example comma separated lists, where subsequent values
 * are prefixed with a comma or csv rows where the first row is categorised
 * as a header.
 */
@SuppressWarnings({"unchecked", "FieldCanBeLocal"})
public class RepeatMatcher extends BaseMatcher {

    private List resultsList = new ArrayList();


    private MatchResult firstMatchResult;
    private MatchResult subsequentMatchResult;


    public RepeatMatcher( Matcher firstValueMatcher, Matcher subsequentValueMatcher ) {
        this.firstMatchResult      = MatchResult.continuation(firstValueMatcher, firstValueContinuation);
        this.subsequentMatchResult = MatchResult.continuation(subsequentValueMatcher, subsequentValueContinuation);
    }


    public MatchResult match( CharBuffer buf, boolean isEOS ) {
        return firstMatchResult;
    }


    private MatchResult createMatchedResult() {
        ArrayList clonedResult = new ArrayList(resultsList);
        resultsList.clear();

        return MatchResult.matched(0, clonedResult);
    }


    private Function1<MatchResult,MatchResult> firstValueContinuation = new Function1<MatchResult, MatchResult>() {
        public MatchResult invoke( MatchResult childsResult ) {
//            if ( childsResult.isNoMatch() ) {
//                return createMatchedResult();
//            } else
            if ( !childsResult.isMatch() ) {
                return childsResult;
            }

            resultsList.add( childsResult.getParsedValue() );

            return subsequentMatchResult;
        }

        public String toString() {
            return "firstValueContinuation";
        }
    };

    private Function1<MatchResult,MatchResult> subsequentValueContinuation = new Function1<MatchResult, MatchResult>() {
        public MatchResult invoke( MatchResult childsResult ) {
            if ( childsResult.isNoMatch() || childsResult.isError() ) {
                return createMatchedResult();
            } else if ( childsResult.isMatch() ) {
                resultsList.add( childsResult.getParsedValue() );

                return subsequentMatchResult;
            }

            return childsResult;
        }

        public String toString() {
            return "subsequentValueContinuation";
        }
    };

}
