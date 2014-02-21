package com.mosaic.lang.text;

import com.mosaic.lang.Backdoor;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class UTF8ToolsTest {
    private byte[]           array        = new byte[5];
    private DecodedCharacter decodeOutput = new DecodedCharacter();


    @Test
    public void utf8EncodeThenDecodeByteArray_1ByteCharacter() throws UnsupportedEncodingException {
        UTF8Tools.write( array, 1, 'a' );
        assertByteArrayEquals( array, 0,'a',0,0,0 );


        UTF8Tools.decode( array, 1, decodeOutput );

        assertEquals( 'a', decodeOutput.c );
        assertEquals( 1, decodeOutput.numBytesConsumed );
    }

    @Test
    public void utf8EncodeThenDecodeByteArray_2ByteCharacter() throws UnsupportedEncodingException {
            UTF8Tools.write( array, 2, '£' );

            byte[] poundSign = "£".getBytes( "UTF8" );
            assertByteArrayEquals( array, 0,0,poundSign[0],poundSign[1], 0 );

            UTF8Tools.decode( array, 2, decodeOutput );
            assertEquals( '£', decodeOutput.c );
            assertEquals( 2, decodeOutput.numBytesConsumed );
    }

    @Test
    public void utf8EncodeThenDecodeByteArray_3ByteCharacter() throws UnsupportedEncodingException {
        UTF8Tools.write( array, 2, 'グ' );

        byte[] kanjiSymbol = "グ".getBytes( "UTF8" );
        assertByteArrayEquals( array, 0,0,kanjiSymbol[0],kanjiSymbol[1],kanjiSymbol[2] );

        UTF8Tools.decode( array, 2, decodeOutput );
        assertEquals( 'グ', decodeOutput.c );
        assertEquals( 3, decodeOutput.numBytesConsumed );
    }

    @Test
    public void utf8EncodeThenDecodeNative_1ByteCharacter() throws UnsupportedEncodingException {
        long ptr = Backdoor.alloc( 5 );

        try {
            UTF8Tools.write( ptr+1, ptr+5, 'a' );
            assertBytesViaPointer( ptr, 0, 'a', 0, 0, 0 );


            UTF8Tools.decode( ptr+1, ptr+5, decodeOutput );

            assertEquals( 'a', decodeOutput.c );
            assertEquals( 1, decodeOutput.numBytesConsumed );
        } finally {
            Backdoor.free( ptr );
        }
    }

    @Test
    public void utf8EncodeThenDecodeNative_2ByteCharacter() throws UnsupportedEncodingException {
        long ptr           = Backdoor.alloc( 5 );
        long maxAddressExc = ptr + 5;

        Backdoor.fill( ptr, 5, (byte) 0 );

        try {
            UTF8Tools.write( ptr+2, maxAddressExc, '£' );

            byte[] poundSign = "£".getBytes( "UTF8" );
            assertBytesViaPointer( ptr, 0,0,poundSign[0],poundSign[1], 0 );

            UTF8Tools.decode( ptr+2, maxAddressExc, decodeOutput );
            assertEquals( '£', decodeOutput.c );
            assertEquals( 2, decodeOutput.numBytesConsumed );
        } finally {
            Backdoor.free( ptr );
        }
    }

    @Test
    public void utf8EncodeThenDecodeNative_3ByteCharacter() throws UnsupportedEncodingException {
        long ptr           = Backdoor.alloc( 5 );
        long maxAddressExc = ptr + 5;

        Backdoor.fill( ptr, 5, (byte) 0 );

        try {
            UTF8Tools.write( ptr+2, maxAddressExc, 'グ' );

            byte[] kanjiSymbol = "グ".getBytes( "UTF8" );
            assertBytesViaPointer( ptr, 0,0,kanjiSymbol[0],kanjiSymbol[1],kanjiSymbol[2] );

            UTF8Tools.decode( ptr+2, maxAddressExc, decodeOutput );
            assertEquals( 'グ', decodeOutput.c );
            assertEquals( 3, decodeOutput.numBytesConsumed );
        } finally {
            Backdoor.free( ptr );
        }
    }


    private void assertByteArrayEquals( byte[] array, int c0, int c1, int c2, int c3, int c4 ) {
        assertEquals( (byte) c0, array[0] );
        assertEquals( (byte) c1, array[1] );
        assertEquals( (byte) c2, array[2] );
        assertEquals( (byte) c3, array[3] );
        assertEquals( (byte) c4, array[4] );
    }

    private void assertBytesViaPointer( long ptr, int c0, int c1, int c2, int c3, int c4 ) {
        assertEquals( (byte) c0, Backdoor.getByte(ptr) );
        assertEquals( (byte) c1, Backdoor.getByte(ptr+1) );
        assertEquals( (byte) c2, Backdoor.getByte(ptr+2) );
        assertEquals( (byte) c3, Backdoor.getByte(ptr+3) );
        assertEquals( (byte) c4, Backdoor.getByte(ptr+4) );
    }

}
