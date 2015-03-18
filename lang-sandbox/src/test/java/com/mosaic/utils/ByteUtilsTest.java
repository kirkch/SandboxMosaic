package com.mosaic.utils;

import org.junit.Test;

import static org.junit.Assert.*;


public class ByteUtilsTest {

    @Test
    public void testCopy() {
        byte[] source = new byte[] {1,2,3,4,5};

        assertArrayEquals( new byte[] {1,2,3,4,5}, ByteUtils.copy(source, 0, 5) );
        assertArrayEquals( new byte[] {1,2,3}, ByteUtils.copy(source, 0, 3) );
        assertArrayEquals( new byte[] {2,3}, ByteUtils.copy(source, 1, 3) );

        try {
            ByteUtils.copy(source, 1, 6);
            fail( "expected ArrayIndexOutOfBoundsException" );
        } catch ( ArrayIndexOutOfBoundsException ex ) {

        }
    }

}