package com.mosaic.parsers.matchers;

import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;
import org.junit.Test;

import java.nio.CharBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 *
 */
public class SeparatedListMatcherTest {

    private Matcher valueMatcher     = ConstantMatcher.create( "ab" );
    private Matcher separatorMatcher = ConstantMatcher.create(",");
    private Matcher matcher          = new SeparatedListMatcher( valueMatcher, separatorMatcher );



    @Test
    public void givenEmptyString_expectContinuationWithValueMatcher() {
        CharBuffer input = CharBuffer.wrap("");

        MatchResult result = matcher.match(input, false);

        assertTrue( result.isSubmatcher() );
        assertSame(result.getNextMatcher(), valueMatcher);
        assertNotNull(result.getContinuation());
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenEmptyStringContinuation_invokeWithInprogress_expectInprogress() {
        CharBuffer input = CharBuffer.wrap("");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.incompleteMatch() );


        assertTrue( result.isIncompleteMatch() );
        assertNull(result.getNextMatcher());
        assertNull(result.getContinuation());
        assertEquals( 0, input.position() );
    }

    @Test
    public void givenEmptyStringContinuation_invokeWithError_expectError() {
        CharBuffer input = CharBuffer.wrap("");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.errored(0,"foobar") );


        assertTrue( result.isError() );
        assertNull(result.getNextMatcher());
        assertNull(result.getContinuation());
        assertEquals("foobar", result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
    }

    @Test
    public void givenFirstValueContinuation_invokeWithNoMatch_expectEmptyListResult() {
        CharBuffer input = CharBuffer.wrap("hello world");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.noMatch() );


        assertTrue( result.isMatch() );
        assertEquals(Arrays.asList(), result.getParsedValue());
        assertNull(result.getNextMatcher());
        assertNull(result.getContinuation());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
    }

    @Test
    public void givenFirstValueContinuation_invokeWithSuccess_expectContinuationWithSeparatorMatcher() {
        CharBuffer input = CharBuffer.wrap("hello world");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(2,"foobar") );


        assertTrue( result.isSubmatcher() );
        assertSame(separatorMatcher, result.getNextMatcher());
        assertNotNull(result.getContinuation());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

    @Test
    public void givenFirstSeparatorContinuation_invokeWithNoMatch_expectSingleValueListMatch() {
        CharBuffer input = CharBuffer.wrap("a,b,c");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(1,"a") )
                .getContinuation().invoke( MatchResult.noMatch() );


        assertTrue( result.isMatch() );
        assertEquals(Arrays.asList("a"), result.getParsedValue());
        assertNull(result.getNextMatcher());
        assertNull(result.getContinuation());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

    @Test
    public void givenFirstSeparatorContinuation_invokeWithIncompleteMatch_expectIncompleteMatch() {
        CharBuffer input = CharBuffer.wrap("a,b,c");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(2,"a") )
                .getContinuation().invoke( MatchResult.incompleteMatch() );


        assertTrue( result.isIncompleteMatch() );
        assertNull(result.getParsedValue());
        assertNull(result.getNextMatcher());
        assertNull(result.getContinuation());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

    @Test
    public void givenFirstSeparatorContinuation_invokeWithError_expectError() {
        CharBuffer input = CharBuffer.wrap("a,b,c");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(2,"a") )
                .getContinuation().invoke( MatchResult.errored(1,"splat") );


        assertTrue( result.isError() );
        assertNull(result.getParsedValue());
        assertNull(result.getNextMatcher());
        assertNull(result.getContinuation());
        assertEquals("splat", result.getErrorMessage());
        assertEquals(1, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

    @Test
    public void givenFirstSeparatorContinuation_invokeWithMatch_expectContinuation() {
        CharBuffer input = CharBuffer.wrap("a,b,c");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(2,"a") )
                .getContinuation().invoke( MatchResult.matched(1,",") );


        assertTrue( result.isSubmatcher() );
        assertNull(result.getParsedValue());
        assertSame(valueMatcher, result.getNextMatcher());
        assertNotNull(result.getContinuation());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

    @Test
    public void givenSecondValueContinuation_invokeWithNoMatch_expectError() {
        CharBuffer input = CharBuffer.wrap("a,b,c");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(2,"a") )
                .getContinuation().invoke( MatchResult.matched(1,",") )
                .getContinuation().invoke( MatchResult.noMatch() );


        assertTrue( result.isError() );
        assertNull(result.getParsedValue());
        assertNull(result.getNextMatcher());
        assertNull(result.getContinuation());
        assertEquals("expected value after ','", result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

    @Test
    public void givenSecondValueContinuation_invokeWithIncompleteMatch_expectIncompleteMatch() {
        CharBuffer input = CharBuffer.wrap("a,b,c");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(2,"a") )
                .getContinuation().invoke( MatchResult.matched(1,",") )
                .getContinuation().invoke( MatchResult.incompleteMatch() );


        assertTrue( result.isIncompleteMatch() );
        assertNull(result.getParsedValue());
        assertNull(result.getNextMatcher());
        assertNull(result.getContinuation());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

    @Test
    public void givenSecondValueContinuation_invokeWithError_expectError() {
        CharBuffer input = CharBuffer.wrap("a,b,c");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(2,"a") )
                .getContinuation().invoke( MatchResult.matched(1,",") )
                .getContinuation().invoke( MatchResult.errored(2,"splat") );


        assertTrue( result.isError() );
        assertNull(result.getParsedValue());
        assertNull(result.getNextMatcher());
        assertNull(result.getContinuation());
        assertEquals("splat", result.getErrorMessage());
        assertEquals(2, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

    @Test
    public void givenSecondValueContinuation_invokeWithMatch_expectContinuation() {
        CharBuffer input = CharBuffer.wrap("a,b,c");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(2,"a") )
                .getContinuation().invoke( MatchResult.matched(1,",") )
                .getContinuation().invoke( MatchResult.matched(1,"b") );


        assertTrue( result.isSubmatcher() );
        assertNull(result.getParsedValue());
        assertSame(separatorMatcher, result.getNextMatcher());
        assertNotNull(result.getContinuation());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

    @Test
    public void givenTwoSeparatorContinuation_invokeWithNoMatch_expectTwoValueListMatch() {
        CharBuffer input = CharBuffer.wrap("a,b,c");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(2,"a") )
                .getContinuation().invoke( MatchResult.matched(1,",") )
                .getContinuation().invoke( MatchResult.matched(1,"b") )
                .getContinuation().invoke( MatchResult.noMatch() );


        assertTrue( result.isMatch() );
        assertEquals(Arrays.asList("a","b"), result.getParsedValue());
        assertNull(result.getNextMatcher());
        assertNull(result.getContinuation());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

    @Test
    public void givenTwoSeparatorContinuation_invokeWithIncompleteMatch_expectIncompleteMatch() {
        CharBuffer input = CharBuffer.wrap("a,b,c");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(2,"a") )
                .getContinuation().invoke( MatchResult.matched(1,",") )
                .getContinuation().invoke( MatchResult.matched(1,"b") )
                .getContinuation().invoke( MatchResult.incompleteMatch() );


        assertTrue( result.isIncompleteMatch() );
        assertNull(result.getParsedValue());
        assertNull(result.getNextMatcher());
        assertNull(result.getContinuation());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

    @Test
    public void givenTwoSeparatorContinuation_invokeWithError_expectError() {
        CharBuffer input = CharBuffer.wrap("a,b,c");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(2,"a") )
                .getContinuation().invoke( MatchResult.matched(1,",") )
                .getContinuation().invoke( MatchResult.matched(1,"b") )
                .getContinuation().invoke( MatchResult.errored(0,"splat") );


        assertTrue( result.isError() );
        assertNull(result.getParsedValue());
        assertNull(result.getNextMatcher());
        assertNull(result.getContinuation());
        assertEquals("splat",result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

    @Test
    public void givenTwoSeparatorContinuation_invokeWithMatch_expectContinuation() {
        CharBuffer input = CharBuffer.wrap("a,b,c");

        MatchResult result = matcher.match(input, false).getContinuation().invoke( MatchResult.matched(2,"a") )
                .getContinuation().invoke( MatchResult.matched(1,",") )
                .getContinuation().invoke( MatchResult.matched(1,"b") )
                .getContinuation().invoke( MatchResult.matched(1,",") );


        assertTrue( result.isSubmatcher() );
        assertNull(result.getParsedValue());
        assertSame(valueMatcher, result.getNextMatcher());
        assertNotNull(result.getContinuation());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getMatchIndexOnError());
        assertEquals(0, result.getNumCharactersConsumed());
    }

}
