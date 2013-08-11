package com.mosaic.parsers.matchers;

import com.mosaic.lang.function.Function1;
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

    public static Matcher commaSeparatedValues( Matcher valueMatcher ) {
        return new SeparatedListMatcher(valueMatcher, ConstantMatcher.create(",") );
    }


    private Matcher valueMatcher;
    private Matcher separatorMatcher;


    private List resultsList = new ArrayList();



    public SeparatedListMatcher( Matcher valueMatcher, Matcher separatorMatcher ) {
        this.valueMatcher     = valueMatcher;
        this.separatorMatcher = separatorMatcher;
    }

    public MatchResult match(CharBuffer buf, boolean isEOS) {
        return MatchResult.continuation(valueMatcher, firstAndThusOptionalValueContinuation);
    }



    private Function1<MatchResult,MatchResult> firstAndThusOptionalValueContinuation = new Function1<MatchResult,MatchResult>() {
        public MatchResult invoke( MatchResult childsResult ) {
            if ( childsResult.isNoMatch() ) {
                return createMatchedResult();
            } else if ( !childsResult.isMatch() ) {
                return childsResult;
            }

            resultsList.add( childsResult.getParsedValue() );

            return MatchResult.continuation(separatorMatcher, separatorContinuation);
        }

        public String toString() {
            return "firstAndThusOptionalValueContinuation";
        }
    };

    private Function1<MatchResult,MatchResult> mandatoryValueContinuation = new Function1<MatchResult,MatchResult>() {
        public MatchResult invoke( MatchResult childsResult ) {
            if ( childsResult.isNoMatch() ) {
                return MatchResult.errored(0, "expected value after '" + separatorMatcher + "'");
            } else if ( !childsResult.isMatch() ) {
                return childsResult;
            }

            resultsList.add( childsResult.getParsedValue() );

            return MatchResult.continuation(separatorMatcher, separatorContinuation);
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
                return MatchResult.continuation(valueMatcher, mandatoryValueContinuation);
            }

            return childsResult;
        }

        public String toString() {
            return "separatorContinuation";
        }
    };

    private MatchResult createMatchedResult() {
        ArrayList clonedResult = new ArrayList(resultsList);
        resultsList.clear();

        return MatchResult.matched(0, clonedResult);
    }

}
