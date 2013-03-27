package com.mosaic.parsers.push.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

/**
 * Throws away the result of another matcher. But fails when that matcher fails.
 */
public class DiscardMatcher<T> extends Matcher<T> {
    private final Matcher<T> wrappedMatcher;

    public DiscardMatcher( Matcher<T> wrappedMatcher ) {
        Validate.notNull( wrappedMatcher, "wrappedMatcher" );

        this.wrappedMatcher = appendChild( wrappedMatcher );
    }

    @Override
    protected MatchResult<T> _processInput() {
        MatchResult<T> result = wrappedMatcher.processInput();

        if ( result.hasResult() ) {
            return createHasResultStatus( null );
        } else {
            return createResultFrom( result );
        }
    }

    public String toString() {
        return String.format("discard(%s)",wrappedMatcher);
    }

}
