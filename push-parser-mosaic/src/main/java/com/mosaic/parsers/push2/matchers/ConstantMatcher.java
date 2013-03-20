package com.mosaic.parsers.push2.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.parsers.push2.MatchResult;
import com.mosaic.parsers.push2.Matcher;

/**
 *
 */
public class ConstantMatcher extends Matcher<String> {

    private final String targetString;

    public ConstantMatcher( String targetString ) {
        Validate.isGTZero( targetString.length(), "targetString.length()" );

        this.targetString = targetString;
    }

    @Override
    public MatchResult<String> processInput() {
        int targetStringLength = targetString.length();

        if ( inputStream.startsWith(targetString) ) {
            inputStream.skipCharacters( targetStringLength );

            return createHasResultStatus( targetString );
        } else {
            return createHasFailedStatus( "expected '%s'", targetString );
        }
    }

}