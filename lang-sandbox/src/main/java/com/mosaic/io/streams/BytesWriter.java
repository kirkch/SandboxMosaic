package com.mosaic.io.streams;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.UTF8;
import com.mosaic.lang.system.SystemX;


/**
 * Writes UTF8 characters to an instance of Bytes.
 */
public class BytesWriter implements WriterX {

    private static final byte[] FALSE = "false".getBytes(SystemX.UTF8);
    private static final byte[] TRUE  = "true".getBytes(SystemX.UTF8);




    private final byte[] buf = new byte[20];

    private Bytes bytes;

    public BytesWriter( Bytes bytes ) {
        this.bytes = bytes;
    }


    public void writeBoolean( boolean v ) {
        bytes.writeBytes( v ? TRUE : FALSE );
    }

    public void writeByte( byte v ) {
//        byte[] bytes1 = Byte.toString(v).getBytes( SystemX.UTF8 );
//
//        bytes.writeBytes( bytes1 );

//        buf[0] = v;
//        buf[1] = v;
//        buf[2] = v;
//
//        bytes.writeBytes(buf);
    }

    @Override
    public void writeBytes( byte[] bytes ) {
    }

    @Override
    public void writeBytes( byte[] bytes, int fromIndexInc, int numBytes ) {
    }

    @Override
    public void writeCharacter( char v ) {
    }

    @Override
    public void writeCharacters( char[] chars ) {
    }

    @Override
    public void writeCharacters( char[] chars, int fromIndexInc, int numChars ) {
    }

    @Override
    public void writeShort( short v ) {
    }

    @Override
    public void writeUnsignedShort( int v ) {
    }

    @Override
    public void writeInt( int v ) {
        byte[] bytes1 = Integer.toString(v).getBytes( SystemX.UTF8 );

        bytes.writeBytes( bytes1 );

//        int i = 3;
//        buf[0] = (byte) v;
//        buf[1] = (byte) v;
//        buf[2] = (byte) v;
//
//        bytes.writeBytes( buf, 0, i );
    }

    @Override
    public void writeUnsignedInt( long v ) {
    }

    @Override
    public void writeLong( long v ) {
    }

    @Override
    public void writeFloat( float v ) {
    }

    public void writeFloat( float v, int numDecimalPlaces ) {
        int intPart = (int) v;
        int decPart = (int) ((v - intPart) * Math.pow(10,numDecimalPlaces) + 0.5f);

        this.writeInt( intPart );
        this.writeCharacter( '.' );
        this.writeInt( decPart );
    }

    @Override
    public void writeDouble( double v ) {
    }

    @Override
    public void writeDouble( double v, int numDecimalPlaces ) {
    }

    @Override
    public void writeString( String v ) {
    }

    @Override
    public void writeLine( String v ) {
    }

    @Override
    public void writeUTF8( UTF8 v ) {
    }

    @Override
    public void writeLine( UTF8 v ) {
    }

    @Override
    public void writeException( Throwable ex ) {
    }

    @Override
    public void writeException( String msg, Throwable ex ) {
    }

    @Override
    public void newLine() {
    }
}
