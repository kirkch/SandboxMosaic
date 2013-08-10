package com.mosaic.parsers.matchers;

import com.mosaic.parsers.BaseMatcher;
import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;

import java.nio.CharBuffer;

/**
 *
 */
public class WhitespaceMatcher {

    private static Matcher TAB_OR_SPACE_MATCHER = new TabOrSpaceMatcher();

    public static Matcher tabOrSpaceMatcher() {
        return TAB_OR_SPACE_MATCHER;
    }

    private WhitespaceMatcher() {}



    private static class TabOrSpaceMatcher extends BaseMatcher {
        public MatchResult match( CharBuffer buf, boolean isEOS ) {
            int pos   = buf.position();
            int limit = buf.limit();

            for ( int i=pos; i<limit; i++ ) {
                char c = buf.get(i);

                if ( c != ' ' && c != '\t' ) {
                    buf.position( i );
                    return MatchResult.matched( i-pos, null );
                }
            }

            if ( isEOS ) {
                buf.position(limit);
                return MatchResult.matched( limit-pos, null );
            } else {
                return MatchResult.incompleteMatch();
            }
        }
    }

}
