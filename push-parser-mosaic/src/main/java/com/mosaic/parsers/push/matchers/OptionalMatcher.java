package com.mosaic.parsers.push.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

/**
 * Makes the wrapped matcher 'optional'. If the wrapped matcher matches then so does this matcher, however if the
 * wrapped matcher fails then this matcher reports success but with a null result.
 *
 * <table>
 *   <tr><th>wrapped matchers result</th><th>optional matcher result</th></tr>
 *   <tr><td>success</td><<td>success</td>/tr>
 *   <tr><td>failed</td><<td>success</td>/tr>
 *   <tr><td>inprogress</td><<td>inprogress</td>/tr>
 * </table>
 */
public class OptionalMatcher<T> extends Matcher<T> {
    private final Matcher<T> wrappedMatcher;

    public OptionalMatcher( Matcher<T> wrappedMatcher ) {
        Validate.notNull( wrappedMatcher, "wrappedMatcher" );

        this.wrappedMatcher = appendChild( wrappedMatcher );
    }

    @Override
    protected MatchResult<T> _processInput() {
        MatchResult<T> result = wrappedMatcher.processInput();

        if ( result.hasFailedToMatch() ) {
            return createHasResultStatus( null );
        } else {
            return createResultFrom( result );
        }
    }

    public String toString() {
        return String.format("optional(%s)",wrappedMatcher);
    }
}
