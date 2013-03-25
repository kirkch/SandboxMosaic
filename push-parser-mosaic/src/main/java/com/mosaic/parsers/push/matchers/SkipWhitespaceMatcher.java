package com.mosaic.parsers.push.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

/**
 *
 */
public class SkipWhitespaceMatcher<T> extends Matcher<T> {

    private final Matcher<T> wrappedMatcher;

    public SkipWhitespaceMatcher( Matcher<T> wrappedMatcher ) {
        Validate.notNull( wrappedMatcher, "wrappedMatcher" );

        this.wrappedMatcher = appendChild( wrappedMatcher );
    }

    @Override
    protected MatchResult<T> _processInput() {
        int whitespaceCount = countWhitespace();

        if ( whitespaceCount > 0 ) {
            inputStream.skipCharacters( whitespaceCount );
        }

        return createResultFrom( wrappedMatcher.processInput() );
    }

    private int countWhitespace() {
        int maxIndex = inputStream.length();

        int count;

        for ( count=0; count < maxIndex && Character.isWhitespace(inputStream.charAt(count)); count++ ) {}

        return count;
    }

}
