package com.mosaic.parsers.push;

import com.mosaic.io.CharPosition;
import com.mosaic.io.Characters;
import com.mosaic.lang.Validate;

import java.util.regex.Pattern;

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

    // regexp
    // skipWhitespace
    // list
    // zeroOrMore
    // issueCallback
    // discard

// private static final Matcher<String> csvColumn = skipWhitespace(regexp("^[,EOL]+"))

    // private static final Matcher<List<String>> row  = list( csvColumn, comma, eol )
    // private static final Matcher               rows = zeroOrMore( issueCallbackAndSkip(row,this,"rowParsed",List<String>.class) )
}



class ConstantMatcher extends Matcher<String> {

    private final String targetString;

    public ConstantMatcher( String targetString ) {
        this( targetString, null, null, null );

        Validate.isGTZero( targetString.length(), "targetString.length()" );
    }

    private ConstantMatcher( String targetString, String result, Characters remainingBytes, CharPosition startingPosition ) {
        super( result, remainingBytes, startingPosition );

        this.targetString = targetString;
    }

    @Override
    protected Matcher<String> _processCharacters( Characters in ) {
        if ( in.startsWith(targetString) ) {
            Characters remainingBytes = in.skipCharacters( targetString.length() );

            return new ConstantMatcher( targetString, targetString, remainingBytes, in.getPosition() );
        } else {
            Characters remainingBytes = this.appendCharacters(in);

            return new ConstantMatcher( targetString, null, remainingBytes, this.getStartingPosition() );
        }
    }

    private Characters appendCharacters( Characters in ) {
        Characters remaining = this.getRemainingCharacters();
        if ( remaining == null ) {
            return in;
        } else {
            return remaining.appendCharacters( in );
        }
    }

}



class RegExpMatcher extends Matcher<String> {

    private final Pattern regexp;

    public RegExpMatcher( Pattern regexp ) {
        this( regexp, null, null, null );
    }

    private RegExpMatcher( Pattern regexp, String result, Characters remainingBytes, CharPosition startingPosition ) {
        super( result, remainingBytes, startingPosition );

        this.regexp = regexp;
    }

    @Override
    protected Matcher<String> _processCharacters( Characters in ) {
        return _processCharacters(in,true);
    }

    @Override
    public Matcher<String> endOfStream() {
        return _processCharacters(getRemainingCharacters(),false);
    }

    private Matcher<String> _processCharacters( Characters in, boolean isGreedy ) {
        java.util.regex.Matcher m = regexp.matcher( in );

        if ( !m.lookingAt() ) {
            return new RegExpMatcher( regexp, null, in, getStartingPosition() );
        }

        int parsedUptoExc = m.end();
        if ( isGreedy && parsedUptoExc == in.length() ) {
            return new RegExpMatcher( regexp, null, in, getStartingPosition() ); // geedy match, wait for more input
        }

        String matchedString = in.toString(0,parsedUptoExc);

        return new RegExpMatcher( regexp, matchedString, in.skipCharacters(parsedUptoExc), getStartingPosition() );
    }
}