package com.mosaic.parsers.matchers;

import com.mosaic.parsers.MatchResult;
import com.mosaic.parsers.Matcher;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class MatcherAsserts {

    public static void assertNoMatch(MatchResult result) {
        assertTrue( "expected 'noMatch', was '"+result+"'", result.isNoMatch() );
        assertNull(result.getNextMatcher());
        assertNull( result.getParsedValue() );
        assertNull(result.getContinuation());
        assertEquals( 0, result.getMatchIndexOnError() );
        assertNull( result.getErrorMessage() );
        assertFalse( result.hasParsedValue() );
    }

    public static void assertIncompleteMatch(MatchResult result) {
        assertTrue( "expected 'incomplete', was '"+result+"'", result.isIncompleteMatch() );
        assertNull(result.getNextMatcher());
        assertNull( result.getParsedValue() );
        assertNull(result.getContinuation());
        assertEquals( 0, result.getMatchIndexOnError() );
        assertNull( result.getErrorMessage() );
        assertFalse( result.hasParsedValue() );
    }

    public static void assertContinuation( MatchResult result, Matcher expectedNextMatcher ) {
        assertTrue( "expected 'continuation', was '"+result+"'", result.isContinuation() );
        assertSame(expectedNextMatcher, result.getNextMatcher());
        assertNull( result.getParsedValue() );
        assertNotNull(result.getContinuation());
        assertEquals( 0, result.getMatchIndexOnError() );
        assertNull( result.getErrorMessage() );
        assertFalse( result.hasParsedValue() );
    }

    public static void assertMatch( MatchResult result, int expectedNumCharactersConsumed, Object expectedParsedValue ) {
        assertTrue( "expected 'match', was '"+result+"'", result.isMatch() );
        assertNull(result.getNextMatcher());
        assertEquals(expectedParsedValue, result.getParsedValue());
        assertNull(result.getContinuation());
        assertEquals( 0, result.getMatchIndexOnError() );
        assertNull( result.getErrorMessage() );
        assertEquals( expectedNumCharactersConsumed, result.getNumCharactersConsumed() );
        assertTrue( result.hasParsedValue() );
    }

    public static void assertMatchWithSkippableContents( MatchResult result, int expectedNumCharactersConsumed ) {
        assertTrue( "expected 'match', was '"+result+"'", result.isMatch() );
        assertNull(result.getNextMatcher());
        assertNull(result.getParsedValue());
        assertNull(result.getContinuation());
        assertEquals( 0, result.getMatchIndexOnError() );
        assertNull( result.getErrorMessage() );
        assertEquals( expectedNumCharactersConsumed, result.getNumCharactersConsumed() );
        assertFalse( result.hasParsedValue() );
    }

    public static void assertError( MatchResult result, int expectedOffset, String expectedErrorMessage ) {
        assertTrue( "expected 'error', was '"+result+"'", result.isError() );
        assertNull(result.getNextMatcher());
        assertNull(result.getParsedValue());
        assertNull(result.getContinuation());
        assertEquals( expectedOffset, result.getMatchIndexOnError() );
        assertEquals( expectedErrorMessage, result.getErrorMessage() );
        assertFalse( result.hasParsedValue() );
    }
    
}
