package com.mosaic.parsers.push.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.Function1;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import com.mosaic.utils.ArrayUtils;

/**
 * Matches the first matcher to claim that it has a result.
 */
public class OrMatcher<T> extends Matcher<T> {

    private final Matcher<T>[] candidateMatchers;

    public OrMatcher( Matcher<T>...candidateMatchers ) {
        Validate.notNull( candidateMatchers, "candidateMatchers" );
        Validate.isGTE( candidateMatchers.length, 1, "candidateMatchers.length" );

        this.candidateMatchers = ArrayUtils.mapInline(candidateMatchers, new Function1<Matcher<T>,Matcher<T>>() {
            public Matcher<T> invoke( Matcher<T> canidateMatcher ) {
                return appendChild( canidateMatcher );
            }
        });
    }

    @Override
    protected MatchResult<T> _processInput() {

        for ( Matcher<T> m : candidateMatchers ) {
            MatchResult<T> result = m.processInput();

            if ( result.hasResult() ) {
                return createHasResultStatus( result.getResult() );
            } else if ( result.isIncompleteMatch() ) {
                return createIncompleteMatch();
            }
        }

        return createHasFailedStatus( "expected to match: " + ArrayUtils.makeString(candidateMatchers, " or ") );
    }

    public String toString() {
        return String.format("or(%s)", ArrayUtils.makeString(candidateMatchers, ","));
    }

}
