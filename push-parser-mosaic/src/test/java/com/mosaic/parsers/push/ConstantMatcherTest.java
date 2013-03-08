package com.mosaic.parsers.push;

import com.mosaic.io.Characters;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class ConstantMatcherTest {

    @Test
    public void givenNullTargetString_expectException() {
        try {
            Matchers.constant( null );
            fail( "Expected NullPointerException" );
        } catch (NullPointerException e) {

        }
    }

    @Test
    public void givenEmptyTargetString_expectException() {
        try {
            Matchers.constant( "" );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'targetString.length()' (0) must be >= 1", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBytes_expectSameInstanceOfMatcherBack() {
        Characters      input   = Characters.wrapString("");
        Matcher<String> matcher = Matchers.constant( "const" );

        Matcher<String> result = matcher.processCharacters( input );

        assertTrue( result == matcher );
    }

    @Test
    public void givenBytesThatMatchExactly_expectMatch() {
        Characters      input   = Characters.wrapString("const");
        Matcher<String> matcher = Matchers.constant( "const" );

        Matcher<String> result = matcher.processCharacters( input );

        assertEquals( 0, result.getLineNumber() );
        assertEquals( 0, result.getColumnNumber() );
        assertEquals( 0, result.getCharacterOffset() );
        assertEquals( 0, result.getRemainingCharacters().length() );

        assertEquals( "const", result.getResult() );
    }

}
