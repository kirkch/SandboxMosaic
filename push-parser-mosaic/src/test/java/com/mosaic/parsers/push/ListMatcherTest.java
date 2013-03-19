package com.mosaic.parsers.push;

import com.mosaic.io.Characters;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class ListMatcherTest {

    @Test
    public void givenNullElementMatcher_expectException() {
        try {
            Matchers.list( null, Matchers.constant(","), Matchers.constant(";") );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'elementMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenNullSeperatingMatcher_expectException() {
        try {
            Matchers.list( Matchers.constant("e"), null, Matchers.constant(";") );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'seperatingMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenNullEndOfListMatcher_expectException() {
        try {
            Matchers.list( Matchers.constant("e"), Matchers.constant(","), null );
            fail( "Expected IllegalArgumentException" );
        } catch (IllegalArgumentException e) {
            assertEquals( "'endOfListMatcher' must not be null", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyBytes_expectSameInstanceOfMatcherBack() {
        Characters            input   = Characters.wrapString("");
        Matcher<List<String>> matcher = Matchers.list( Matchers.constant("e"), Matchers.constant(","), Matchers.constant(";") );

        Matcher<List<String>> result = matcher.processCharacters( input );

        assertTrue( result == matcher );
    }

    @Test
    public void givenBytesThatErrorOnFirstElement_expectFailedMatch() {
        Characters            input   = Characters.wrapString("z");
        Matcher<List<String>> matcher = Matchers.list( Matchers.constant("e"), Matchers.constant(","), Matchers.constant(";") );

        Matcher<List<String>> result = matcher.processCharacters( input );

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'e'", result.getFailedToMatchDescription() );
    }

    @Test
    public void givenBytesThatPartiallyMatchFirstElement_expectInProgress() {
        Characters            input   = Characters.wrapString("e");
        Matcher<List<String>> matcher = Matchers.list( Matchers.constant("e1"), Matchers.constant(","), Matchers.constant(";") );

        Matcher<List<String>> result = matcher.processCharacters( input );

        assertTrue( result.isAwaitingInput() );
        assertEquals( "e", result.getRemainingCharacters().toString() );
    }

    @Test
    public void givenBytesThatPartiallyMatchFirstElement_thenEndStream_expectFailedMatch() {
        Characters            input   = Characters.wrapString("e");
        Matcher<List<String>> matcher = Matchers.list( Matchers.constant("e1"), Matchers.constant(","), Matchers.constant(";") );

        Matcher<List<String>> result = matcher.processCharacters( input ).processEndOfStream();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( "expected 'e1'", result.getFailedToMatchDescription() );
    }

    @Test
    public void givenBytesThatFullyMatchesFirstElementButDoesNotTerminateList_expectInProgress() {
        Characters            input   = Characters.wrapString("e1");
        Matcher<List<String>> matcher = Matchers.list( Matchers.constant("e1"), Matchers.constant(","), Matchers.constant(";") );

        Matcher<List<String>> result = matcher.processCharacters( input );

        assertTrue( result.isAwaitingInput() );
        assertEquals( "", result.getRemainingCharacters().toString() );
    }

//    @Test
    public void givenBytesThatFullyMatchesFirstElement_thenEndOfStream_expectMatch() {
        Characters            input   = Characters.wrapString("e1");
        Matcher<List<String>> matcher = Matchers.list( Matchers.constant("e1"), Matchers.constant(","), Matchers.constant(";") );

        Matcher<List<String>> result = matcher.processCharacters(input).processEndOfStream();

        assertTrue( result.hasResult() );
        assertEquals( "", result.getRemainingCharacters().toString() );
        assertEquals( Arrays.asList("e1"), result.getRemainingCharacters().toString() );
    }

//
//
//
//
// givenBytesThatFullyMatchesFirstElement_thenEndOfListMatch_expectMatch

// givenBytesThatOneElementAndSeperator_expectInProgress
// givenBytesThatOneElementAndSeperator_thenEndStream_expectFailedMatch

// givenBytesThatOneElementAndSeperatorThenElement_expectInProgress
// givenBytesThatOneElementAndSeperatorThenElement_thenEndStream_expectTwoElementResult
// givenBytesThatOneElementAndSeperatorThenElement_thenEndOfListMatch_expectTwoElementResult

// givenBytesThreeSeperatedElements_expectInProgress
// givenBytesThreeSeperatedElements_thenEndStream_expectThreeElementResult
// givenBytesThreeSeperatedElements_thenEndOfListMatch_expectThreeElementResult



}
