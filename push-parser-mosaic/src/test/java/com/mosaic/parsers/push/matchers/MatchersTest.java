package com.mosaic.parsers.push.matchers;

import com.mosaic.io.CharPosition;
import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.MatchResult;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class MatchersTest {

    @Test
    public void listDemarcated() {
        CharacterStream stream = new CharacterStream( "(e1,e1,e1)" ).appendEOS();
        Matcher<List<String>> matcher = Matchers.listDemarcated( new ConstantMatcher( "(" ), new ConstantMatcher( "e1" ), new ConstantMatcher( "," ), new ConstantMatcher( ")" ) );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList( "e1", "e1", "e1" ), result.getResult() );
        assertEquals( new CharPosition(0,10,10), stream.getPosition() );
    }

    @Test
    public void listUndemarcated() {
        CharacterStream stream = new CharacterStream( "e1,e1,e1" ).appendEOS();
        Matcher<List<String>> matcher = Matchers.listUndemarcated( new ConstantMatcher("e1"), new ConstantMatcher(",") );
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( Arrays.asList( "e1", "e1", "e1" ), result.getResult() );
        assertEquals( new CharPosition(0,8,8), stream.getPosition() );
    }

    @Test
    public void eolRN() {
        CharacterStream stream = new CharacterStream( "\r\n" ).appendEOS();
        Matcher<List<String>> matcher = Matchers.eol();
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( null, result.getResult() );
        assertEquals( "", stream.toString() );
        assertEquals( 0, stream.markCount() );
    }

    @Test
    public void eolN() {
        CharacterStream stream = new CharacterStream( "\n" ).appendEOS();
        Matcher<List<String>> matcher = Matchers.eol();
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( null, result.getResult() );
        assertEquals( "", stream.toString() );
        assertEquals( 0, stream.markCount() );
    }

    @Test
    public void eolEOF() {
        CharacterStream stream = new CharacterStream( "" ).appendEOS();

        Matcher<List<String>> matcher = Matchers.eol();
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasResult() );
        assertEquals( null, result.getResult() );
        assertEquals( "", stream.toString() );
        assertEquals( 0, stream.markCount() );
    }

    @Test
    public void eolABC() {
        CharacterStream stream = new CharacterStream( "abc" ).appendEOS();
        Matcher<List<String>> matcher = Matchers.eol();
        matcher.withInputStream( stream );

        MatchResult<List<String>> result = matcher.processInput();

        assertTrue( result.hasFailedToMatch() );
        assertEquals( null, result.getResult() );
        assertEquals( "abc", stream.toString() );
        assertEquals( 0, stream.markCount() );
    }

//    @Test
//    public void repeatedGreedy() {
//        CharacterStream stream = new CharacterStream( "e1e1e1" ).appendEOS();
//        Matcher<List<String>> matcher = Matchers.repeatedGreedy( new ConstantMatcher("e1") );
//        matcher.withInputStream( stream );
//
//        MatchResult<List<String>> result = matcher.processInput();
//
//        assertTrue( result.hasResult() );
//        assertEquals( Arrays.asList( "e1", "e1", "e1" ), result.getResult() );
//        assertEquals( new CharPosition(0,6,6), stream.getPosition() );
//    }

}
