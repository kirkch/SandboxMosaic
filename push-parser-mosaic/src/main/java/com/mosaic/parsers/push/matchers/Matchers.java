package com.mosaic.parsers.push.matchers;

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

    public static <T> Matcher<List<T>> listDemarcated( Matcher prefix, Matcher<T> element, Matcher seperator, Matcher postfix ) {
        return new ListMatcher( prefix, element, seperator, postfix );
    }

    public static <T> Matcher<List<T>> listUndemarcated( Matcher<T> element, Matcher seperator ) {
        AlwaysMatchesMatcher alwaysMatchesMatcher = new AlwaysMatchesMatcher();

        return new ListMatcher( alwaysMatchesMatcher, element, seperator, alwaysMatchesMatcher );
    }
}


//
// list
// zeroOrMore
// issueCallback
// discard

// private static final Matcher<String> csvColumn = skipWhitespace(regexp("^[,EOL]+"))

// private static final Matcher<List<String>> row  = list( csvColumn, comma, eolf )
// private static final Matcher               rows = zeroOrMore( issueCallbackAndSkip(row,this,"rowParsed",List<String>.class) )
