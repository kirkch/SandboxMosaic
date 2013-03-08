package com.mosaic.parsers.push;

import com.mosaic.io.Characters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class RegExpMatcherTest {

    @Test
    public void givenNullTargetString_expectException() {
        try {
            Matchers.regexp( (String) null );
            fail( "Expected NullPointerException" );
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void givenEmptyTargetString_expectException() {
        try {
            Matchers.regexp( "" );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'regexp.length()' (0) must be >= 1", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBytes_expectSameInstanceOfMatcherBack() {
        Characters      input   = Characters.wrapString("");
        Matcher<String> matcher = Matchers.regexp( "const" );

        Matcher<String> result = matcher.processCharacters( input );

        assertTrue( result == matcher );
    }

    @Test
    public void givenBytesThatMatchExactly_expectMatch() {
        Characters      input   = Characters.wrapString("const ");
        Matcher<String> matcher = Matchers.regexp( "const" );

        Matcher<String> result = matcher.processCharacters( input );

        assertEquals( 0, result.getLineNumber() );
        assertEquals( 0, result.getColumnNumber() );
        assertEquals( 0, result.getCharacterOffset() );
        assertEquals( " ", result.getRemainingCharacters().toString() );

        assertEquals( "const", result.getResult() );
    }

    @Test
    public void givenNumThenAlphaCharacters_matchNumbers_expectNumbersToMatchAndAlphasInRemaining() {
        Characters      input   = Characters.wrapString("0123abc");
        Matcher<String> matcher = Matchers.regexp( "[0-9]+" );

        Matcher<String> result = matcher.processCharacters( input );

        assertEquals( "0123", result.getResult() );

        assertEquals( 0, result.getLineNumber() );
        assertEquals( 0, result.getColumnNumber() );
        assertEquals( 0, result.getCharacterOffset() );
        assertEquals( "abc", result.getRemainingCharacters().toString() );
    }

    @Test
    public void givenAlphaThenNumCharacters_matchNumbers_expectNoMatch() {
        Characters      input   = Characters.wrapString("abc0123");
        Matcher<String> matcher = Matchers.regexp( "[0-9]+" );

        Matcher<String> result = matcher.processCharacters( input );

        assertEquals( null, result.getResult() );

        assertEquals( 0, result.getLineNumber() );
        assertEquals( 0, result.getColumnNumber() );
        assertEquals( 0, result.getCharacterOffset() );
        assertEquals( "abc0123", result.getRemainingCharacters().toString() );
    }

    @Test
    public void givenNumbersInTwoBatchesWithNoOtherCharacter_expectNoMatchAsRegExpIsGreedyAndNotReportedAMatchYet() {
        Characters      input1  = Characters.wrapString("012");
        Characters      input2  = Characters.wrapString("345");
        Matcher<String> matcher = Matchers.regexp( "[0-9]+" );

        Matcher<String> result1 = matcher.processCharacters(input1);
        Matcher<String> result2 = result1.processCharacters(input2);

        assertEquals( null, result1.getResult() );
        assertEquals( null, result2.getResult() );

        assertEquals( 0, result1.getLineNumber() );
        assertEquals( 0, result1.getColumnNumber() );
        assertEquals( 0, result1.getCharacterOffset() );

        assertEquals( "012", result1.getRemainingCharacters().toString() );
        assertEquals( "012345", result2.getRemainingCharacters().toString() );
    }

    @Test
    public void givenNumbersInTwoBatchesWithNoOtherCharacter_expectMatchAfterEndOfStreamHasBeenReported() {
        Characters      input1  = Characters.wrapString("012");
        Characters      input2  = Characters.wrapString("345");
        Matcher<String> matcher = Matchers.regexp( "[0-9]+" );

        Matcher<String> result = matcher.processCharacters(input1).processCharacters(input2).endOfStream();

        assertEquals( "012345", result.getResult() );

        assertEquals( 0, result.getLineNumber() );
        assertEquals( 0, result.getColumnNumber() );
        assertEquals( 0, result.getCharacterOffset() );

        assertEquals( 0, result.getRemainingCharacters().length() );
    }

}
