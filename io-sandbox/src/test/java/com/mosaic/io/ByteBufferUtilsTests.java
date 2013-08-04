package com.mosaic.io;

import org.junit.Test;

import java.nio.ByteBuffer;

import static com.mosaic.io.ByteBufferUtils.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ByteBufferUtilsTests {

    @Test
    public void startsWith() {
        ByteBuffer buf = encode("abcdef", UTF8);

        assertTrue( ByteBufferUtils.startsWith(0, buf, "ab".getBytes(UTF8)) );
        assertFalse(ByteBufferUtils.startsWith(1, buf, "ab".getBytes(UTF8)));
        assertFalse(ByteBufferUtils.startsWith(5, buf, "ab".getBytes(UTF8)));
        assertTrue(ByteBufferUtils.startsWith(5, buf, "f".getBytes(UTF8)));
        assertTrue(ByteBufferUtils.startsWith(4, buf, "ef".getBytes(UTF8)));
        assertFalse(ByteBufferUtils.startsWith(4, buf, "efg".getBytes(UTF8)));
        assertFalse(ByteBufferUtils.startsWith(6, buf, "g".getBytes(UTF8)));
    }

}
