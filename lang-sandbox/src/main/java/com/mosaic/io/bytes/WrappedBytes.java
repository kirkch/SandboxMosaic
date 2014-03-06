package com.mosaic.io.bytes;

import com.mosaic.lang.Validate;
import com.mosaic.lang.text.DecodedCharacter;


/**
 *
 */
public class WrappedBytes extends BaseBytes {

    private Bytes delegate;

    public WrappedBytes( Bytes delegate ) {
        Validate.argNotNull( delegate, "delegate" );

        this.delegate = delegate;
    }


    public boolean readBoolean( long index ) {
        return delegate.readBoolean(index);
    }

    public byte readByte( long index ) {
        return delegate.readByte(index);
    }

    public short readShort( long index ) {
        return delegate.readShort(index);
    }

    public char readCharacter( long index ) {
        return delegate.readCharacter(index);
    }

    public int readInteger( long index ) {
        return delegate.readInteger(index);
    }

    public long readLong( long index ) {
        return delegate.readLong(index);
    }

    public float readFloat( long index ) {
        return delegate.readFloat(index);
    }

    public double readDouble( long index ) {
        return delegate.readDouble(index);
    }

    public short readUnsignedByte( long index ) {
        return delegate.readUnsignedByte(index);
    }

    public int readUnsignedShort( long index ) {
        return delegate.readUnsignedShort(index);
    }

    public long readUnsignedInteger( long index ) {
        return delegate.readUnsignedInteger(index);
    }

    public boolean readBoolean() {
        return delegate.readBoolean();
    }

    public byte readByte() {
        return delegate.readByte();
    }

    public short readShort() {
        return delegate.readShort();
    }

    public char readCharacter() {
        return delegate.readCharacter();
    }

    public int readInteger() {
        return delegate.readInteger();
    }

    public long readLong() {
        return delegate.readLong();
    }

    public float readFloat() {
        return delegate.readFloat();
    }

    public double readDouble() {
        return delegate.readDouble();
    }

    public short readUnsignedByte() {
        return delegate.readUnsignedByte();
    }

    public int readUnsignedShort() {
        return delegate.readUnsignedShort();
    }

    public long readUnsignedInteger() {
        return delegate.readUnsignedInteger();
    }

    public void fill( long from, long toExc, byte v ) {
        delegate.fill( from, toExc, v );
    }

    public void readSingleUTF8Character( long index, DecodedCharacter output ) {
        delegate.readSingleUTF8Character( index, output );
    }

    public int readUTF8String( long index, Appendable buf ) {
        return delegate.readUTF8String( index, buf );
    }

    public int readBytes( long index, byte[] array ) {
        return delegate.readBytes( index, array );
    }

    public void readBytes( long index, byte[] array, int fromInc, int toExc ) {
        delegate.readBytes( index, array, fromInc, toExc );
    }

    public void readBytes( long index, long toAddress, int numBytes ) {
        delegate.readBytes( index, toAddress, numBytes );
    }

    public String name() {
        return delegate.name();
    }

    public void setName( String name ) {
        delegate.setName( name );
    }

    public void writeBoolean( long index, boolean v ) {
        delegate.writeBoolean( index, v );
    }

    public void writeByte( long index, byte v ) {
        delegate.writeByte( index, v );
    }

    public void writeShort( long index, short v ) {
        delegate.writeShort( index, v );
    }

    public void writeCharacter( long index, char v ) {
        delegate.writeCharacter( index, v );
    }

    public void writeInteger( long index, int v ) {
        delegate.writeInteger( index, v );
    }

    public void writeLong( long index, long v ) {
        delegate.writeLong( index, v );
    }

    public void writeFloat( long index, float v ) {
        delegate.writeFloat( index, v );
    }

    public void writeDouble( long index, double v ) {
        delegate.writeDouble( index, v );
    }

    public void writeUnsignedByte( long index, short v ) {
        delegate.writeUnsignedByte( index, v );
    }

    public void writeUnsignedShort( long index, int v ) {
        delegate.writeUnsignedShort( index, v );
    }

    public void writeUnsignedInt( long index, long v ) {
        delegate.writeUnsignedInt( index, v );
    }

    public int writeUTF8( long index, char v ) {
        return delegate.writeUTF8( index, v );
    }

    public void writeBytes( long index, byte[] array, int fromInc, int toExc ) {
        delegate.writeBytes( index, array, fromInc, toExc );
    }

    public void writeBytes( long index, long fromAddress, int numBytes ) {
        delegate.writeBytes( index, fromAddress, numBytes );
    }

    public long startIndex() {
        return delegate.startIndex();
    }

    public long getEndIndexExc() {
        return delegate.getEndIndexExc();
    }

    public void writeBoolean( boolean v ) {
        delegate.writeBoolean( v );
    }

    public void writeByte( byte v ) {
        delegate.writeByte( v );
    }

    public void writeShort( short v ) {
        delegate.writeShort( v );
    }

    public void writeCharacter( char v ) {
        delegate.writeCharacter( v );
    }

    public void writeInteger( int v ) {
        delegate.writeInteger( v );
    }

    public void writeLong( long v ) {
        delegate.writeLong( v );
    }

    public void writeFloat( float v ) {
        delegate.writeFloat( v );
    }

    public void writeDouble( double v ) {
        delegate.writeDouble( v );
    }

    public void writeUnsignedByte( short v ) {
        delegate.writeUnsignedByte( v );
    }

    public void writeUnsignedShort( int v ) {
        delegate.writeUnsignedShort( v );
    }

    public void writeUnsignedInteger( long v ) {
        delegate.writeUnsignedInteger( v );
    }

    public void writeBytes( long fromAddress, int numBytes ) {
        delegate.writeBytes( fromAddress, numBytes );
    }

}
