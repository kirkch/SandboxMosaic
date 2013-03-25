package com.mosaic.parsers.push2.matchers;

import com.mosaic.parsers.push2.Matcher;

/**
 *
 */
public class Matchers {
    public static Matcher<String> constant( String target ) {
        return new ConstantMatcher( target );
    }

//    public static <T> Matcher<T> listDemarcated( Matcher prefix, Matcher<T> element, Matcher seperator, Matcher postfix ) {
//        return new ListMatcher( prefix, element, seperator, postfix );
//    }
//
//    public static <T> Matcher<T> listUndemarcated( Matcher<T> element, Matcher seperator ) {
//        return new ListMatcher( prefix, element, seperator, postfix );
//    }
//
//    public static <T> Matcher<T> repeatedGreedy( Matcher<T> element ) {
//        return new ListMatcher( prefix, element, seperator, postfix );
//    }
}
