package com.mosaic.parsers.matchers;

import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;
import org.junit.Test;

import java.nio.CharBuffer;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class AndMatcherTest {

    private Matcher child1 = Matchers.constant("one");
    private Matcher child2 = Matchers.constant("two");
    private Matcher child3 = Matchers.constant("three");

    @Test
    public void createAndMatcherWithNoChildren_expectException() {
        try {
            new AndMatcher();
            fail( "required IllegalArgumentException" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "AndMatcher does not support having no child matchers", e.getMessage() );
        }
    }




    @Test
    public void singleChild_givenInput_expectContinuationToFirstChild() {
        Matcher     matcher = new AndMatcher(child1);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false);

        MatcherAsserts.assertContinuation(result, child1);
        assertEquals(0, buf.position());
    }

    @Test
    public void singleChild_givenFirstContinuation_continueWithNoMatch_expectNoMatch() {
        Matcher     matcher = new AndMatcher(child1);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.noMatch());

        MatcherAsserts.assertNoMatch(result);
        assertEquals(0, buf.position());
    }

    @Test
    public void singleChild_givenFirstContinuation_continueWithError_expectError() {
        Matcher     matcher = new AndMatcher(child1);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.errored(2, "splat"));

        MatcherAsserts.assertError(result, 2, "splat");
        assertEquals(0, buf.position());
    }

    @Test
    public void singleChild_givenFirstContinuation_continueWithIncomplete_expectIncomplete() {
        Matcher     matcher = new AndMatcher(child1);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.incompleteMatch());

        MatcherAsserts.assertIncompleteMatch(result);
        assertEquals(0, buf.position());
    }

    @Test
    public void singleChild_givenFirstContinuation_continueWithMatch_expectMatch() {
        Matcher     matcher = new AndMatcher(child1);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.matched(2, "ab"));

        MatcherAsserts.assertMatch(result, 0, Arrays.asList("ab"));
        assertEquals(0, buf.position());
    }

    @Test
    public void singleChild_givenFirstContinuation_continueWithSkipableMatch_expectEmptyListMatch() {
        Matcher     matcher = new AndMatcher(child1);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.matched(2));

        MatcherAsserts.assertMatch(result, 0, Arrays.asList());
        assertEquals(0, buf.position());
    }

    @Test
    public void singleChild_givenFirstContinuation_continueWithNullMatch_expectNullInListMatch() {
        Matcher     matcher = new AndMatcher(child1);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.matched(2, null));

        MatcherAsserts.assertMatch(result, 0, Arrays.asList(new Object[] {null}));
        assertEquals(0, buf.position());
    }






    @Test
    public void twoChildren_givenInput_expectContinuationToFirstChild() {
        Matcher     matcher = new AndMatcher(child1,child2);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false);

        MatcherAsserts.assertContinuation(result, child1);
        assertEquals(0, buf.position());
    }

    @Test
    public void twoChildren_givenFirstContinuation_continueWithNoMatch_expectNoMatch() {
        Matcher     matcher = new AndMatcher(child1,child2);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.noMatch());

        MatcherAsserts.assertNoMatch(result);
        assertEquals(0, buf.position());
    }

    @Test
    public void twoChildren_givenFirstContinuation_continueWithError_expectError() {
        Matcher     matcher = new AndMatcher(child1,child2);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.errored(5, "trip"));

        MatcherAsserts.assertError(result, 5, "trip");
        assertEquals(0, buf.position());
    }

    @Test
    public void twoChildren_givenFirstContinuation_continueWithIncomplete_expectIncomplete() {
        Matcher     matcher = new AndMatcher(child1,child2);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.incompleteMatch());

        MatcherAsserts.assertIncompleteMatch(result);
        assertEquals(0, buf.position());
    }

    @Test
    public void twoChildren_givenFirstContinuation_continueWithMatch_expectContinuationToSecondChild() {
        Matcher     matcher = new AndMatcher(child1,child2);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.matched(2, "ab"));

        MatcherAsserts.assertContinuation(result, child2);
        assertEquals(0, buf.position());
    }

    @Test
    public void twoChildren_givenSecondContinuation_continueWithNoMatch_expectNoMatch() {
        Matcher     matcher = new AndMatcher(child1,child2);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.matched(2, "ab"))
                .getContinuation().invoke( MatchResult.noMatch() );

        MatcherAsserts.assertNoMatch(result);
        assertEquals(0, buf.position());
    }

    @Test
    public void twoChildren_givenSecondContinuation_continueWithError_expectError() {
        Matcher     matcher = new AndMatcher(child1,child2);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.matched(2, "ab"))
                .getContinuation().invoke( MatchResult.errored(0, "foo barred") );

        MatcherAsserts.assertError(result, 0, "foo barred");
        assertEquals(0, buf.position());
    }

    @Test
    public void twoChildren_givenSecondContinuation_continueWithIncomplete_expectIncomplete() {
        Matcher     matcher = new AndMatcher(child1,child2);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.matched(2, "ab"))
                .getContinuation().invoke( MatchResult.incompleteMatch() );

        MatcherAsserts.assertIncompleteMatch(result);
        assertEquals(0, buf.position());
    }

    @Test
    public void twoChildren_givenSecondContinuation_continueWithMatch_expectMatch() {
        Matcher     matcher = new AndMatcher(child1,child2);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.matched(2, "ab"))
                .getContinuation().invoke( MatchResult.matched(3, "cde") );

        MatcherAsserts.assertMatch(result, 0, Arrays.asList("ab", "cde"));
        assertEquals(0, buf.position());
    }

    @Test
    public void twoChildren_givenSecondContinuation_continueWithSkipableMatch_expectSingleValueListMatch() {
        Matcher     matcher = new AndMatcher(child1,child2);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke( MatchResult.matched(2,"ab") )
                .getContinuation().invoke( MatchResult.matched(3) );

        MatcherAsserts.assertMatch(result, 0, Arrays.asList("ab"));
        assertEquals(0, buf.position());
    }

    @Test
    public void twoChildren_givenSecondContinuation_continueWithMatchAfterInitialSkippedMatch_expectSingleValueListMatch() {
        Matcher     matcher = new AndMatcher(child1,child2);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke( MatchResult.matched(2) )
                .getContinuation().invoke( MatchResult.matched(3, "123") );

        MatcherAsserts.assertMatch(result, 0, Arrays.asList("123"));
        assertEquals(0, buf.position());
    }

    @Test
    public void twoChildren_givenSecondContinuation_continueWithNullMatch_expectNullInListMatch() {
        Matcher     matcher = new AndMatcher(child1,child2);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke( MatchResult.matched(2,"abc") )
                .getContinuation().invoke( MatchResult.matched(3, null) );

        MatcherAsserts.assertMatch(result, 0, Arrays.asList("abc", null));
        assertEquals(0, buf.position());
    }






    @Test
    public void threeChildren_givenInput_expectContinuationToFirstChild() {
        Matcher     matcher = new AndMatcher(child1,child2,child3);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false);

        MatcherAsserts.assertContinuation(result, child1);
        assertEquals(0, buf.position());
    }

    @Test
    public void threeChildren_givenFirstContinuation_continueWithNoMatch_expectNoMatch() {
        Matcher     matcher = new AndMatcher(child1,child2,child3);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.noMatch());

        MatcherAsserts.assertNoMatch(result);
        assertEquals(0, buf.position());
    }

    @Test
    public void threeChildren_givenThirdContinuation_continueWithMatch_expectMatch() {
        Matcher     matcher = new AndMatcher(child1,child2,child3);
        CharBuffer  buf     = CharBuffer.wrap("hello");
        MatchResult result  = matcher.match(buf, false)
                .getContinuation().invoke(MatchResult.matched(2,"ab"))
                .getContinuation().invoke(MatchResult.matched(2,"cd"))
                .getContinuation().invoke(MatchResult.matched(2,"ef"));

        MatcherAsserts.assertMatch(result, 0, Arrays.asList("ab","cd","ef"));
        assertEquals(0, buf.position());
    }

}
