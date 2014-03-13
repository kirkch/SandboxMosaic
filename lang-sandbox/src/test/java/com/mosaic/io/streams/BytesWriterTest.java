package com.mosaic.io.streams;

import com.mosaic.io.bytes.Bytes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class BytesWriterTest {

    private Bytes       bytes = Bytes.allocOnHeap( 1024 );
    private BytesWriter out   = new BytesWriter( bytes );


    @Test
    public void writeBoolean() {
        out.writeBoolean(true);
        out.writeBoolean(false);

        assertBytes( "truefalse" );
    }

    @Test
    public void writeByte() {
        out.writeByte( (byte) 127 );
        out.writeByte( (byte) 0 );
        out.writeByte( Byte.MAX_VALUE );
        out.writeByte( Byte.MIN_VALUE );

        assertBytes( "1270127-128" );
    }

    @Test
    public void writeBytes() {
        out.writeBytes( new byte[] { 0, 1, 2, -1, Byte.MIN_VALUE, Byte.MAX_VALUE} );
        out.writeBytes( new byte[] { -3,-2,-100,100} );

        assertBytes( "012-1-128127-3-2-100100" );
    }

    @Test
    public void writeIndexedBytes() {
        out.writeBytes( new byte[] { 0, 1, 2, -1, Byte.MIN_VALUE, Byte.MAX_VALUE}, 1,4 );
        out.writeBytes( new byte[] { -3,-2,-100,100}, 0, 1 );

        assertBytes( "12-1-3" );
    }

    @Test
    public void writeCharacter() {
        out.writeCharacter( 'a' );
        out.writeCharacter( 'b' );
        out.writeCharacter( 'c' );
        out.writeCharacter( '0' );
        out.writeCharacter( '9' );
        out.writeCharacter( '-' );
        out.writeCharacter( '£' );
        out.writeCharacter( 'グ' );
        out.writeCharacter( 'd' );

        assertBytes( "abc09-£グd" );
    }

    @Test
    public void writeCharacters() {
        out.writeCharacters( new char[] {'a','b','c'} );
        out.writeCharacters( new char[] {'グ','£','グ'} );

        assertBytes( "abcグ£グ" );
    }

    @Test
    public void writeIndexedCharacters() {
        out.writeCharacters( new char[] {'a','b','c'}, 0,1 );
        out.writeCharacters( new char[] {'a','b','c'}, 1,1 );
        out.writeCharacters( new char[] {'グ','£','グ'},1,3 );

        assertBytes( "a£グ" );
    }


    private void assertBytes( String expected ) {
        StringBuilder buf = new StringBuilder();

        long max = bytes.positionIndex();
        bytes.positionIndex(0);
        while ( bytes.positionIndex() < max ) {
            char c = bytes.readSingleUTF8Character();

            buf.append( c );
        }

        assertEquals( expected, buf.toString() );
    }

}
