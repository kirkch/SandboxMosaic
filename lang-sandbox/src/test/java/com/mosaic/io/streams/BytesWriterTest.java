package com.mosaic.io.streams;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.UTF8;
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
        out.writeBytes( new byte[]{0, 1, 2, -1, Byte.MIN_VALUE, Byte.MAX_VALUE} );
        out.writeBytes( new byte[]{-3, -2, -100, 100} );

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
        out.writeCharacters( new char[]{'a', 'b', 'c'} );
        out.writeCharacters( new char[]{'グ', '£', 'グ'} );

        assertBytes( "abcグ£グ" );
    }

    @Test
    public void writeIndexedCharacters() {
        out.writeCharacters( new char[] {'a','b','c'}, 0,1 );
        out.writeCharacters( new char[] {'a','b','c'}, 1,1 );
        out.writeCharacters( new char[] {'グ','£','グ'},1,3 );

        assertBytes( "a£グ" );
    }

    @Test
    public void writeShort() {
        out.writeShort( (short) 0 );
        out.writeShort( (short) 1 );
        out.writeShort( (short) 2 );
        out.writeShort( (short) -1 );
        out.writeShort( (short) -2 );
        out.writeShort( (short) -10 );
        out.writeShort( (short) -11 );
        out.writeShort( (short) 10 );
        out.writeShort( (short) 11 );
        out.writeShort( Short.MIN_VALUE );
        out.writeShort( Short.MAX_VALUE );

        assertBytes( "012-1-2-10-111011-3276832767" );
    }

    @Test
    public void writeUnsignedShort() {
        out.writeUnsignedShort( 0 );
        out.writeUnsignedShort( 1 );
        out.writeUnsignedShort( 2 );
        out.writeUnsignedShort( 10 );
        out.writeUnsignedShort( 11 );
        out.writeUnsignedShort( (Short.MAX_VALUE << 1) + 1 );

        assertBytes( "012101165535" );
    }

    @Test
    public void writeInt() {
        out.writeInt( 0 );
        out.writeInt( 1 );
        out.writeInt( 2 );
        out.writeInt( -1 );
        out.writeInt( -2 );
        out.writeInt( -10 );
        out.writeInt( -11 );
        out.writeInt( 10 );
        out.writeInt( 11 );
        out.writeInt( Integer.MIN_VALUE );
        out.writeInt( Integer.MAX_VALUE );

        assertBytes( "012-1-2-10-111011-21474836482147483647" );
    }

    @Test
    public void writeUnsignedInt() {
        out.writeUnsignedInt( 0 );
        out.writeUnsignedInt( 1 );
        out.writeUnsignedInt( 2 );
        out.writeUnsignedInt( 10 );
        out.writeUnsignedInt( 11 );
        out.writeUnsignedInt( ((((long) Integer.MAX_VALUE) << 1)) + 1 );

        assertBytes( "01210114294967295" );
    }

    @Test
    public void writeFloat() {
        out.writeFloat( 3.14f, 2 );
        out.writeCharacter( ' ' );
        out.writeFloat( 0f, 2 );
        out.writeCharacter( ' ' );
        out.writeFloat( 2.133f, 2 );
        out.writeCharacter( ' ' );
        out.writeFloat( 2.135f, 2 );
        out.writeCharacter( ' ' );
        out.writeFloat( 2.136f, 2 );
        out.writeCharacter( ' ' );
        out.writeFloat( -2.133f, 2 );
        out.writeCharacter( ' ' );
        out.writeFloat( -2.1373f, 2 );
        out.writeCharacter( ' ' );
        out.writeFloat( 10.01f, 2 );

        assertBytes( "3.14 0.00 2.13 2.14 2.14 -2.13 -2.14 10.01" );
    }

    @Test
    public void writeDouble() {
        out.writeDouble( 3.14, 2 );
        out.writeCharacter( ' ' );
        out.writeDouble( 0, 2 );
        out.writeCharacter( ' ' );
        out.writeDouble( 2.133, 2 );
        out.writeCharacter( ' ' );
        out.writeDouble( 2.135, 2 );
        out.writeCharacter( ' ' );
        out.writeDouble( 2.136, 2 );
        out.writeCharacter( ' ' );
        out.writeDouble( -2.133, 2 );
        out.writeCharacter( ' ' );
        out.writeDouble( -2.1373, 2 );
        out.writeCharacter( ' ' );
        out.writeDouble( 10.01, 2 );

        assertBytes( "3.14 0.00 2.13 2.14 2.14 -2.13 -2.14 10.01" );
    }

    @Test
    public void writeString() {
        out.writeString( "foo bar" );
        out.writeString( " tar" );
        out.writeString( "£グf" );
        out.writeString( "123" );

        assertBytes( "foo bar tar£グf123" );
    }

    @Test
    public void writeLine() {
        out.writeLine( "foo bar" );
        out.writeLine( " tar" );
        out.writeLine( "£グf" );
        out.writeLine( "123" );

        assertBytes( "foo bar\n tar\n£グf\n123\n" );
    }

    @Test
    public void writeUTF8() {
        out.writeUTF8( UTF8.wrap("foo bar") );
        out.writeUTF8( UTF8.wrap(" tar") );
        out.writeUTF8( UTF8.wrap("£グf") );
        out.writeUTF8( UTF8.wrap("123") );

        assertBytes( "foo bar tar£グf123" );
    }

    @Test
    public void writeUTF8Line() {
        out.writeLine( UTF8.wrap("foo bar") );
        out.writeLine( UTF8.wrap(" tar") );
        out.writeLine( UTF8.wrap("£グf") );
        out.writeLine( UTF8.wrap("123") );

        assertBytes( "foo bar\n tar\n£グf\n123\n" );
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
