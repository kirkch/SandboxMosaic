package com.mosaic.parsers.push.matchers;

import com.mosaic.lang.function.VoidFunction1;
import com.mosaic.parsers.push.Matcher;

import java.util.List;

/**
 *
 */
public class Matchers {

    public static Matcher<String> constant( String target ) {
        return new ConstantMatcher( target );
    }

    public static <T> Matcher<T> skipWhitespace( Matcher<T> wrappedMatcher ) {
        return new SkipWhitespaceMatcher( wrappedMatcher );
    }

    public static Matcher eof() {
        return new EOFMatcher();
    }
    
    public static Matcher eol() {
        return discard( or(constant("\n"),constant("\r\n"),eof()) );
    }

    public static Matcher<String> alwaysMatches() {
        return new AlwaysMatchesMatcher();
    }

    /**
     * returns the match from the first matcher that reports a match. Fails if and only if all matchers fail.
     */
    public static <T> Matcher<T> or( Matcher<T>...candidateMatchers ) {
        return new OrMatcher(candidateMatchers);
    }

    public static <T> Matcher<T> discard( Matcher<T> wrappedMatcher ) {
        return new DiscardMatcher( wrappedMatcher );
    }

    public static <T> Matcher<List<T>> listDemarcated( Matcher prefix, Matcher<T> element, Matcher seperator, Matcher postfix ) {
        return new ListMatcher( prefix, element, seperator, postfix );
    }

    public static <T> Matcher<List<T>> listUndemarcated( Matcher<T> element, Matcher seperator ) {
        return new ListMatcher( alwaysMatches(), element, seperator, alwaysMatches() );
    }

    public static <T> Matcher<List<T>> zeroOrMore( Matcher<T> wrappedMatcher ) {
        return new ZeroOrMoreMatcher( wrappedMatcher );
    }

    public static <T> Matcher<T> issueCallback( Matcher<T> wrappedMatcher, VoidFunction1<T> callback ) {
        return new IssueCallbackMatcher( wrappedMatcher, callback );
    }

    public static <T> Matcher<T> issueCallbackAndSkip( Matcher<T> wrappedMatcher, VoidFunction1<T> callback ) {
        return discard( new IssueCallbackMatcher(wrappedMatcher, callback) );
    }

}
