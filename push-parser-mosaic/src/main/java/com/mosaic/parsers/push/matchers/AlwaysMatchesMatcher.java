package com.mosaic.parsers.push.matchers;

import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;

/**
 * Always reports a match, does not consume any characters.
 */
public class AlwaysMatchesMatcher extends Matcher<String> {

    @Override
    protected MatchResult<String> _processInput() {
        return createHasResultStatus( "" );
    }

    public String toString() {
        return "alwaysMatches()";
    }

}
