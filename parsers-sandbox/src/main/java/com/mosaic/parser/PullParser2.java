package com.mosaic.parser;

import com.mosaic.io.CharPosition;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Try;
import com.mosaic.lang.functional.TryNow;
import com.mosaic.lang.functional.VoidFunction1;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.utils.string.CharacterMatcher;
import com.mosaic.utils.string.CharacterMatchers;


/**
 *
 * @param <L> Specifies what is being matched.  Typically via a enum representing the lexical tokens
 *           of a language.
 */
public class PullParser2<L> {

    private final MatchResult<L> NO_MATCH = new NoMatch<>();

    private final CharSequence chars;

    private CharPosition pos;
    private int          maxExc;


    private CharacterMatcher skipMatcher ;

    public PullParser2( CharSequence chars, L whiteSpaceTokenType ) {
        this( chars, 0, whiteSpaceTokenType );
    }

    public PullParser2( CharSequence chars, int pos, L whiteSpaceTokenType ) {
//        this.skipMatcher = CharacterMatchers.whitespace( whiteSpaceTokenType );

        this.chars  = chars;
        this.pos    = new CharPosition();
        this.maxExc = chars.length();

        this.pos.walkCharacters( pos, chars );
    }

    public CharPosition getPosition() {
        return pos;
    }

    /**
     * Automatically skip any characters matched by the specified matcher.  These characters are
     * skipped between calls to other parse methods on this class, and not during the execution of
     * a CharacterMatcher.
     */
    public void setSkipMatcher( CharacterMatcher matcher ) {
        this.skipMatcher = matcher;
    }

    /**
     * Use the specified matcher to match characters at the current location.  If it succeeds then
     * the current location will scroll on to after the matched characters, if it fails then the
     * current location will remain the same.  Either way, the result of the match will be returned
     * in the result.
     */
    public MatchResult<L> pull( CharacterMatcher matcher ) {
        runSkipMatcher();

        CharPosition startOfMatch         = pos;
        int          fromIndex            = Backdoor.toInt( startOfMatch.getCharacterOffset() );
        int          numCharactersMatched = matcher.consumeFrom( chars, fromIndex, maxExc );

        if ( numCharactersMatched > 0 ) {
            CharSequence matchedText = chars.subSequence(fromIndex,fromIndex+numCharactersMatched);

            this.pos = pos.walkCharacters( numCharactersMatched, chars );

//            return new SuccessfulMatch<>( startOfMatch, matcher.matchType(), matchedText );
            return null;
        } else {
            return NO_MATCH;
        }
    }

    /**
     * Attempt to keep parsing with the specified matchers until no more match.  Each time one of
     * the matchers match, the action associated with that matcher will be invoked.  In BNF terms,
     * this method is the equivalent to (A | B | C ... )*.
     */
    public void parseZeroOrMore( MatcherAndAction...matcherAndActions ) {

    }

    /**
     * Attempt to keep parsing with the specified matchers until no more match.  Each time one of
     * the matchers match, the action associated with that matcher will be invoked.  In BNF terms,
     * this method is the equivalent to (A | B | C ... )*.
     */
    public void parseZeroOrMore( ParseFunctionAndAction...parseFunctionAndActions ) {

    }

    private void runSkipMatcher() {
        int numCharactersMatched;

        do {
            int fromIndex = Backdoor.toInt( pos.getCharacterOffset() );

            numCharactersMatched = skipMatcher.consumeFrom( chars, fromIndex, maxExc );

            this.pos = pos.walkCharacters( numCharactersMatched, chars );
        } while ( numCharactersMatched > 0 && pos.getCharacterOffset() < maxExc );
    }

    public <R> Try<R> createFailure( String msg, Object...args ) {
        return TryNow.failed( msg + ", instead found '" + pullToEndOfLine() + "'", args );
    }

    private String pullToEndOfLine() {
        int fromIndex = Backdoor.toInt( pos.getCharacterOffset() );

        for ( int i=fromIndex; i<maxExc; i++ ) {
            char c = chars.charAt(i);

            if ( c == '\n' || c == '\r' ) {
                return chars.subSequence( fromIndex, i ).toString();
            }
        }

        return chars.subSequence( fromIndex, maxExc ).toString();
    }


    /**
     * A DTO for PullParser2.parseZeroOrMore.  This DTO contains a matcher that will either
     * pass or fail, if it passes then the result of the parse will be past the action function.
     */
    public static class MatcherAndAction<L,T>{
        public final CharacterMatcher matcher;
        public final VoidFunction1<T>    action;

        public MatcherAndAction( CharacterMatcher matcher, VoidFunction1<T> action ) {
            this.matcher = matcher;
            this.action  = action;
        }
    }

    /**
     * A DTO for PullParser2.parseZeroOrMore.  This DTO contains a matcher function that will either
     * pass or fail, if it passes then the result of the parse will be past the action function.
     * The parse function is used over a CharacterMatcher when a composite parse is required.  The
     * recommendation is to use a CharacterMatcher directly until
     */
    public static class ParseFunctionAndAction<T>{
        public final Function1<PullParser2, Try<T>> matchFunction;
        public final VoidFunction1<T>               action;

        public ParseFunctionAndAction( Function1<PullParser2,Try<T>> matchFunction, VoidFunction1<T> action ) {
            this.matchFunction = matchFunction;
            this.action        = action;
        }
    }

}
