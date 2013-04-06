package com.mosaic.parsers.push.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

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
    protected MatchResult<String> _processInput() {
        int targetStringLength = targetString.length();
        int streamLength       = inputStream.length();

        if ( streamLength < targetStringLength ) {
            if ( targetString.startsWith(inputStream.toString()) && !inputStream.hasReceivedEOS() ) {
                return createIncompleteMatch();
            } else {
                return createHasFailedStatus( "expected '%s'", targetString );
            }
        } else if ( inputStream.startsWith(targetString) ) {
            inputStream.skipCharacters( targetStringLength );

            return createHasResultStatus( targetString );
        } else {
            return createHasFailedStatus( "expected '%s'", targetString );
        }
    }

    public String toString() {
        String formattedTargetStr = targetString.replaceAll("\r","\\\\r").replaceAll("\n","\\\\n");

        return "'"+formattedTargetStr+"'";
//        return String.format("constant(%s)".format(targetString.replaceAll("\r","\\\\r").replaceAll("\n","\\\\n")));
    }

}