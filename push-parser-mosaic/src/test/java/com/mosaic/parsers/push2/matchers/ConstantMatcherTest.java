package com.mosaic.parsers.push2.matchers;

import com.mosaic.io.Characters;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class ConstantMatcherTest {

//    @Test
//    public void givenNullTargetString_expectException() {
//        try {
//            Matchers.constant( null );
//            fail( "Expected NullPointerException" );
//        } catch (NullPointerException e) {
//
//        }
//    }
//
//    @Test
//    public void givenEmptyTargetString_expectException() {
//        try {
//            Matchers.constant( "" );
//            fail( "Expected IllegalArgumentException" );
//        } catch (IllegalArgumentException e) {
//            assertEquals( "'targetString.length()' (0) must be >= 1", e.getMessage() );
//        }
//    }
//
//    @Test
//    public void givenEmptyBytes_expectSameInstanceOfMatcherBack() {
//        Characters      input   = Characters.wrapString("");
//        Matcher<String> matcher = Matchers.constant( "const" );
//
//        Matcher<String> result = matcher.processCharacters( input );
//
//        assertTrue( result == matcher );
//    }
//
//    @Test
//    public void givenBytesThatMatchExactly_expectMatch() {
//        Characters      input   = Characters.wrapString("const");
//        Matcher<String> matcher = Matchers.constant( "const" );
//
//        Matcher<String> result = matcher.processCharacters( input );
//
//        assertEquals( 0, result.getLineNumber() );
//        assertEquals( 0, result.getColumnNumber() );
//        assertEquals( 0, result.getCharacterOffset() );
//        assertEquals( 0, result.getRemainingCharacters().length() );
//
//        assertEquals( "const", result.getResult() );
//    }
//
//    @Test
//    public void givenBytesThatMatchExactlyWithExcess_expectMatch() {
//        Characters      input   = Characters.wrapString("const123");
//        Matcher<String> matcher = Matchers.constant( "const" );
//
//        Matcher<String> result = matcher.processCharacters( input );
//
//        assertEquals( 0, result.getLineNumber() );
//        assertEquals( 0, result.getColumnNumber() );
//        assertEquals( 0, result.getCharacterOffset() );
//        assertEquals( "123", result.getRemainingCharacters().toString() );
//
//        assertEquals( "const", result.getResult() );
//    }
//
//    @Test
//    public void givenBytesThatMatchTwiceWithExcess_expectMatch() {
//        Characters      input   = Characters.wrapString("abcabc123");
//        Matcher<String> matcher = Matchers.constant( "abc" );
//
//        Matcher<String> result1 = matcher.processCharacters( input );
//
//        assertEquals( 0, result1.getLineNumber() );
//        assertEquals( 0, result1.getColumnNumber() );
//        assertEquals( 0, result1.getCharacterOffset() );
//        assertEquals( "abc123", result1.getRemainingCharacters().toString() );
//
//        assertEquals( "abc", result1.getResult() );
//
//        Matcher<String> result2 = matcher.processCharacters( result1.getRemainingCharacters() );
//
//        assertEquals( 0, result2.getLineNumber() );
//        assertEquals( 3, result2.getColumnNumber() );
//        assertEquals( 3, result2.getCharacterOffset() );
//        assertEquals( "123", result2.getRemainingCharacters().toString() );
//
//        assertTrue( result2.hasCompleted() );
//        assertTrue( result2.hasResult() );
//        assertEquals( "abc", result2.getResult() );
//    }
//
//    @Test
//    public void givenBytesThatMatchOverTwoCalls_expectMatchOnSecond() {
//        Characters      input1  = Characters.wrapString("ab");
//        Characters      input2  = Characters.wrapString("c");
//        Matcher<String> matcher = Matchers.constant( "abc" );
//
//        Matcher<String> result1 = matcher.processCharacters( input1 );
//
//        assertEquals( 0, result1.getLineNumber() );
//        assertEquals( 0, result1.getColumnNumber() );
//        assertEquals( 0, result1.getCharacterOffset() );
//        assertEquals( "ab", result1.getRemainingCharacters().toString() );
//        assertEquals( null, result1.getResult() );
//        assertFalse( result1.hasCompleted() );
//
//        Matcher<String> result2 = result1.processCharacters( input2 );
//
//        assertEquals( "abc", result2.getResult() );
//        assertEquals( 0, result2.getLineNumber() );
//        assertEquals( 0, result2.getColumnNumber() );
//        assertEquals( 0, result2.getCharacterOffset() );
//        assertEquals( "", result2.getRemainingCharacters().toString() );
//
//        assertTrue( result2.hasCompleted() );
//        assertEquals( 3, result2.getRemainingCharacters().getColumnNumber() );
//    }
//
//    @Test
//    public void givenBytesThatWillNotMatch_expectFailure() {
//        Characters      input   = Characters.wrapString("d");
//        Matcher<String> matcher = Matchers.constant( "abc" );
//
//        Matcher<String> result = matcher.processCharacters( input );
//
//        assertTrue( result.hasCompleted() );
//        assertTrue( result.hasFailedToMatch() );
//        assertEquals( "expected 'abc'", result.getFailedToMatchDescription() );
//    }
//
//    @Test
//    public void givenFirstPartOfMatch_thenEndStream_expectFailedToMatch() {
//        Characters      input   = Characters.wrapString("ab");
//        Matcher<String> matcher = Matchers.constant( "abc" );
//
//        Matcher<String> result = matcher.processCharacters( input ).processEndOfStream();
//
//        assertTrue( result.hasCompleted() );
//        assertTrue( result.hasFailedToMatch() );
//        assertEquals( "expected 'abc'", result.getFailedToMatchDescription() );
//    }

}
