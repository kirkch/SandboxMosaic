package com.mosaic.parsers.matchers;

import com.mosaic.lang.Validate;
import com.mosaic.parsers.BaseMatcher;
import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;

import java.nio.CharBuffer;

/**
 *
 */
public class ConstantMatcher {
    public static Matcher create( char target ) {
        return new SingleCharacterMatcher(Character.toString(target));
    }

    public static Matcher create( String targetString ) {
        Validate.argIsGTZero(targetString.length(), "targetString.length()");

        if ( targetString.length() == 1 ) {
            return new SingleCharacterMatcher(targetString);
        }

        return new MultiCharacterMatcher( targetString );
    }

    // SingleCharacterMatcher is only 3ns faster than multi; however repeated
    // frequently that saving will mount up
    private static class SingleCharacterMatcher extends BaseMatcher {

        private final char        targetChar;
        private final MatchResult successfulMatch;

        public SingleCharacterMatcher(String targetString) {
            this.targetChar       = targetString.charAt(0);
            this.successfulMatch  = MatchResult.matched( 1, targetString );
        }

        public MatchResult match(CharBuffer buf, boolean isEOS) {
            if ( buf.remaining() == 0 ) {
                return isEOS ? MatchResult.noMatch() : MatchResult.incompleteMatch();
            }

            int pos = buf.position();

            if ( targetChar == buf.get(pos) ) {
                buf.position(pos+1);

                return successfulMatch;
            } else {
                return MatchResult.noMatch();
            }
        }

        public String toString() {
            String name = super.getName();

            return name == null ? Character.toString(targetChar) : name;
        }
    }

    private static class MultiCharacterMatcher extends BaseMatcher {

        private final char[]      targetCharacters;
        private final MatchResult successfulMatch;

        public MultiCharacterMatcher( String targetString ) {
            this.targetCharacters = targetString.toCharArray();
            this.successfulMatch  = MatchResult.matched( targetCharacters.length, targetString );
        }

        public MatchResult match( CharBuffer buf, boolean isEOS ) {
            int pos = buf.position();
            int max = Math.min(targetCharacters.length, buf.remaining());

            for ( int i=0; i<max; i++ ) {
                if ( buf.get(i+pos) != targetCharacters[i] ) {
                    return MatchResult.noMatch();
                }
            }

            if ( buf.remaining() < targetCharacters.length ) {
                return MatchResult.incompleteMatch();
            } else {
                buf.position( pos+max );

                return successfulMatch;
            }
        }

        public String toString() {
            String name = super.getName();

            return name == null ? successfulMatch.getParsedValue().toString() : name;
        }

    }
}
