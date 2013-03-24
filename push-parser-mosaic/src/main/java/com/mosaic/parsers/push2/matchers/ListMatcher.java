package com.mosaic.parsers.push2.matchers;


import com.mosaic.io.CharacterStream;
import com.mosaic.lang.Validate;
import com.mosaic.parsers.push2.MatchResult;
import com.mosaic.parsers.push2.Matcher;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * State transitions:
 *
 * *elementMatcher -> seperatingMatcher)* -> endOfListMatcher
 */
//abstract class ListMatcher<T> extends Matcher<T> {
//
//}

class ListMatcher<T> extends Matcher<List<T>> {
    private final Matcher<T> elementMatcher;
    private final Matcher    seperatingMatcher;
    private final Matcher    endOfListMatcher;

    public ListMatcher( Matcher<T> elementMatcher, Matcher seperatingMatcher, Matcher endOfListMatcher ) {
        Validate.notNull( elementMatcher,    "elementMatcher" );
        Validate.notNull( seperatingMatcher, "seperatingMatcher" );
        Validate.notNull( endOfListMatcher,  "endOfListMatcher" );

        this.elementMatcher    = elementMatcher;
        this.seperatingMatcher = seperatingMatcher;
        this.endOfListMatcher  = endOfListMatcher;
    }

    @Override
    public Matcher<List<T>> withInputStream( CharacterStream in ) {
        elementMatcher.withInputStream( in );
        seperatingMatcher.withInputStream( in );
        endOfListMatcher.withInputStream( in );

        return super.withInputStream( in );
    }

    @Override
    protected MatchResult<List<T>> _processInput() {
        List<T> elementsParsed = new ArrayList(10);

        MatchResult<List<T>> elementResult = parseElement( elementsParsed );
        if ( !elementResult.hasResult() ) {
            return elementResult;
        }

        do {
            MatchResult seperatingResult = seperatingMatcher.processInput();
            if ( seperatingResult.isIncompleteMatch() ) {
                return createIncompleteMatch();
            } else if ( seperatingResult.hasFailedToMatch() ) {
                MatchResult endResult = endOfListMatcher.processInput();
                if ( endResult.hasResult() ) {
                    return createHasResultStatus( elementsParsed );
                } else if ( inputStream.isAtEOS() ) {
                    return createHasFailedStatus("end of stream reached");
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

}
