package com.mosaic.io.bytes;

import com.mosaic.lang.QA;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8;


/**
 *
 */
public abstract class WrappedBytes extends Bytes implements Cloneable {

    protected Bytes delegate;

    public WrappedBytes( Bytes delegate ) {
        QA.argNotNull( delegate, "delegate" );

        this.delegate = delegate;
    }

    public Bytes narrow( long fromInc, long toExc ) {
        WrappedBytes clone = ReflectionUtils.clone( this );

        clone.delegate = this.delegate.narrow( fromInc, toExc );

        return clone;
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

    public int readInt( long index ) {
        return delegate.readInt( index );
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

    public long readUnsignedInt( long index ) {
        return delegate.readUnsignedInt( index );
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

    public char readSingleUTF8Character() {
        return delegate.readSingleUTF8Character();
    }

    public void readUTF8String( Appendable buf ) {
        delegate.readUTF8String( buf );
    }

    public long readBytes( byte[] destinationArray ) {
        return delegate.readBytes( destinationArray );
    }

    public void readBytes( byte[] destinationArray, int fromInc, int toExc ) {
        delegate.readBytes( destinationArray, fromInc, toExc );
    }

    public void readBytes( long destinationAddress, int numBytes ) {
        delegate.readBytes( destinationAddress, numBytes );
    }

    public void readUTF8Character( long index, DecodedCharacter output ) {
        delegate.readUTF8Character( index, output );
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

    public String getName() {
        return delegate.getName();
    }

    public void setName( String name ) {
        delegate.setName( name );
    }

    public void release() {
        delegate.release();
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

    public void writeInt( long index, int v ) {
        delegate.writeInt( index, v );
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

    public int writeUTF8Character( long destinationIndex, char v ) {
        return delegate.writeUTF8Character( destinationIndex, v );
    }

    public int writeUTF8String( long destinationIndex, CharSequence sourceCharacters ) {
        return delegate.writeUTF8String( destinationIndex, sourceCharacters );
    }

    public int writeUTF8String( long destinationIndex, UTF8 sourceCharacters ) {
        return delegate.writeUTF8String( destinationIndex, sourceCharacters );
    }

    public void writeBytes( long destinationIndex, byte[] sourceArray ) {
        delegate.writeBytes( destinationIndex, sourceArray );
    }

    public void writeBytes( long destinationIndex, byte[] sourceArray, int sourceFromInc, int sourceToExc ) {
        delegate.writeBytes( destinationIndex, sourceArray, sourceFromInc, sourceToExc );
    }

    public void writeBytes( long destinationIndex, long sourceFromAddress, int numBytes ) {
        delegate.writeBytes( destinationIndex, sourceFromAddress, numBytes );
    }

    public void writeBytes( long destinationIndex, Bytes source, long sourceFromInc, long sourceToExc ) {
        delegate.writeBytes( destinationIndex, source, sourceFromInc, sourceToExc );
    }

    public void writeBytes( Bytes source ) {
        delegate.writeBytes( source );
    }

    public void writeBytes( Bytes source, long sourceFromInc, long sourceToExc ) {
        delegate.writeBytes( source, sourceFromInc, sourceToExc );
    }

    public long startIndex() {
        return delegate.startIndex();
    }

    public long getEndIndexExc() {
        return delegate.getEndIndexExc();
    }

    public long positionIndex() {
        return delegate.positionIndex();
    }

    public void positionIndex( long newIndex ) {
        delegate.positionIndex( newIndex );
    }

    public void rewindPositionIndex() {
        delegate.rewindPositionIndex();
    }

    public long bufferLength() {
        return delegate.bufferLength();
    }

    public long remaining() {
        return delegate.remaining();
    }

    public void resize( long newLength ) {
        delegate.resize( newLength );
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

    public int writeUTF8( char v ) {
        return delegate.writeUTF8( v );
    }

    public int writeUTF8StringWithLengthPrefix( CharSequence characters ) {
        return delegate.writeUTF8StringWithLengthPrefix( characters );
    }

    public void writeBytes( byte[] array ) {
        delegate.writeBytes( array );
    }

    public void writeBytes( byte[] array, int fromInc, int toExc ) {
        delegate.writeBytes( array, fromInc, toExc );
    }

    public void writeBytes( long fromAddress, int numBytes ) {
        delegate.writeBytes( fromAddress, numBytes );
    }

    public String toString() {
        return delegate.toString();
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public boolean equals( Object o ) {
        return delegate.equals( o );
    }
}
