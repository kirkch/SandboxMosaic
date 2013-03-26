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

    public static <T> Matcher<T> discard( Matcher<T> wrappedMatcher ) {
        return new DiscardMatcher( wrappedMatcher );
    }

    public static <T> Matcher<List<T>> listDemarcated( Matcher prefix, Matcher<T> element, Matcher seperator, Matcher postfix ) {
        return new ListMatcher( prefix, element, seperator, postfix );
    }

    public static <T> Matcher<List<T>> listUndemarcated( Matcher<T> element, Matcher seperator ) {
        AlwaysMatchesMatcher alwaysMatchesMatcher = new AlwaysMatchesMatcher();

        return new ListMatcher( alwaysMatchesMatcher, element, seperator, alwaysMatchesMatcher );
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
