package com.mosaic.parsers.push2.matchers;

import com.mosaic.io.CharacterStream;
import com.mosaic.io.Characters;
import com.mosaic.parsers.push2.MatchResult;
import com.mosaic.parsers.push2.Matcher;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class ConstantMatcherTest {

    @Test
    public void givenNullTargetString_expectException() {
        try {
            new ConstantMatcher( null );
            fail( "Expected NullPointerException" );
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void givenEmptyTargetString_expectException() {
        try {
            new ConstantMatcher( "" );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'targetString.length()' (0) must be >= 1", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBytes_expectFailedMatch() {
        CharacterStream stream  = new CharacterStream("");
        Matcher<String> matcher = new ConstantMatcher( "const" ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasErrored() );
    }

    @Test
    public void givenBytesThatMatchExactly_expectMatch() {
        CharacterStream stream  = new CharacterStream("const");
        Matcher<String> matcher = new ConstantMatcher( "const" ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertEquals( "const", result.getResult() );
    }

    @Test
    public void givenBytesThatMatchExactlyWithExcess_expectMatch() {
        CharacterStream stream  = new CharacterStream("const123");
        Matcher<String> matcher = new ConstantMatcher( "const" ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertEquals( "const", result.getResult() );
        assertEquals( "123", stream.toString() );
    }

    @Test
    public void givenBytesThatMatchTwiceWithExcess_expectMatch() {
        CharacterStream stream  = new CharacterStream("abcabc123");
        Matcher<String> matcher = new ConstantMatcher( "abc" ).withInputStream( stream );

        MatchResult<String> result1 = matcher.processInput();

        assertEquals( "abc", result1.getResult() );
        assertEquals( "abc123", stream.toString() );


        MatchResult<String> result2 = matcher.processInput();

        assertTrue( result2.hasResult() );
        assertEquals( "abc", result2.getResult() );

        assertEquals( "123", stream.toString() );
    }

    @Test
    public void givenBytesThatMatchOverTwoCalls_expectMatchOnSecond() {
        Characters      input1  = Characters.wrapString( "ab" );
        Characters      input2  = Characters.wrapString("c");

        CharacterStream stream  = new CharacterStream();
        Matcher<String> matcher = new ConstantMatcher( "abc" ).withInputStream( stream );

        stream.appendCharacters( input1 );

        MatchResult<String> result1 = matcher.processInput();

        assertTrue( result1.hasErrored() );
        assertEquals( null, result1.getResult() );
        assertEquals( "expected 'abc'", result1.getFailedToMatchDescription() );
        assertEquals( "ab", stream.toString() );


        stream.appendCharacters( input2 );

        MatchResult<String> result2 = matcher.processInput();

        assertEquals( "abc", result2.getResult() );
        assertEquals( "", stream.toString() );
        assertTrue( result2.hasResult() );
    }

    @Test
    public void givenBytesThatWillNotMatch_expectFailure() {
        CharacterStream stream  = new CharacterStream("d");
        Matcher<String> matcher = new ConstantMatcher( "abc" ).withInputStream( stream );

        MatchResult<String> result = matcher.processInput();

        assertTrue( result.hasErrored() );
        assertEquals( "expected 'abc'", result.getFailedToMatchDescription() );
        assertEquals( "d", stream.toString() );
    }

}
