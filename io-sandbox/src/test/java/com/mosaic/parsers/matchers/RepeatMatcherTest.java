package com.mosaic.parsers.matchers;

import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;
import org.junit.Test;

import java.nio.CharBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

// todo andMatcher
// todo repeatMatcher
// todo benchmark both
// todo compare with SeparatedListMatcher

/**
 *
 */
public class RepeatMatcherTest {

    private Matcher    firstMatcher      = ConstantMatcher.create( "a" );
    private Matcher    subsequentMatcher = ConstantMatcher.create(".");
    private Matcher    matcher           = new RepeatMatcher(firstMatcher, subsequentMatcher);

    private CharBuffer input             = CharBuffer.wrap("a.....");


    @Test
    public void givenInput_expectContinuationWithValueMatcher() {
        MatchResult result = matcher.match(input, false);

        assertContinuation( result, firstMatcher );
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenFirstContinuation_giveIncompleteMatch_expectIncompleteMatch() {
        MatchResult result = matcher.match(input, false)
                .getContinuation().invoke(MatchResult.incompleteMatch());

        assertIncompleteMatch(result);
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenFirstContinuation_giveNoMatch_expectMatchResultWithEmptyList() {
        MatchResult result = matcher.match(input, false)
                .getContinuation().invoke(MatchResult.noMatch());

        assertMatch(result, Arrays.asList());
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenFirstContinuation_givenErrorMatch_expectMatchResultWithEmptyList() {
        MatchResult result = matcher.match(input, false)
                .getContinuation().invoke(MatchResult.errored(3, "splat"));

        assertError(result, 3, "splat");
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenFirstContinuation_giveMatch_expectContinuation() {
        MatchResult result = matcher.match(input, false)
                .getContinuation().invoke(MatchResult.matched(3, "abc"));

        assertContinuation(result, subsequentMatcher);
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenSecondContinuation_giveIncompleteMatch_expectIncompleteMatch() {
        MatchResult result = matcher.match(input, false)
                .getContinuation().invoke(MatchResult.matched(3, "abc"))
                .getContinuation().invoke(MatchResult.incompleteMatch());

        assertIncompleteMatch(result);
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenSecondContinuation_giveNoMatch_expectMatchResultWithOneValue() {
        MatchResult result = matcher.match(input, false)
                .getContinuation().invoke(MatchResult.matched(3, "abc"))
                .getContinuation().invoke(MatchResult.noMatch());

        assertMatch(result, Arrays.asList("abc"));
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenSecondContinuation_givenErrorMatch_expectMatchResultWithOneValue() {
        MatchResult result = matcher.match(input, false)
                .getContinuation().invoke(MatchResult.matched(3, "abc"))
                .getContinuation().invoke(MatchResult.errored(2, "splat"));

        assertMatch(result, Arrays.asList("abc"));
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenSecondContinuation_giveMatch_expectContinuation() {
        MatchResult result = matcher.match(input, false)
                .getContinuation().invoke(MatchResult.matched(3, "abc"))
                .getContinuation().invoke(MatchResult.matched(2, "de"));

        assertContinuation(result, subsequentMatcher);
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenThirdContinuation_giveIncompleteMatch_expectIncompleteMatch() {
        MatchResult result = matcher.match(input, false)
                .getContinuation().invoke(MatchResult.matched(3, "abc"))
                .getContinuation().invoke(MatchResult.matched(2, "de"))
                .getContinuation().invoke(MatchResult.incompleteMatch());

        assertIncompleteMatch(result);
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenThirdContinuation_giveNoMatch_expectMatchResultWithTwoValues() {
        MatchResult result = matcher.match(input, false)
                .getContinuation().invoke(MatchResult.matched(3, "abc"))
                .getContinuation().invoke(MatchResult.matched(2, "de"))
                .getContinuation().invoke(MatchResult.noMatch());

        assertMatch(result, Arrays.asList("abc", "de"));
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenThirdContinuation_givenErrorMatch_expectMatchResultWithTwoValues() {
        MatchResult result = matcher.match(input, false)
                .getContinuation().invoke(MatchResult.matched(3, "abc"))
                .getContinuation().invoke(MatchResult.matched(2, "de"))
                .getContinuation().invoke(MatchResult.errored(1, "splat"));

        assertMatch(result, Arrays.asList("abc", "de"));
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenThirdContinuation_giveMatch_expectContinuation() {
        MatchResult result = matcher.match(input, false)
                .getContinuation().invoke(MatchResult.matched(3, "abc"))
                .getContinuation().invoke(MatchResult.matched(2, "de"))
                .getContinuation().invoke(MatchResult.matched(1, "f"));

        assertContinuation(result, subsequentMatcher);
        assertEquals( 0, input.position() );
    }


    //
    //
    //
    //




    private void assertIncompleteMatch(MatchResult result) {
        assertTrue( "expected 'incomplete', was '"+result+"'", result.isIncompleteMatch() );
        assertNull(result.getNextMatcher());
        assertNull( result.getParsedValue() );
        assertNull(result.getContinuation());
        assertEquals( 0, result.getMatchIndexOnError() );
        assertNull( result.getErrorMessage() );
    }

    private void assertContinuation( MatchResult result, Matcher expectedNextMatcher ) {
        assertTrue( "expected 'continuation', was '"+result+"'", result.isContinuation() );
        assertSame(expectedNextMatcher, result.getNextMatcher());
        assertNull( result.getParsedValue() );
        assertNotNull(result.getContinuation());
        assertEquals( 0, result.getMatchIndexOnError() );
        assertNull( result.getErrorMessage() );
    }

    private void assertMatch( MatchResult result, Object expectedParsedValue ) {
        assertTrue( "expected 'match', was '"+result+"'", result.isMatch() );
        assertNull(result.getNextMatcher());
        assertEquals(expectedParsedValue, result.getParsedValue());
        assertNull(result.getContinuation());
        assertEquals( 0, result.getMatchIndexOnError() );
        assertNull( result.getErrorMessage() );
    }

    private void assertError( MatchResult result, int expectedOffset, String expectedErrorMessage ) {
        assertTrue( "expected 'error', was '"+result+"'", result.isError() );
        assertNull(result.getNextMatcher());
        assertNull(result.getParsedValue());
        assertNull(result.getContinuation());
        assertEquals( expectedOffset, result.getMatchIndexOnError() );
        assertEquals( expectedErrorMessage, result.getErrorMessage() );
    }

//    @Test
//    public void givenEmptyStringContinuation_invokeWithInprogress_expectInprogress() {
//        CharBuffer input = CharBuffer.wrap("");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.incompleteMatch());
//
//
//        assertTrue( result.isIncompleteMatch() );
//        assertNull(result.getNextMatcher());
//        assertNull(result.getContinuation());
//        assertEquals( 0, input.position() );
//    }
//
//    @Test
//    public void givenEmptyStringContinuation_invokeWithError_expectError() {
//        CharBuffer input = CharBuffer.wrap("");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.errored(0, "foobar"));
//
//
//        assertTrue( result.isError() );
//        assertNull(result.getNextMatcher());
//        assertNull(result.getContinuation());
//        assertEquals("foobar", result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//    }
//
//    @Test
//    public void givenFirstValueContinuation_invokeWithNoMatch_expectEmptyListResult() {
//        CharBuffer input = CharBuffer.wrap("hello world");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.noMatch());
//
//
//        assertTrue( result.isMatch() );
//        assertEquals(Arrays.asList(), result.getParsedValue());
//        assertNull(result.getNextMatcher());
//        assertNull(result.getContinuation());
//        assertNull(result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//    }
//
//    @Test
//    public void givenFirstValueContinuation_invokeWithSuccess_expectContinuationWithSeparatorMatcher() {
//        CharBuffer input = CharBuffer.wrap("hello world");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(2, "foobar"));
//
//
//        assertTrue( result.isContinuation() );
//        assertSame(subsequentMatcher, result.getNextMatcher());
//        assertNotNull(result.getContinuation());
//        assertNull(result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }
//
//    @Test
//    public void givenFirstSeparatorContinuation_invokeWithNoMatch_expectSingleValueListMatch() {
//        CharBuffer input = CharBuffer.wrap("a,b,c");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(1, "a"))
//                .getContinuation().invoke( MatchResult.noMatch() );
//
//
//        assertTrue( result.isMatch() );
//        assertEquals(Arrays.asList("a"), result.getParsedValue());
//        assertNull(result.getNextMatcher());
//        assertNull(result.getContinuation());
//        assertNull(result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }
//
//    @Test
//    public void givenFirstSeparatorContinuation_invokeWithIncompleteMatch_expectIncompleteMatch() {
//        CharBuffer input = CharBuffer.wrap("a,b,c");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(2, "a"))
//                .getContinuation().invoke( MatchResult.incompleteMatch() );
//
//
//        assertTrue( result.isIncompleteMatch() );
//        assertNull(result.getParsedValue());
//        assertNull(result.getNextMatcher());
//        assertNull(result.getContinuation());
//        assertNull(result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }
//
//    @Test
//    public void givenFirstSeparatorContinuation_invokeWithError_expectError() {
//        CharBuffer input = CharBuffer.wrap("a,b,c");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(2, "a"))
//                .getContinuation().invoke( MatchResult.errored(1, "splat") );
//
//
//        assertTrue( result.isError() );
//        assertNull(result.getParsedValue());
//        assertNull(result.getNextMatcher());
//        assertNull(result.getContinuation());
//        assertEquals("splat", result.getErrorMessage());
//        assertEquals(1, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }
//
//    @Test
//    public void givenFirstSeparatorContinuation_invokeWithMatch_expectContinuation() {
//        CharBuffer input = CharBuffer.wrap("a,b,c");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(2, "a"))
//                .getContinuation().invoke( MatchResult.matched(1, ",") );
//
//
//        assertTrue( result.isContinuation() );
//        assertNull(result.getParsedValue());
//        assertSame(firstMatcher, result.getNextMatcher());
//        assertNotNull(result.getContinuation());
//        assertNull(result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }
//
//    @Test
//    public void givenSecondValueContinuation_invokeWithNoMatch_expectError() {
//        CharBuffer input = CharBuffer.wrap("a,b,c");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(2, "a"))
//                .getContinuation().invoke( MatchResult.matched(1,",") )
//                .getContinuation().invoke( MatchResult.noMatch() );
//
//
//        assertTrue( result.isError() );
//        assertNull(result.getParsedValue());
//        assertNull(result.getNextMatcher());
//        assertNull(result.getContinuation());
//        assertEquals("expected value after ','", result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }
//
//    @Test
//    public void givenSecondValueContinuation_invokeWithIncompleteMatch_expectIncompleteMatch() {
//        CharBuffer input = CharBuffer.wrap("a,b,c");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(2, "a"))
//                .getContinuation().invoke( MatchResult.matched(1,",") )
//                .getContinuation().invoke( MatchResult.incompleteMatch() );
//
//
//        assertTrue( result.isIncompleteMatch() );
//        assertNull(result.getParsedValue());
//        assertNull(result.getNextMatcher());
//        assertNull(result.getContinuation());
//        assertNull(result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }
//
//    @Test
//    public void givenSecondValueContinuation_invokeWithError_expectError() {
//        CharBuffer input = CharBuffer.wrap("a,b,c");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(2, "a"))
//                .getContinuation().invoke( MatchResult.matched(1,",") )
//                .getContinuation().invoke( MatchResult.errored(2, "splat") );
//
//
//        assertTrue( result.isError() );
//        assertNull(result.getParsedValue());
//        assertNull(result.getNextMatcher());
//        assertNull(result.getContinuation());
//        assertEquals("splat", result.getErrorMessage());
//        assertEquals(2, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }
//
//    @Test
//    public void givenSecondValueContinuation_invokeWithMatch_expectContinuation() {
//        CharBuffer input = CharBuffer.wrap("a,b,c");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(2, "a"))
//                .getContinuation().invoke( MatchResult.matched(1,",") )
//                .getContinuation().invoke( MatchResult.matched(1, "b") );
//
//
//        assertTrue( result.isContinuation() );
//        assertNull(result.getParsedValue());
//        assertSame(subsequentMatcher, result.getNextMatcher());
//        assertNotNull(result.getContinuation());
//        assertNull(result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }
//
//    @Test
//    public void givenTwoSeparatorContinuation_invokeWithNoMatch_expectTwoValueListMatch() {
//        CharBuffer input = CharBuffer.wrap("a,b,c");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(2, "a"))
//                .getContinuation().invoke( MatchResult.matched(1,",") )
//                .getContinuation().invoke( MatchResult.matched(1,"b") )
//                .getContinuation().invoke( MatchResult.noMatch() );
//
//
//        assertTrue( result.isMatch() );
//        assertEquals(Arrays.asList("a", "b"), result.getParsedValue());
//        assertNull(result.getNextMatcher());
//        assertNull(result.getContinuation());
//        assertNull(result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }
//
//    @Test
//    public void givenTwoSeparatorContinuation_invokeWithIncompleteMatch_expectIncompleteMatch() {
//        CharBuffer input = CharBuffer.wrap("a,b,c");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(2, "a"))
//                .getContinuation().invoke( MatchResult.matched(1,",") )
//                .getContinuation().invoke( MatchResult.matched(1,"b") )
//                .getContinuation().invoke( MatchResult.incompleteMatch() );
//
//
//        assertTrue( result.isIncompleteMatch() );
//        assertNull(result.getParsedValue());
//        assertNull(result.getNextMatcher());
//        assertNull(result.getContinuation());
//        assertNull(result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }
//
//    @Test
//    public void givenTwoSeparatorContinuation_invokeWithError_expectError() {
//        CharBuffer input = CharBuffer.wrap("a,b,c");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(2, "a"))
//                .getContinuation().invoke( MatchResult.matched(1,",") )
//                .getContinuation().invoke( MatchResult.matched(1,"b") )
//                .getContinuation().invoke( MatchResult.errored(0, "splat") );
//
//
//        assertTrue( result.isError() );
//        assertNull(result.getParsedValue());
//        assertNull(result.getNextMatcher());
//        assertNull(result.getContinuation());
//        assertEquals("splat", result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }
//
//    @Test
//    public void givenTwoSeparatorContinuation_invokeWithMatch_expectContinuation() {
//        CharBuffer input = CharBuffer.wrap("a,b,c");
//
//        MatchResult result = matcher.match(input, false).getContinuation().invoke(MatchResult.matched(2, "a"))
//                .getContinuation().invoke( MatchResult.matched(1,",") )
//                .getContinuation().invoke( MatchResult.matched(1,"b") )
//                .getContinuation().invoke( MatchResult.matched(1, ",") );
//
//
//        assertTrue( result.isContinuation() );
//        assertNull(result.getParsedValue());
//        assertSame(firstMatcher, result.getNextMatcher());
//        assertNotNull(result.getContinuation());
//        assertNull(result.getErrorMessage());
//        assertEquals(0, result.getMatchIndexOnError());
//        assertEquals(0, result.getNumCharactersConsumed());
//    }

}
