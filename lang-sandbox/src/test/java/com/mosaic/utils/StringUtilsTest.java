package com.mosaic.utils;

import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
@SuppressWarnings("StringEquality")
public class StringUtilsTest {

    @Test
    public void join() {
        assertEquals( "", StringUtils.join(null,",") );
        assertEquals( "", StringUtils.join(Arrays.asList(),",") );
        assertEquals( "a", StringUtils.join(Arrays.asList('a'),",") );
        assertEquals( "a,b,c", StringUtils.join(Arrays.asList('a', 'b', 'c'),",") );
    }

    @Test
    public void trimRight() {
        assertEquals( "", StringUtils.trimRight("") );
        assertEquals( "", StringUtils.trimRight("    ") );
        assertEquals( "", StringUtils.trimRight("   \t \t ") );
        assertEquals( "  foo", StringUtils.trimRight("  foo \t \t ") );
        assertEquals( "  foo", StringUtils.trimRight("  foo") );
        assertTrue( "  foo" == StringUtils.trimRight("  foo") );
    }


    @Benchmark(durationResultMultiplier = 1.0/8)
    public void trimRightBenchmark() {
        StringUtils.trimRight( "" );
        StringUtils.trimRight( "    " );
        StringUtils.trimRight( "foo" );
        StringUtils.trimRight( "foo" );
        StringUtils.trimRight( "foo bar" );
        StringUtils.trimRight( "foo bar  " );
        StringUtils.trimRight( "foo bar  " );
        StringUtils.trimRight( "foo bar  " );
    }

    @Test
    public void removePostFix() {
        assertEquals( "foo", StringUtils.removePostFix("foo", "bar") );
        assertEquals( "foo", StringUtils.removePostFix("foobar", "bar") );
        assertEquals( "", StringUtils.removePostFix("", "bar") );
        assertEquals( null, StringUtils.removePostFix(null, "bar") );
    }

    @Test
    public void upto() {
        assertEquals( "", StringUtils.upto("", '#') );
        assertEquals( "foo", StringUtils.upto("foo", '#') );
        assertEquals( "foo  ", StringUtils.upto("foo  ", '#') );
        assertTrue( "foo  " == StringUtils.upto("foo  ", '#') );
        assertEquals( null, StringUtils.upto(null, '#') );
        assertEquals( "foo  ", StringUtils.upto("foo  # foo bar", '#') );
        assertEquals( "foo  ", StringUtils.upto("foo  ## foo bar", '#') );
        assertEquals( "foo  ", StringUtils.upto("foo  #", '#') );
    }

}
