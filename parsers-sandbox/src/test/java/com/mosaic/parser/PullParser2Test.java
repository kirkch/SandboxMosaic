package com.mosaic.parser;

import org.junit.Test;

import static com.mosaic.utils.string.CharacterMatchers.constant;
import static org.junit.Assert.*;


public class PullParser2Test {

    @Test
    public void onmismatch_expectNoMatchResult() {
        PullParser2 pp = new PullParser2<>( "hello world", "ws" );

        MatchResult r = pp.pull( constant( "keyword", "world" ) );

        assertFalse( r.isMatch() );
        assertNull( r.getMatchedText() );
        assertNull( r.getMatchedType() );
    }

    @Test
    public void matchThreeWordsInSequence() {
        PullParser2 pp = new PullParser2<>( "hello world", "ws" );

        MatchResult r1 = pp.pull( constant( "keyword", "hello" ) );
        MatchResult r2 = pp.pull( constant( "keyword", " " ) );
        MatchResult r3 = pp.pull( constant( "keyword", "world" ) );

        assertTrue( r1.isMatch() );
        assertEquals( "hello", r1.getMatchedText() );
        assertEquals( "keyword", r1.getMatchedType() );
        assertEquals( 0, r1.getPosition().getColumnNumber() );
        assertEquals( 0, r1.getPosition().getLineNumber() );
        assertEquals( 0, r1.getPosition().getCharacterOffset() );

        assertFalse( r2.isMatch() );  // whitespace would have been skipped already

        assertTrue( r3.isMatch() );
        assertEquals( "world", r3.getMatchedText() );
        assertEquals( "keyword", r3.getMatchedType() );
        assertEquals( 6, r3.getPosition().getColumnNumber() );
        assertEquals( 0, r3.getPosition().getLineNumber() );
        assertEquals( 6, r3.getPosition().getCharacterOffset() );
    }

    @Test
    public void matchOverALineBoundary() {
        PullParser2 pp = new PullParser2<>( "hello\nworld", "ws" );

        MatchResult r1 = pp.pull( constant( "keyword", "hello" ) );
        MatchResult r2 = pp.pull( constant( "keyword", "world" ) );

        assertTrue( r1.isMatch() );
        assertEquals( "hello", r1.getMatchedText() );
        assertEquals( "keyword", r1.getMatchedType() );
        assertEquals( 0, r1.getPosition().getColumnNumber() );
        assertEquals( 0, r1.getPosition().getLineNumber() );
        assertEquals( 0, r1.getPosition().getCharacterOffset() );

        assertTrue( r2.isMatch() );
        assertEquals( "world", r2.getMatchedText() );
        assertEquals( "keyword", r2.getMatchedType() );
        assertEquals( 0, r2.getPosition().getColumnNumber() );
        assertEquals( 1, r2.getPosition().getLineNumber() );
        assertEquals( 6, r2.getPosition().getCharacterOffset() );
    }

}