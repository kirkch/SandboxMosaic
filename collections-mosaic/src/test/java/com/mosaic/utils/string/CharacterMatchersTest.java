package com.mosaic.utils.string;

import org.junit.Test;

import static junit.framework.Assert.*;

/**
 *
 */
public class CharacterMatchersTest {
    @Test
    public void testJdkRegexp() {
        CharacterMatcher matcher = CharacterMatchers.jdkRegexp( "[0-9]+" );

        assertEquals( 3, matcher.consumeFrom( "123", 0, 3 ) );
        assertEquals( 2, matcher.consumeFrom( "123", 1, 3 ) );
        assertEquals( 2, matcher.consumeFrom( "a23", 1, 3 ) );
        assertEquals( 1, matcher.consumeFrom( "a23", 1, 2 ) );
        assertEquals( 0, matcher.consumeFrom( "a23", 0, 3 ) );
    }

    @Test
    public void testConstantMatcher() {
        CharacterMatcher matcher = CharacterMatchers.constant( "rar" );

        assertEquals( 0, matcher.consumeFrom( "1", 0, 3 ) );
        assertEquals( 0, matcher.consumeFrom( "123", 0, 3 ) );
        assertEquals( 3, matcher.consumeFrom( "rar", 0, 3 ) );
        assertEquals( 0, matcher.consumeFrom( "rad", 0, 3 ) );
        assertEquals( 0, matcher.consumeFrom( "1rar", 0, 3 ) );
        assertEquals( 0, matcher.consumeFrom( "1rar", 0, 4 ) );
        assertEquals( 3, matcher.consumeFrom( "1rar", 1, 4 ) );
        assertEquals( 3, matcher.consumeFrom( "1rar ", 1, 5 ) );
        assertEquals( 3, matcher.consumeFrom( "rar rar", 0, 5 ) );
        assertEquals( 0, matcher.consumeFrom( "rar rar", 1, 4 ) );
        assertEquals( 0, matcher.consumeFrom( "rrad rar", 1, 4 ) );
    }

    @Test
    public void testWhitespaceMatcher() {
        CharacterMatcher matcher = CharacterMatchers.whitespace();

        assertEquals( 0, matcher.consumeFrom( "foo  \t bar\r\n rar", 0, 19 ) );
        assertEquals( 4, matcher.consumeFrom( "foo  \t bar\r\n rar", 3, 19 ) );
        assertEquals( 3, matcher.consumeFrom( "foo  \t bar\r\n rar", 4, 19 ) );
        assertEquals( 2, matcher.consumeFrom( "foo  \t bar\r\n rar", 5, 19 ) );
        assertEquals( 3, matcher.consumeFrom( "foo  \t bar\r\n rar", 10, 19 ) );
    }

    @Test
    public void testEverythingExceptMatcher() {
        CharacterMatcher matcher = CharacterMatchers.everythingExcept( 'o' );

        assertEquals( 1, matcher.consumeFrom( "foo  \t bar\r\n rar", 0, 15 ) );
        assertEquals( 0, matcher.consumeFrom( "foo  \t bar\r\n rar", 1, 15 ) );
        assertEquals( 12, matcher.consumeFrom( "foo  \t bar\r\n rar", 3, 15 ) );
        assertEquals( 0, matcher.consumeFrom( "foo  \t bar\r\n rar", 18, 15 ) );
        assertEquals( 0, matcher.consumeFrom( "foo  \t bar\r\n rar", 19, 15 ) );
    }

    @Test
    public void testJavaVariableNameMatcher() {
        CharacterMatcher matcher = CharacterMatchers.javaVariableName();

        assertEquals( 0, matcher.consumeFrom( " a ", 0, 3 ) );
        assertEquals( 1, matcher.consumeFrom( " a ", 1, 3 ) );
        assertEquals( 1, matcher.consumeFrom( " a.b.c ", 1, 7 ) );

        assertEquals( 0, matcher.consumeFrom( " $abc ", 0, 5 ) );
        assertEquals( 0, matcher.consumeFrom( " $abc ", 1, 5 ) );
        assertEquals( 4, matcher.consumeFrom( " a$bc ", 1, 5 ) );
        assertEquals( 3, matcher.consumeFrom( " $abc ", 2, 5 ) );
        assertEquals( 2, matcher.consumeFrom( " $abc ", 2, 4 ) );
        assertEquals( 1, matcher.consumeFrom( " $abc ", 2, 3 ) );

        assertEquals( 4, matcher.consumeFrom( "_foo", 0, 4 ) );
        assertEquals( 0, matcher.consumeFrom( "123abc", 0, 6 ) );
        assertEquals( 3, matcher.consumeFrom( "123abc", 3, 6 ) );
        assertEquals( 10, matcher.consumeFrom( "flex2beast", 0, 10 ) );
    }

    @Test
    public void testJavaVariableTypeMatcher() {
        CharacterMatcher matcher = CharacterMatchers.javaVariableType();

        assertEquals( 0, matcher.consumeFrom( " a ", 0, 3 ) );
        assertEquals( 1, matcher.consumeFrom( " a ", 1, 3 ) );
        assertEquals( 5, matcher.consumeFrom( " a.b.c ", 1, 7 ) );
        assertEquals( 9, matcher.consumeFrom( " a.b.c.Foo ", 1, 10 ) );

        assertEquals( 0, matcher.consumeFrom( " $abc ", 0, 5 ) );
        assertEquals( 0, matcher.consumeFrom( " $abc ", 1, 5 ) );
        assertEquals( 3, matcher.consumeFrom( " $abc ", 2, 5 ) );
        assertEquals( 2, matcher.consumeFrom( " $abc ", 2, 4 ) );
        assertEquals( 1, matcher.consumeFrom( " $abc ", 2, 3 ) );

        assertEquals( 4, matcher.consumeFrom( "_foo", 0, 4 ) );
        assertEquals( 0, matcher.consumeFrom( "123abc", 0, 6 ) );
        assertEquals( 3, matcher.consumeFrom( "123abc", 3, 6 ) );
        assertEquals( 10, matcher.consumeFrom( "flex2beast", 0, 10 ) );
    }

//    @Test
//    public void timeConstantMatcherIsFasterThanRegexp() {
//        CharacterMatcher regexpMatcher   = CharacterMatchers.jdkRegexp( "rar" );
//        CharacterMatcher constantMatcher = CharacterMatchers.constant( "rar" );
//
//        time( constantMatcher ); // warm jvm
//        time( regexpMatcher );   // warm jvm
//
//        double constantMatcherDuration = time( constantMatcher );
//        double regexpMatcherTime = time( regexpMatcher );
//
//        assertTrue( constantMatcherDuration + " < " + regexpMatcherTime, constantMatcherDuration < regexpMatcherTime );
//
//        // NB at time of writing, constant matcher is 10 times faster than regexp when hotspot was cold, and 2-5 times
//        // faster after hotspot was warmed.
//
//        // Java version "1.6.0_26" on OSx 10.6.8, mac book pro dual 2Ghz i7
//        // Java(TM) SE Runtime Environment (build 1.6.0_26-b03-384-10M3425)
//
//
////        for ( int i=0; i<5; i++ ) {
////            System.out.println( "time(regexpMatcher) = " + time( regexpMatcher ) );
////            System.out.println( "time(constant) = " + time( constantMatcher ) );
////        }
//    }

    @Test
    public void testConsumeUptoNCharactersMatcher() {
        CharacterMatcher matcher = CharacterMatchers.consumeUptoNCharacters( 5 );

        assertEquals( 5, matcher.consumeFrom( "foo  \t bar\r\n rar", 0, 15 ) );
        assertEquals( 5, matcher.consumeFrom( "foo  \t bar\r\n rar", 1, 15 ) );
        assertEquals( 5, matcher.consumeFrom( "foo  \t bar\r\n rar", 3, 15 ) );
        assertEquals( 2, matcher.consumeFrom( "foo  \t bar\r\n rar", 13, 15 ) );
        assertEquals( 1, matcher.consumeFrom( "foo  \t bar\r\n rar", 14, 15 ) );
        assertEquals( 0, matcher.consumeFrom( "foo  \t bar\r\n rar", 18, 15 ) );
        assertEquals( 0, matcher.consumeFrom( "foo  \t bar\r\n rar", 19, 15 ) );
        assertEquals( 4, matcher.consumeFrom( "foo  \t bar\r\n rar", 0, 4 ) );
        assertEquals( 5, matcher.consumeFrom( "foo  \t bar\r\n rar", 0, 5 ) );
    }


//    private double time( CharacterMatcher matcher ) {
//        StopWatch sw = new StopWatch();
//
//        sw.start();
//        for ( int i=0; i<1000; i++ ) {
//            assertEquals( 0, matcher.consumeFrom( "1", 0, 1 ) );
//            assertEquals( 0, matcher.consumeFrom( "123", 0, 3 ) );
//            assertEquals( 3, matcher.consumeFrom( "rar", 0, 3 ) );
//            assertEquals( 0, matcher.consumeFrom( "1rar", 0, 3 ) );
//            assertEquals( 0, matcher.consumeFrom( "1rar", 0, 4 ) );
//            assertEquals( 3, matcher.consumeFrom( "1rar", 1, 4 ) );
//            assertEquals( 3, matcher.consumeFrom( "1rar ", 1, 5 ) );
//        }
//
//        sw.stop();
//
//        return sw.getNanoTime()/1000000.0;
//    }
}
