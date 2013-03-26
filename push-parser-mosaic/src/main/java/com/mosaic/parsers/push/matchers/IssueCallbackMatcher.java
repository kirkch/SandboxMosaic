package com.mosaic.parsers.push.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.lang.function.VoidFunction1;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

/**
 * Enhances another matcher by invoking a callback every time the wrapped matcher reports a match. Use to trigger
 * processing of the result matched so far, for example every row of a csv file or XML block.
 */
public class IssueCallbackMatcher<T> extends Matcher<T> {

    private final Matcher<T>       wrappedMatcher;
    private final VoidFunction1<T> callback;

    public IssueCallbackMatcher( Matcher<T> wrappedMatcher, VoidFunction1<T> callback ) {
        Validate.notNull( wrappedMatcher, "wrappedMatcher" );
        Validate.notNull( callback, "callback" );

        this.wrappedMatcher = appendChild( wrappedMatcher );
        this.callback       = callback;
    }

    @Override
    protected MatchResult<T> _processInput() {
        MatchResult<T> result = wrappedMatcher.processInput();

        if ( result.hasResult() ) {
            T value = result.getResult();

            callback.invoke( value );

            return createHasResultStatus( value );
        }

        return createResultFrom( result );
    }

}
