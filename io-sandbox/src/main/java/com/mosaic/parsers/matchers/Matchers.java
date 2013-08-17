package com.mosaic.parsers.matchers;

import com.mosaic.parsers.Matcher;

/**
 *
 */
public class Matchers {

    /**
     * Must match each of the supplied child matchers. At which point each of the
     * childrens parsed values will be returned in a list.  If the child is marked
     * as skippable then its parsed value will be skipped but the matcher itself
     * must still match.
     */
    public static Matcher and( Matcher...childMatchers ) {
        return new AndMatcher(childMatchers);
    }

    /**
     * Repeats valueMatcher until it no longer matches anything.  Collects all
     * results into a list.
     */
    public static Matcher repeatOnceOrMore( Matcher valueMatcher ) {
        return new RepeatMatcher( valueMatcher, valueMatcher );
    }

    /**
     * Optional matcher that does not need to match anything.  First performs
     * an optional match using firstValueMatcher and then if that is a hit, repeats
     * with subsequentValueMatcher.  Useful for scenarios where matching the first
     * item in a list is a special case.  For example:   1, 2, 3. <p/>
     *
     * Collects all results into a list.
     */
    public static Matcher repeatOnceOrMore( Matcher firstValueMatcher, Matcher subsequentValueMatcher ) {
        return new RepeatMatcher( firstValueMatcher, subsequentValueMatcher );
    }

    public static Matcher commaSeparatedValues( Matcher valueMatcher ) {
        return new SeparatedListMatcher(valueMatcher, constant(','));
//        return repeatOnceOrMore(valueMatcher, and(constant(","),valueMatcher) );
    }

    public static Matcher constant( String target ) {
        return ConstantMatcher.create(target);
    }

    public static Matcher constant( char target ) {
        return ConstantMatcher.create(target);
    }

    public static Matcher tabOrSpaceMatcher() {
        return WhitespaceMatcher.tabOrSpaceMatcher();
    }

    public static Matcher whitespaceMatcher() {
        return WhitespaceMatcher.whitespaceMatcher();
    }


}
