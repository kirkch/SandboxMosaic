package com.mosaic.utils;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class StringUtilsTest {

    @Test
    public void join() {
        assertEquals( "", StringUtils.join(null,",") );
        assertEquals( "", StringUtils.join(Arrays.asList(),",") );
        assertEquals( "a", StringUtils.join(Arrays.asList('a'),",") );
        assertEquals( "a,b,c", StringUtils.join(Arrays.asList('a', 'b', 'c'),",") );
    }

}
