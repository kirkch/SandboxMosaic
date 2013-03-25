package com.mosaic.parsers.push2.matchers;

import com.mosaic.parsers.push2.MatchResult;
import com.mosaic.parsers.push2.Matcher;

/**
 * Always reports a match, does not consume any characters.
 */
public class AlwaysMatchesMatcher extends Matcher<String> {

    @Override
    protected MatchResult<String> _processInput() {
        return createHasResultStatus( "" );
    }

}
