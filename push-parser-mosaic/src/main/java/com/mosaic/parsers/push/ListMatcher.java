package com.mosaic.parsers.push;

import com.mosaic.io.CharPosition;
import com.mosaic.io.Characters;
import com.mosaic.lang.Validate;

/**
 *
 * State transitions:
 *
 * *elementMatcher -> seperatingMatcher)* -> endOfListMatcher
 */
//abstract class ListMatcher<T> extends Matcher<T> {
//
//}

class ListElementMatcher<T> extends Matcher<T> {

    private final Matcher<T> elementMatcherPrototype;
    private final Matcher    seperatingMatcherPrototype;
    private final Matcher    endOfListMatcherPrototype;

    private final Matcher<T> activeElementMatcher;

    public ListElementMatcher( Matcher<T> elementMatcher, Matcher seperatingMatcher, Matcher endOfListMatcher ) {
        this( elementMatcher, elementMatcher, seperatingMatcher, endOfListMatcher, MatcherStatus.<T>createIsParsingStatus(), null, null );

        Validate.notNull( elementMatcher, "elementMatcher" );
        Validate.notNull( seperatingMatcher, "seperatingMatcher" );
        Validate.notNull( endOfListMatcher,  "endOfListMatcher"  );
    }

    private ListElementMatcher( Matcher<T> currentElementMatcher, Matcher<T> elementMatcherPrototype, Matcher seperatingMatcherPrototype, Matcher endOfListMatcherPrototype, MatcherStatus<T> result, Characters remainingBytes, CharPosition startingPosition ) {
        super( result, remainingBytes, startingPosition );

        this.elementMatcherPrototype    = elementMatcherPrototype;
        this.seperatingMatcherPrototype = seperatingMatcherPrototype;
        this.endOfListMatcherPrototype  = endOfListMatcherPrototype;

        this.activeElementMatcher       = currentElementMatcher;
    }

//    private final List<T> resultSoFar;

    @Override
    protected Matcher<T> _processCharacters( Characters in ) {
        Matcher<T> result = activeElementMatcher.processCharacters(in);

        MatcherStatus<T> newStatus;
        if ( result.hasFailedToMatch() ) {
            newStatus = result.getStatus();
//        } else if ( result.hasResult() ) {

        } else {
            newStatus = MatcherStatus.createIsParsingStatus(); //newStatus = result.getStatus();
        }



        return new ListElementMatcher( result, elementMatcherPrototype, seperatingMatcherPrototype, endOfListMatcherPrototype, newStatus, result.getRemainingCharacters(), getStartingPosition() );

//        if ( result.hasFailedToMatch() ) {
//            return result;
//        } else if ( result.isAwaitingInput() ) {
//            return new ListElementMatcher<T>( result, elementMatcherPrototype, seperatingMatcherPrototype, endOfListMatcherPrototype, result.getStatus(), in, getStartingPosition() );
//        }

//        Characters modifiedInput = in.skipWhile( WHITESPACE_PREDICATE );
//
//        if ( modifiedInput.hasContents() && !Character.isWhitespace(modifiedInput.charAt(0)) ) {
//            return wrappedMatcher._processCharacters( modifiedInput );
//        }
//
//        return new SkipWhitespaceMatcher( wrappedMatcher, MatcherStatus.<T>createIsParsingStatus(), modifiedInput, getStartingPosition() );
//        return null;
    }

    @Override
    public Matcher<T> processEndOfStream() {
        if ( this.hasCompleted() ) {
            return this;
        }

        return activeElementMatcher.processEndOfStream();

//        String failedToMatchDescription = "";
//        if ( activeElementMatcher.isAwaitingInput() ) {
////            = activeElementMatcher.getFailedToMatchDescription();
//            failedToMatchDescription = ""
//        }
//
//        MatcherStatus<T> hasFailedStatus          = MatcherStatus.<T>createHasFailedStatus( activeElementMatcher.getStartingPosition(), failedToMatchDescription );
//
//        return new ListElementMatcher<T>( activeElementMatcher, elementMatcherPrototype, seperatingMatcherPrototype, endOfListMatcherPrototype, hasFailedStatus, getRemainingCharacters(), getStartingPosition() );
    }

}
