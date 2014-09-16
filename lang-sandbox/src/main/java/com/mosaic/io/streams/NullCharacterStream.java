package com.mosaic.io.streams;

import com.mosaic.bytes.Bytes2;
import com.mosaic.lang.text.UTF8;


/**
 *
 */
public class NullCharacterStream implements CharacterStream {

    public static CharacterStream NULL_CHARACTER_STREAM = new NullCharacterStream();

    public boolean isEnabled() {
        return false;
    }
    public void setEnabled( boolean flag ) {}

    public void writeBoolean( boolean v ) {
    }

    public void writeByteAsNumber( byte v ) {
    }

    public void writeUTF8Bytes( Bytes2 bytes ) {
    }

    public void writeUTF8Bytes( Bytes2 bytes, int fromIndexInc, int toExc ) {
    }

    public void writeUTF8Bytes( byte[] bytes ) {
    }

    public void writeUTF8Bytes( byte[] bytes, int fromIndexInc, int toExc ) {
    }

    public void writeCharacter( char v ) {
    }

    public void writeCharacters( char[] chars ) {
    }

    public void writeCharacters( char[] chars, int fromIndexInc, int toExc ) {
    }

    public void writeShort( short v ) {
    }

    public void writeUnsignedShort( int v ) {
    }

    public void writeInt( int v ) {
    }

    public void writeFixedWidthInt( int v, int fixedWidth, byte paddingByte ) {
    }

    public void writeSmallCashMajorUnit( int v ) {
    }

    public void writeSmallCashMinorUnit( int v ) {
    }

    public void writeBigCashMajorUnit( long v ) {
    }

    public void writeBigCashMinorUnit( long v ) {
    }

    public void writeUnsignedInt( long v ) {
    }

    public void writeLong( long v ) {
    }

    public void writeFloat( float v, int numDecimalPlaces ) {
    }

    public void writeDouble( double v, int numDecimalPlaces ) {
    }

    public void writeString( String v ) {
    }

    public void writeLine( String v ) {
    }

    public void writeUTF8( UTF8 v ) {
    }

    public void writeLine( UTF8 v ) {
    }

    public void writeException( Throwable ex ) {
    }

    public void writeException( String msg, Throwable ex ) {
    }

    public void newLine() {
    }

    public void flush() {
    }
}
