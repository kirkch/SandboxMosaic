package com.mosaic.parsers.matchers;

import com.mosaic.lang.functional.Function1;
import com.mosaic.parsers.BaseMatcher;
import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unchecked")
public class SeparatedListMatcher extends BaseMatcher {

    private MatchResult firstMatchResult;
    private MatchResult mandatoryValueMatchResult;
    private MatchResult separatorMatchResult;

    private List resultsList = new ArrayList();



    public SeparatedListMatcher( Matcher valueMatcher, Matcher separatorMatcher ) {
        this.firstMatchResult          = MatchResult.continuation(valueMatcher, firstAndThusOptionalValueContinuation);
        this.mandatoryValueMatchResult = MatchResult.continuation(valueMatcher, mandatoryValueContinuation);
        this.separatorMatchResult      = MatchResult.continuation(separatorMatcher, separatorContinuation);
    }

    public MatchResult match(CharBuffer buf, boolean isEOS) {
        return firstMatchResult;
    }



    private Function1<MatchResult,MatchResult> firstAndThusOptionalValueContinuation = new Function1<MatchResult,MatchResult>() {
        public MatchResult invoke( MatchResult childsResult ) {
            if ( childsResult.isNoMatch() ) {
                return createMatchedResult();
            } else

            if ( !childsResult.isMatch() ) {
                return childsResult;
            }

            resultsList.add( childsResult.getParsedValue() );

            return separatorMatchResult;
        }

        public String toString() {
            return "firstAndThusOptionalValueContinuation";
        }
    };

    private Function1<MatchResult,MatchResult> mandatoryValueContinuation = new Function1<MatchResult,MatchResult>() {
        public MatchResult invoke( MatchResult childsResult ) {
            if ( childsResult.isNoMatch() ) {
                return MatchResult.errored(0, "expected value after '" + separatorMatchResult.getNextMatcher() + "'");
            } else if ( !childsResult.isMatch() ) {
                return childsResult;
            }

            resultsList.add( childsResult.getParsedValue() );

            return separatorMatchResult;
        }

        public String toString() {
            return "mandatoryValueContinuation";
        }
    };

    private Function1<MatchResult,MatchResult> separatorContinuation = new Function1<MatchResult,MatchResult>(){
        public MatchResult invoke(MatchResult childsResult) {
            if ( childsResult.isNoMatch() ) {
                return createMatchedResult();
            } else if ( childsResult.isMatch() ) {
                return mandatoryValueMatchResult;
            }

            return childsResult;
        }

        public String toString() {
            return "separatorContinuation";
        }
    };

    private MatchResult createMatchedResult() {
        if ( resultsList.isEmpty() ) {
            return MatchResult.noMatch();
        }

        List clonedResult = new ArrayList(resultsList);
        resultsList.clear();

        return MatchResult.matched(0, clonedResult);
    }

}
