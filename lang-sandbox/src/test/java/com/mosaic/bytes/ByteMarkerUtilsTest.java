package com.mosaic.bytes;

import org.junit.Test;

import static org.junit.Assert.*;


public class ByteMarkerUtilsTest {

    @Test
    public void testEncodeChar() {
        assertEquals( 0x00, ByteMarkerUtils.encode('0') );
        assertEquals( 0x01, ByteMarkerUtils.encode('1') );
        assertEquals( 0x02, ByteMarkerUtils.encode('2') );
        assertEquals( 0x03, ByteMarkerUtils.encode('3') );
        assertEquals( 0x04, ByteMarkerUtils.encode('4') );
        assertEquals( 0x05, ByteMarkerUtils.encode('5') );
        assertEquals( 0x06, ByteMarkerUtils.encode('6') );
        assertEquals( 0x07, ByteMarkerUtils.encode('7') );
        assertEquals( 0x08, ByteMarkerUtils.encode('8') );
        assertEquals( 0x09, ByteMarkerUtils.encode('9') );
        assertEquals( 0x0A, ByteMarkerUtils.encode('a') );
        assertEquals( 0x0B, ByteMarkerUtils.encode('b') );
        assertEquals( 0x0C, ByteMarkerUtils.encode('c') );
        assertEquals( 0x0D, ByteMarkerUtils.encode('d') );
        assertEquals( 0x0E, ByteMarkerUtils.encode('e') );
        assertEquals( 0x0F, ByteMarkerUtils.encode('f') );

        assertEquals( 0x0A, ByteMarkerUtils.encode('A') );
        assertEquals( 0x0B, ByteMarkerUtils.encode('B') );
        assertEquals( 0x0C, ByteMarkerUtils.encode('C') );
        assertEquals( 0x0D, ByteMarkerUtils.encode('D') );
        assertEquals( 0x0E, ByteMarkerUtils.encode('E') );
        assertEquals( 0x0F, ByteMarkerUtils.encode('F') );
    }

    @Test
    public void testEncodeString() {
        assertArrayEquals( new byte[]{(byte) 0xCA, (byte) 0xFE, (byte)0xBA, (byte)0xBE}, ByteMarkerUtils.encode( "cafebabe") );
        assertArrayEquals( new byte[]{(byte) 0xCA, (byte) 0xFE, (byte)0xBA, (byte)0xBE}, ByteMarkerUtils.encode( "CAFEBABE") );

        assertArrayEquals( new byte[]{(byte) 0xCA, (byte) 0xFE, (byte)0xBA, (byte)0xB0}, ByteMarkerUtils.encode( "CAFEBAB") );
        assertArrayEquals( new byte[]{(byte) 0xBA, (byte) 0xDB, (byte)0x07, (byte)0x00,(byte)0x10}, ByteMarkerUtils.encode( "BADBOY-01") );
    }

}