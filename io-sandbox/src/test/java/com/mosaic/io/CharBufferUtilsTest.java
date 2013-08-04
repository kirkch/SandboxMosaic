package com.mosaic.io;

import org.junit.Test;

import java.nio.CharBuffer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class CharBufferUtilsTest {

    @Test
    public void isOneOf() {
        CharBuffer buf = CharBuffer.wrap("hello world");

        assertFalse(CharBufferUtils.isOneOf(0, buf, new char[]{'a', 'b', 'c'}));
        assertTrue(CharBufferUtils.isOneOf(0, buf, new char[]{'a', 'b', 'h'}));
        assertTrue(CharBufferUtils.isOneOf(0, buf, new char[]{'a', 'h', 'c'}));
        assertTrue(CharBufferUtils.isOneOf(0, buf, new char[]{'h', 'b', 'c'}));

        assertFalse(CharBufferUtils.isOneOf(1, buf, new char[]{'h', 'b', 'c'}));
        assertTrue(CharBufferUtils.isOneOf(1, buf, new char[]{'h', 'b', 'e'}));
    }

    @Test
    public void startsWith() {
        CharBuffer buf = CharBuffer.wrap("abcdef");

        assertTrue(CharBufferUtils.startsWith(0, buf, "ab".toCharArray()));
        assertFalse(CharBufferUtils.startsWith(1, buf, "ab".toCharArray()));
        assertFalse(CharBufferUtils.startsWith(5, buf, "ab".toCharArray()));
        assertTrue(CharBufferUtils.startsWith(5, buf, "f".toCharArray()));
        assertTrue(CharBufferUtils.startsWith(4, buf, "ef".toCharArray()));
        assertFalse(CharBufferUtils.startsWith(4, buf, "efg".toCharArray()));
        assertFalse(CharBufferUtils.startsWith(6, buf, "g".toCharArray()));
    }

}
