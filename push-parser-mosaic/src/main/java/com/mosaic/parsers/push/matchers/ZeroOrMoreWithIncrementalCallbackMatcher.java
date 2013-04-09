package com.mosaic.parsers.push.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

/**
 * Matches a wrapped matcher zero or more times. Each match will consume the characters from the character stream and
 * generate a callback event for extra processing. Each match will be processed individually without the matcher holding
 * on to the result, as such this matcher will always return null on success. It is also important that any parent
 * matcher does not roll back the character stream past the point where events have been generated. As such this parser
 * disables rollback support for itself and each of its parent matchers.<p/>
 *
 * The target use case for this matcher is parsing an unbounded number of identical statements without having to hold them
 * all in memory, such as rows in a CSV file, json elements in an array or xml tags representing a list (think books
 * in a library, parts in an aircraft or tv shows aired in the US over the last month etc).
 *
 * <table>
 *   <tr><th>wrapped matchers result</th><th>optional matcher behaviour</th></tr>
 *   <tr><td>success</td><<td>generate callback and consume characters up to this point in the character stream (illegal to rollback past this point)</td>/tr>
 *   <tr><td>failed</td><<td>completes this matcher, rolls back to the end of the last successful match (or start point of this matcher if no matches have been found) and reports success with a null result value</td>/tr>
 *   <tr><td>inprogress</td><<td>reports inprogress</td>/tr>
 * </table>
 */
public class ZeroOrMoreWithIncrementalCallbackMatcher<T> extends Matcher<Void> {

    private final Matcher<T>            wrappedMatcher;
    private final ZeroOrMoreCallback<T> callback;

    private boolean hasSentStartCallback;

    public ZeroOrMoreWithIncrementalCallbackMatcher( Matcher<T> wrappedMatcher, ZeroOrMoreCallback<T> callback ) {
        super( false );

        Validate.notNull( wrappedMatcher, "wrappedMatcher" );
        Validate.notNull( callback,       "callback"       );

        this.wrappedMatcher = appendChild( wrappedMatcher );
        this.callback       = callback;
    }

    @Override
    protected MatchResult<Void> _processInput() {
        String descriptiveMatcherName = getDescriptiveName();

        int lineNumber = inputStream.getLineNumber();
        MatchResult<T> matchResult = wrappedMatcher.processInput();

        if ( matchResult.hasResult() && !hasSentStartCallback ) {
            callback.startOfBlockReceived( lineNumber );
            hasSentStartCallback = true;
        }

        while ( matchResult.hasResult() ) {
            T value = matchResult.getResult();

            callback.valueReceived( lineNumber, value );
            inputStream.markNonRollbackablePoint( descriptiveMatcherName, "callback has processed the characters" );

            lineNumber = inputStream.getLineNumber();
            matchResult = wrappedMatcher.processInput();
        }

        if ( matchResult.hasFailedToMatch() ) {
            if ( hasSentStartCallback ) {
                callback.endOfBlockReceived( lineNumber );
                hasSentStartCallback = false;
            }

            return createHasResultStatus( null );
        } else {
            assert matchResult.isIncompleteMatch();

            return createIncompleteMatch();
        }
    }

    public String toString() {
        return String.format("zeroOrMoreWithIncrementalCallback(%s)", wrappedMatcher);
    }

}
