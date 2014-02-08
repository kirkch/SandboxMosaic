package com.mosaic.parsers.matchers;

import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;
import org.junit.Test;

import java.nio.CharBuffer;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class ConstantMatcherTest {

    @Test
    public void givenNullTargetString_expectException() {
        try {
            ConstantMatcher.create( null );
            fail( "Expected NullPointerException" );
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void givenEmptyTargetString_expectException() {
        try {
            ConstantMatcher.create( "" );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'targetString.length()' (0) must be > 0", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBytes_expectIncompleteMatch() {
        Matcher matcher = ConstantMatcher.create("const");

        MatchResult result = matcher.match("", true);

        assertTrue( result.isIncompleteMatch() );
    }

    @Test
    public void givenBytesThatMatchExactly_expectMatch() {
        Matcher matcher = ConstantMatcher.create("const");

        MatchResult result = matcher.match("const", false);

        assertTrue( result.isMatch() );
        assertEquals("const", result.getParsedValue());
    }

    @Test
    public void givenBytesThatMatchExactlyEOS_expectMatch() {
        Matcher matcher = ConstantMatcher.create("const");

        MatchResult result = matcher.match("const", true);

        assertTrue( result.isMatch() );
        assertEquals("const", result.getParsedValue());
    }

    @Test
    public void afterMatchBufPositionIsIncremented() {
        Matcher    matcher = ConstantMatcher.create("const");
        CharBuffer buf     = CharBuffer.wrap("const");


        matcher.match(buf, false);

        assertEquals(5, buf.position());
    }

    @Test
    public void givenBytesThatMatchExactlyWithExcess_expectMatch() {
        Matcher    matcher = ConstantMatcher.create("const");
        CharBuffer buf     = CharBuffer.wrap("const123");


        MatchResult result = matcher.match(buf, false);

        assertTrue( result.isMatch() );
        assertEquals( "const", result.getParsedValue());
        assertEquals( 5, buf.position() );
    }

    @Test
    public void givenBytesThatMatchTwiceWithExcess_expectMatch() {
        Matcher    matcher = ConstantMatcher.create("abc");
        CharBuffer buf     = CharBuffer.wrap("abcabc123");


        MatchResult result = matcher.match(buf, false);

        assertTrue( result.isMatch() );
        assertEquals( "abc", result.getParsedValue());
        assertEquals( 3, buf.position() );
    }

    @Test
    public void givenPartialMatchOnEndedStream_expectIncompleteMatch() {
        Matcher    matcher = ConstantMatcher.create("abc");
        CharBuffer buf     = CharBuffer.wrap("ab");


        MatchResult result = matcher.match(buf, false);

        assertTrue(result.isIncompleteMatch());
        assertNull(result.getParsedValue());
        assertEquals( 0, buf.position() );
    }

    @Test
    public void givenBytesThatWillNotMatch_expectFailure() {
        Matcher    matcher = ConstantMatcher.create("abc");
        CharBuffer buf     = CharBuffer.wrap("abd");


        MatchResult result = matcher.match(buf, false);

        assertTrue(result.isNoMatch());
        assertNull(result.getParsedValue());
        assertEquals( 0, buf.position() );
    }

    @Test
    public void givenMatchWithinString() {
        Matcher    matcher = ConstantMatcher.create("bc");
        CharBuffer buf     = CharBuffer.wrap("abcdef");
        buf.position(1);

        MatchResult result = matcher.match(buf, false);

        assertTrue(result.isMatch());
        assertEquals("bc", result.getParsedValue());
        assertEquals( 3, buf.position() );
    }

    @Test
    public void givenSingleCharMatchWithinString() {
        Matcher    matcher = ConstantMatcher.create("=");
        CharBuffer buf     = CharBuffer.wrap("assert foo == bar;");
        buf.position(11);

        MatchResult result = matcher.match(buf, false);

        assertTrue(result.isMatch());
        assertEquals("=", result.getParsedValue());
        assertEquals( 12, buf.position() );
    }

    @Test
    public void givenMatchWithinStringButStartFromBegining_expectNoMatch() {
        Matcher    matcher = ConstantMatcher.create("bc");
        CharBuffer buf     = CharBuffer.wrap("abcdef");
        buf.position(0);

        MatchResult result = matcher.match(buf, false);

        assertTrue(result.isNoMatch());
        assertEquals(null, result.getParsedValue());
        assertEquals(0, buf.position());
    }

}