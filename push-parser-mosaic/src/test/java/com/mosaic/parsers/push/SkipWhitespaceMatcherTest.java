package com.mosaic.parsers.push;

import com.mosaic.io.Characters;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class SkipWhitespaceMatcherTest {

    @Test
    public void givenNullTargetString_expectException() {
        try {
            Matchers.skipWhitespace( null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'wrappedMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBytes_expectFailedToMatch() {
        Characters      input   = Characters.wrapString("");
        Matcher<String> matcher = Matchers.skipWhitespace( Matchers.constant("a") );

        Matcher<String> result = matcher.processCharacters( input ).processEndOfStream();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'a'", result.getFailedToMatchDescription() );
    }

    @Test
    public void givenMatchingBytes_expectMatch() {
        Characters      input   = Characters.wrapString("a");
        Matcher<String> matcher = Matchers.skipWhitespace( Matchers.constant("a") );

        Matcher<String> result = matcher.processCharacters( input );

        assertEquals( 0, result.getLineNumber() );
        assertEquals( 0, result.getColumnNumber() );
        assertEquals( 0, result.getCharacterOffset() );
        assertEquals( "", result.getRemainingCharacters().toString() );

        assertEquals( "a", result.getResult() );
    }

    @Test
    public void givenMatchingBytesPrefixedWithWhitespace_expectMatch() {
        Characters      input   = Characters.wrapString("  a");
        Matcher<String> matcher = Matchers.skipWhitespace( Matchers.constant("a") );

        Matcher<String> result = matcher.processCharacters( input );

        assertEquals( "a", result.getResult() );

        assertTrue( result.hasResult() );

        assertEquals( 0, result.getLineNumber() );
        assertEquals( 2, result.getColumnNumber() );
        assertEquals( 2, result.getCharacterOffset() );
        assertEquals( "", result.getRemainingCharacters().toString() );
    }

    @Test
    public void givenWhitespace_expectNoMatch() {
        Characters      input   = Characters.wrapString("   ");
        Matcher<String> matcher = Matchers.skipWhitespace( Matchers.constant("a") );

        Matcher<String> result = matcher.processCharacters( input );

        assertEquals( null, result.getResult() );

        assertEquals(  0, result.getLineNumber() );
        assertEquals(  0, result.getColumnNumber() );
        assertEquals(  0, result.getCharacterOffset() );
        assertEquals( "", result.getRemainingCharacters().toString() );
    }

    @Test
    public void givenWhitespace_expectWhitespaceToBeConsumedByMatcherToStillBeParsing() {
        Characters      input   = Characters.wrapString("   ");
        Matcher<String> matcher = Matchers.skipWhitespace( Matchers.constant("a") );

        Matcher<String> result = matcher.processCharacters( input );

        assertEquals( null, result.getResult() );
        assertTrue( result.isAwaitingInput() );

        assertEquals(  0, result.getLineNumber() );
        assertEquals(  0, result.getColumnNumber() );
        assertEquals(  0, result.getCharacterOffset() );
        assertEquals( "", result.getRemainingCharacters().toString() );
    }

    @Test
    public void givenTwoGroupsOfWhitespace_expectWhitespaceToBeConsumedByMatcherToStillBeParsing() {
        Characters      input1   = Characters.wrapString("   ");
        Characters      input2   = Characters.wrapString("   ");
        Matcher<String> matcher = Matchers.skipWhitespace( Matchers.constant("a") );

        Matcher<String> result = matcher.processCharacters( input1 ).processCharacters( input2 );

        assertEquals( null, result.getResult() );
        assertTrue( result.isAwaitingInput() );

        assertEquals(  0, result.getLineNumber() );
        assertEquals(  0, result.getColumnNumber() );
        assertEquals(  0, result.getCharacterOffset() );
        assertEquals( "", result.getRemainingCharacters().toString() );
    }

}
