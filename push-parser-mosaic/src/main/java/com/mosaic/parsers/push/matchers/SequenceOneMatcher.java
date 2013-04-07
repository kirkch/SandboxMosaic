package com.mosaic.parsers.push.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

/**
 * Works through a sequence of matchers and returns the first of their results. Fails when any of the child matchers fails.
 * Expects only one of the child matchers to return a non-null result, more than one will be ignored.
 * The name 'sequence one' describes the behaviour of matching multiple matchers in order (sequence) and returning only
 * one of their results (one).
 *
 * <table>
 *   <tr><th>wrapped matchers results</th><th>SequenceOneMatcher result</th></tr>
 *   <tr><td>all success</td><<td>success</td>/tr>
 *   <tr><td>any failed</td><<td>failed</td>/tr>
 *   <tr><td>any inprogress</td><<td>inprogress</td>/tr>
 * </table>
 *
 * Child matchers are matched in the order that they are declared, and processing stops on the first child matcher that
 * does not provide a successful result.
 */
public class SequenceOneMatcher<T> extends Matcher<T> {
    private final Matcher[] wrappedMatchers;

    public SequenceOneMatcher( Matcher...wrappedMatchers ) {   // NB no <T> on the wrappedMatchers so that the matcher can support different types, however only the child that returns a real value must support T
        Validate.noNullElements( wrappedMatchers, "wrappedMatchers" );
        Validate.isGTZero( wrappedMatchers.length, "wrappedMatchers.length" );

        this.wrappedMatchers = appendChildren( wrappedMatchers );
    }

    @Override
    protected MatchResult<T> _processInput() {
        T resultValue = null;

        for ( Matcher wrappedMatcher : wrappedMatchers ) {
            MatchResult result = wrappedMatcher.processInput();

            if ( result.hasResult() ) {
                Object v = result.getResult();

                if ( v != null && resultValue == null ) {
                    resultValue = (T) v;   // failure of this cast means that the matcher has not been setup correctly
                }
            } else if ( result.hasFailedToMatch() ) {
                return createHasFailedStatus( result.getFailedToMatchDescription() );
            } else {
                assert result.isIncompleteMatch();

                return createIncompleteMatch();
            }
        }

        return createHasResultStatus( resultValue );
    }

    public String toString() {
        return String.format("sequenceMany(%s)",wrappedMatchers);
    }
}
