package com.mosaic.parsers.matchers;

import com.mosaic.parsers.BaseMatcher;
import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;

import java.nio.CharBuffer;

/**
 *
 */
public class WhitespaceMatcher {

    public static Matcher tabOrSpaceMatcher() {
        return new TabOrSpaceMatcher();
    }

    public static Matcher whitespaceMatcher() {
        return new AllWhitespaceMatcher();
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

    private static class AllWhitespaceMatcher extends BaseMatcher {
        public MatchResult match( CharBuffer buf, boolean isEOS ) {
            int pos   = buf.position();
            int limit = buf.limit();

            for ( int i=pos; i<limit; i++ ) {
                char c = buf.get(i);

                // NB this optimisation improves performance of this matcher by 5%
                //  at the cost of also stepping over other control characters such as
                //  escape, delete, and so forth..  a fair trade off
                if ( c > ' ' ) {//c > ' '  || (c != ' ' && c != '\n' && c != '\r' && c != '\t') ) {
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
