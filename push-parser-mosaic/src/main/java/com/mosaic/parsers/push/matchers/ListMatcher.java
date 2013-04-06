package com.mosaic.parsers.push.matchers;


import com.mosaic.lang.Validate;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * State transitions:
 *
 * prefixMatcher -> (*elementMatcher -> seperatingMatcher)* -> postfixMatcher
 */
class ListMatcher<T> extends Matcher<List<T>> {
    private final Matcher    prefixMatcher;
    private final Matcher<T> elementMatcher;
    private final Matcher    seperatingMatcher;
    private final Matcher    postfixMatcher;

    public ListMatcher( Matcher prefixMatcher, Matcher<T> elementMatcher, Matcher seperatingMatcher, Matcher postfixMatcher ) {
        Validate.notNull( prefixMatcher,     "prefixMatcher" );
        Validate.notNull( elementMatcher,    "elementMatcher" );
        Validate.notNull( seperatingMatcher, "seperatingMatcher" );
        Validate.notNull( postfixMatcher,    "postfixMatcher" );

        this.prefixMatcher     = appendChild( prefixMatcher     );
        this.elementMatcher    = appendChild( elementMatcher    );
        this.seperatingMatcher = appendChild( seperatingMatcher );
        this.postfixMatcher    = appendChild( postfixMatcher    );
    }

    @Override
    protected MatchResult<List<T>> _processInput() {
        List<T> elementsParsed = new ArrayList(10);

        MatchResult<List<T>> prefixResult = prefixMatcher.processInput();
        if ( prefixResult.hasFailedToMatch() ) {
            return createHasFailedStatus( prefixResult );
        } else if ( prefixResult.isIncompleteMatch() ) {
            return createIncompleteMatch();
        }

        MatchResult<List<T>> elementResult = parseElement( elementsParsed );
        if ( !elementResult.hasResult() ) {
            return elementResult;
        }

        do {
            MatchResult seperatingResult = seperatingMatcher.processInput();
            if ( seperatingResult.isIncompleteMatch() ) {
                return createIncompleteMatch();
            } else if ( seperatingResult.hasFailedToMatch() ) {
                MatchResult endResult = postfixMatcher.processInput();
                if ( endResult.hasResult() ) {
                    return createHasResultStatus( elementsParsed );
                } else if ( inputStream.isAtEOS() ) {
                    return createHasFailedStatus( "end of stream reached" );
                }

                return createHasFailedStatus( seperatingResult.getFailedToMatchDescription() );
            } else if ( seperatingResult.isIncompleteMatch() ) {
                return createIncompleteMatch();
            }

            elementResult = parseElement( elementsParsed );
            if ( !elementResult.hasResult() ) {
                return elementResult;
            }
        } while (true);
    }

    private MatchResult<List<T>> parseElement( List<T> elementsParsed) {
        MatchResult<T> elementResult = elementMatcher.processInput();

        if ( elementResult.hasFailedToMatch()) {
            return createHasFailedStatus( elementResult.getFailedToMatchDescription() );
        } else if ( elementResult.isIncompleteMatch() ) {
            return createIncompleteMatch();
        }

        elementsParsed.add( elementResult.getResult() );

        return createHasResultStatus( elementsParsed );
    }

    public String toString() {
        return String.format("list(prefix=%s, element=%s, separator=%s, postfix=%s)", prefixMatcher.toString(),elementMatcher.toString(),seperatingMatcher.toString(),postfixMatcher.toString());
    }

}
