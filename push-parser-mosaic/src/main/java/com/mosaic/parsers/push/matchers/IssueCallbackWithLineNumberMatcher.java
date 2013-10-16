package com.mosaic.parsers.push.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

/**
 * Enhances another matcher by invoking a callback every time the wrapped matcher reports a match. Use to trigger
 * processing of the result matched so far, for example every row of a csv file or XML block.
 */
public class IssueCallbackWithLineNumberMatcher<T> extends Matcher<T> {

    private final Matcher<T>               wrappedMatcher;
    private final VoidFunction2<Integer,T> callback;

    public IssueCallbackWithLineNumberMatcher( Matcher<T> wrappedMatcher, VoidFunction2<Integer,T> callback ) {
        Validate.notNull( wrappedMatcher, "wrappedMatcher" );
        Validate.notNull( callback, "callback" );

        this.wrappedMatcher = appendChild( wrappedMatcher );
        this.callback       = callback;
    }

    @Override
    protected MatchResult<T> _processInput() {
        int lineNumber = inputStream.getPosition().getLineNumber();

        MatchResult<T> result = wrappedMatcher.processInput();

        if ( result.hasResult() ) {
            T value = result.getResult();

            callback.invoke( lineNumber, value );

            return createHasResultStatus( value );
        }

        return createResultFrom( result );
    }

    public String toString() {
        return String.format("issueCallback(%s)", wrappedMatcher);
    }

}
