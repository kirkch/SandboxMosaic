package com.mosaic.parsers.push.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Works through a sequence of matchers and returns all of their results. Fails when any of the child matchers fails.
 * The results of this matcher are returned in a list that is index matched with its child matchers, thus the result
 * for child 2 will be in index 2 of the results. The name 'sequence many' describes the behaviour of matching multiple
 * matchers in order (sequence) and returning all of their results (many).
 *
 * <table>
 *   <tr><th>wrapped matchers results</th><th>SequenceManyMatcher result</th></tr>
 *   <tr><td>all success</td><<td>success</td>/tr>
 *   <tr><td>any failed</td><<td>failed</td>/tr>
 *   <tr><td>any inprogress</td><<td>inprogress</td>/tr>
 * </table>
 *
 * Child matchers are matched in the order that they are declared, and processing stops on the first child matcher that
 * does not provide a successful result.
 */
public class SequenceManyMatcher<T> extends Matcher<List<T>> {
    private final Matcher<T>[] wrappedMatchers;

    public SequenceManyMatcher( Matcher<T>...wrappedMatchers ) {
        Validate.noNullElements( wrappedMatchers, "wrappedMatchers" );
        Validate.isGTZero( wrappedMatchers.length, "wrappedMatchers.length" );

        this.wrappedMatchers = appendChildren( wrappedMatchers );
    }

    @Override
    protected MatchResult<List<T>> _processInput() {
        int     numMatchers  = wrappedMatchers.length;
        List<T> resultValues = new ArrayList(numMatchers);

        for ( int i=0; i<numMatchers; i++ ) {
            MatchResult<T> result = wrappedMatchers[i].processInput();

            if ( result.hasResult() ) {
                resultValues.add( i, result.getResult() );
            } else if ( result.hasFailedToMatch() ) {
                return createHasFailedStatus( result.getFailedToMatchDescription() );
            } else {
                assert result.isIncompleteMatch();

                return createIncompleteMatch();
            }
        }

        return createHasResultStatus( resultValues );
    }

    public String toString() {
        return String.format("sequenceMany(%s)",wrappedMatchers);
    }
}
