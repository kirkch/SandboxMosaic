package com.mosaic.parser;

import com.mosaic.io.CharPosition;
import com.mosaic.lang.functional.Try;
import com.mosaic.lang.functional.TryNow;
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


    private CharacterMatcher<L> skipMatcher ;

    public PullParser2( CharSequence chars, L whiteSpaceTokenType ) {
        this( chars, 0, whiteSpaceTokenType );
    }

    public PullParser2( CharSequence chars, int pos, L whiteSpaceTokenType ) {
        this.skipMatcher = CharacterMatchers.whitespace( whiteSpaceTokenType );

        this.chars  = chars;
        this.pos    = new CharPosition();
        this.maxExc = chars.length();

        this.pos.walkCharacters( pos, chars );
    }

    public CharPosition getPosition() {
        return pos;
    }

    public void setSkipMatcher( CharacterMatcher<L> matcher ) {
        this.skipMatcher = matcher;
    }

    public MatchResult<L> pull( CharacterMatcher<L> matcher ) {
        runSkipMatcher();

        CharPosition startOfMatch         = pos;
        int          fromIndex            = Backdoor.toInt( startOfMatch.getCharacterOffset() );
        int          numCharactersMatched = matcher.consumeFrom( chars, fromIndex, maxExc );

        if ( numCharactersMatched > 0 ) {
            CharSequence matchedText = chars.subSequence(fromIndex,fromIndex+numCharactersMatched);

            this.pos = pos.walkCharacters( numCharactersMatched, chars );

            return new SuccessfulMatch<>( startOfMatch, matcher.matchType(), matchedText );
        } else {
            return NO_MATCH;
        }
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

}
