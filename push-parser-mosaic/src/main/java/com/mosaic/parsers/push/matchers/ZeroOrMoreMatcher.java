package com.mosaic.parsers.push.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ZeroOrMoreMatcher<T> extends Matcher<List<T>> {

    private final Matcher<T> wrappedMatcher;

    public ZeroOrMoreMatcher( Matcher<T> wrappedMatcher ) {
        Validate.notNull( wrappedMatcher, "wrappedMatcher" );

        this.wrappedMatcher = appendChild( wrappedMatcher );
    }

    @Override
    protected MatchResult<List<T>> _processInput() {
        List<T> results = new ArrayList(10);

        MatchResult<T> matchResult = wrappedMatcher.processInput();
        while ( matchResult.hasResult() ) {
            results.add( matchResult.getResult() );

            matchResult = wrappedMatcher.processInput();
        }

        if ( matchResult.hasFailedToMatch() ) {
            return createHasResultStatus( results );
        } else {
            assert matchResult.isIncompleteMatch();

            return createIncompleteMatch();
        }
    }

}
