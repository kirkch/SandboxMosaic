package com.mosaic.parsers.push;

import com.mosaic.io.CharPosition;
import com.mosaic.io.Characters;
import com.mosaic.lang.Validate;

import java.util.List;
import java.util.regex.Pattern;

import static com.mosaic.parsers.push.MatcherStatus.createHasResultStatus;

/**
 * A collection of common matchers and matcher decorators.
 */
public class Matchers {

    /**
     * Requires an exact match.
     */
    public static Matcher<String> constant( String targetString ) {
        return new ConstantMatcher(targetString);
    }

    public static Matcher<String> regexp( String regexp ) {
        Validate.isGTZero( regexp.length(), "regexp.length()" );

        return regexp( Pattern.compile(regexp) );
    }

    public static Matcher<String> regexp( Pattern regexp ) {
        return new RegExpMatcher(regexp);
    }

    public static <T> Matcher<T> skipWhitespace( Matcher<T> matcher ) {
        return new SkipWhitespaceMatcher(matcher);
    }

    /**
     * Match the same type repeatedly and store each match in a list. Each element matched is demarcated by a seperator and the
     * matching ends when the endOfList matcher gets a hit or the end of the stream is reached. The matches for seperatingMatcher
     * and endOfListMatcher are thrown-away, only the results from repeatingMatcher are kept.
     */
    public static <T> Matcher<List<T>> list( Matcher<T> elementMatcher, Matcher seperatingMatcher, Matcher endOfListMatcher ) {
        return new ListElementMatcher( elementMatcher, seperatingMatcher, endOfListMatcher );
    }


    //
    // list
    // zeroOrMore
    // issueCallback
    // discard

// private static final Matcher<String> csvColumn = skipWhitespace(regexp("^[,EOL]+"))

    // private static final Matcher<List<String>> row  = list( csvColumn, comma, eolf )
    // private static final Matcher               rows = zeroOrMore( issueCallbackAndSkip(row,this,"rowParsed",List<String>.class) )
}



class ConstantMatcher extends Matcher<String> {

    private final String targetString;

    public ConstantMatcher( String targetString ) {
        this( targetString, MatcherStatus.<String>createIsParsingStatus(), null, null );

        Validate.isGTZero( targetString.length(), "targetString.length()" );
    }

    private ConstantMatcher( String targetString, MatcherStatus<String> status, Characters remainingBytes, CharPosition startingPosition ) {
        super( status, remainingBytes, startingPosition );

        this.targetString = targetString;
    }

    @Override
    protected Matcher<String> _processCharacters( Characters in ) {
        return _processCharacters( in, true );
    }

    @Override
    public Matcher<String> processEndOfStream() {
        Characters remainingCharacters = getRemainingCharacters();
        if ( remainingCharacters == null ) {
            return createFailedMatcher( null );
        }
        return _processCharacters( remainingCharacters, false );
    }

    private Matcher<String> _processCharacters( Characters in, boolean waitForMoreInputIfPartialMatch ) {
        String targetStr              = this.targetString;
        int    numCharactersToCompare = Math.min( targetStr.length(), in.length() );

        for ( int i=0; i<numCharactersToCompare; i++ ) {
            if ( targetStr.charAt(i) != in.charAt(i) ) {
                return createFailedMatcher( in );
            }
        }

        if ( in.startsWith(targetStr) ) {
            Characters remainingBytes = in.skipCharacters( targetStr.length() );

            return new ConstantMatcher( targetStr, MatcherStatus.createHasResultStatus(targetStr), remainingBytes, in.getPosition() );
        } else if ( waitForMoreInputIfPartialMatch ) {
            return new ConstantMatcher( targetStr, MatcherStatus.<String>createIsParsingStatus(), in, this.getStartingPosition() );
        } else {
            return createFailedMatcher( in );
        }
    }

    private Matcher<String> createFailedMatcher( Characters in ) {
        CharPosition pos    = this.getStartingPosition();
        MatcherStatus<String> status = MatcherStatus.createHasFailedStatus( pos, "expected '%s'", targetString );

        return new ConstantMatcher( targetString, status, in, pos );
    }
}



class RegExpMatcher extends Matcher<String> {

    private final Pattern regexp;

    public RegExpMatcher( Pattern regexp ) {
        this( regexp, MatcherStatus.<String>createIsParsingStatus(), null, null );
    }

    private RegExpMatcher( Pattern regexp, MatcherStatus<String> status, Characters remainingBytes, CharPosition startingPosition ) {
        super( status, remainingBytes, startingPosition );

        this.regexp = regexp;
    }

    @Override
    protected Matcher<String> _processCharacters( Characters in ) {
        if ( in.length() < 200 ) {
            // hack: the regexp engine state cannot be saved between invocations, and thus we would mistake a partial match as a failure
            //   the real solution would be a new regexp engine, so for now we buffer up 200 characters before progressing as a cheap work around
            return new RegExpMatcher( regexp, MatcherStatus.<String>createIsParsingStatus(), in, getStartingPosition() );
        }

        return _processCharacters( in, true );
    }

    @Override
    public Matcher<String> processEndOfStream() {
        return _processCharacters(getRemainingCharacters(),false);
    }

    // abcde
    // + * ?
    // ()
    // [a-z0-9]
    // [^,]
    // case sensitive
    // EOL
    // $childRegExpName

    // [a-z,0-9,\\"->",\\,->,]*
    // regexp("[^,EOL]*", CaseInsenitive & BackslashEscaped)
    // "($escapedCSVValue)"|($unescapedCSVValue)



    private Matcher<String> _processCharacters( Characters in, boolean isGreedy ) {
        java.util.regex.Matcher m   = regexp.matcher( in );
        CharPosition            pos = getStartingPosition();

        if ( !m.lookingAt() ) {
            return new RegExpMatcher( regexp, MatcherStatus.<String>createHasFailedStatus(pos,"no match for regexp '%s'",regexp.pattern()), in, pos );
        }

        int parsedUptoExc = m.end();
        if ( isGreedy && parsedUptoExc == in.length() ) {
            return new RegExpMatcher( regexp, MatcherStatus.<String>createIsParsingStatus(), in, pos ); // geedy match, wait for more input
        }

        String matchedString = in.toString( 0, parsedUptoExc );

        return new RegExpMatcher( regexp, createHasResultStatus(matchedString), in.skipCharacters(parsedUptoExc), pos );
    }
}



class SkipWhitespaceMatcher<T> extends Matcher<T> {

    private static final CharPredicate WHITESPACE_PREDICATE = new CharPredicate() {
        @Override
        public boolean matches( char c ) {
            return Character.isWhitespace(c);
        }
    };


    private final Matcher<T> wrappedMatcher;

    public SkipWhitespaceMatcher( Matcher<T> wrappedMatcher ) {
        this( wrappedMatcher, MatcherStatus.<T>createIsParsingStatus(), null, null );

        Validate.notNull( wrappedMatcher, "wrappedMatcher" );
    }

    private SkipWhitespaceMatcher( Matcher<T> wrappedMatcher, MatcherStatus<T> result, Characters remainingBytes, CharPosition startingPosition ) {
        super( result, remainingBytes, startingPosition );

        this.wrappedMatcher = wrappedMatcher;
    }

    @Override
    protected Matcher<T> _processCharacters( Characters in ) {
        Characters modifiedInput = in.skipWhile( WHITESPACE_PREDICATE );

        if ( modifiedInput.hasContents() && !Character.isWhitespace(modifiedInput.charAt(0)) ) {
            return wrappedMatcher._processCharacters( modifiedInput );
        }

        return new SkipWhitespaceMatcher( wrappedMatcher, MatcherStatus.<T>createIsParsingStatus(), modifiedInput, getStartingPosition() );
    }

    @Override
    public Matcher<T> processEndOfStream() {
        return wrappedMatcher.processEndOfStream();
    }

}