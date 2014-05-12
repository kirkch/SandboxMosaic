package com.mosaic.io.streams;

import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.QA;
import com.mosaic.lang.text.UTF8;


/**
 *
 */
public class WrapperCharacterStream implements CharacterStream {

    private CharacterStream delegate;

    public WrapperCharacterStream( CharacterStream delegate ) {
        QA.argNotNull( delegate, "delegate" );

        this.delegate = delegate;
    }

    public void writeBoolean( boolean v ) {
        delegate.writeBoolean(v);
    }

    public void writeByteAsNumber( byte v ) {
        delegate.writeByteAsNumber( v );
    }

    public void writeUTF8Bytes( Bytes bytes ) {
        delegate.writeUTF8Bytes( bytes );
    }

    public void writeUTF8Bytes( Bytes bytes, int fromIndexInc, int toExc ) {
        delegate.writeUTF8Bytes( bytes, fromIndexInc, toExc );
    }

    public void writeUTF8Bytes( byte[] bytes ) {
        delegate.writeUTF8Bytes( bytes );
    }

    public void writeUTF8Bytes( byte[] bytes, int fromIndexInc, int toExc ) {
        delegate.writeUTF8Bytes( bytes, fromIndexInc, toExc );
    }

    public void writeCharacter( char v ) {
        delegate.writeCharacter(v);
    }

    public void writeCharacters( char[] chars ) {
        delegate.writeCharacters(chars);
    }

    public void writeCharacters( char[] chars, int fromIndexInc, int toExc ) {
        delegate.writeCharacters(chars,fromIndexInc,toExc);
    }

    public void writeShort( short v ) {
        delegate.writeShort(v);
    }

    public void writeUnsignedShort( int v ) {
        delegate.writeUnsignedShort(v);
    }

    public void writeInt( int v ) {
        delegate.writeInt(v);
    }

    public void writeFixedWidthInt( int v, int fixedWidth, byte paddingByte ) {
        delegate.writeFixedWidthInt(v, fixedWidth, paddingByte);
    }

    public void writeSmallCashMajorUnit( int v ) {
        delegate.writeSmallCashMajorUnit(v);
    }

    public void writeSmallCashMinorUnit( int v ) {
        delegate.writeSmallCashMinorUnit(v);
    }

    public void writeBigCashMajorUnit( long v ) {
        delegate.writeBigCashMajorUnit(v);
    }

    public void writeBigCashMinorUnit( long v ) {
        delegate.writeBigCashMinorUnit(v);
    }

    public void writeUnsignedInt( long v ) {
        delegate.writeUnsignedInt(v);
    }

    public void writeLong( long v ) {
        delegate.writeLong(v);
    }

    public void writeFloat( float v, int numDecimalPlaces ) {
        delegate.writeFloat(v, numDecimalPlaces);
    }

    public void writeDouble( double v, int numDecimalPlaces ) {
        delegate.writeDouble(v, numDecimalPlaces);
    }

    public void writeString( String v ) {
        delegate.writeString(v);
    }

    public void writeLine( String v ) {
        delegate.writeLine(v);
    }

    public void writeUTF8( UTF8 v ) {
        delegate.writeUTF8(v);
    }

    public void writeLine( UTF8 v ) {
        delegate.writeLine(v);
    }

    public void writeException( Throwable ex ) {
        delegate.writeException(ex);
    }

    public void writeException( String msg, Throwable ex ) {
        delegate.writeException(msg,ex);
    }

    public void newLine() {
        delegate.newLine();
    }

    public void flush() {
        delegate.flush();
    }
}
