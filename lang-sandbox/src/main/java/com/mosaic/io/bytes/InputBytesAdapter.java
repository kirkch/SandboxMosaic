package com.mosaic.io.bytes;

import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8;


/**
 *
 */
public class InputBytesAdapter extends Bytes {

    private InputBytes bytes;

    public InputBytesAdapter( InputBytes bytes ) {
        this.bytes = bytes;
    }


    public Bytes narrow( long fromInc, long toExc ) {
        throw new UnsupportedOperationException(  );
    }

    public boolean readBoolean( long index ) {
        return bytes.readBoolean(index);
    }

    public byte readByte( long index ) {
        return bytes.readByte(index);
    }

    public short readShort( long index ) {
        return bytes.readShort(index);
    }

    public char readCharacter( long index ) {
        return bytes.readCharacter(index);
    }

    public int readInt( long index ) {
        return bytes.readInt(index);
    }

    public long readLong( long index ) {
        return bytes.readLong(index);
    }

    public float readFloat( long index ) {
        return bytes.readFloat(index);
    }

    public double readDouble( long index ) {
        return bytes.readDouble(index);
    }

    public short readUnsignedByte( long index ) {
        return bytes.readUnsignedByte(index);
    }

    public int readUnsignedShort( long index ) {
        return bytes.readUnsignedShort(index);
    }

    public long readUnsignedInt( long index ) {
        return bytes.readUnsignedInt(index);
    }

    public long remaining() {
        return bytes.remaining();
    }

    public boolean readBoolean() {
        return bytes.readBoolean();
    }

    public byte readByte() {
        return bytes.readByte();
    }

    public short readShort() {
        return bytes.readShort();
    }

    public char readCharacter() {
        return bytes.readCharacter();
    }

    public int readInteger() {
        return bytes.readInteger();
    }

    public long readLong() {
        return bytes.readLong();
    }

    public float readFloat() {
        return bytes.readFloat();
    }

    public double readDouble() {
        return bytes.readDouble();
    }

    public short readUnsignedByte() {
        return bytes.readUnsignedByte();
    }

    public int readUnsignedShort() {
        return bytes.readUnsignedShort();
    }

    public long readUnsignedInteger() {
        return bytes.readUnsignedInteger();
    }

    public void fill( long from, long toExc, byte v ) {
        throw new UnsupportedOperationException();
    }

    public char readSingleUTF8Character() {
        return bytes.readSingleUTF8Character();
    }

    public void readUTF8String( Appendable buf ) {
        bytes.readUTF8String( buf );
    }

    public long readBytes( byte[] destinationArray ) {
        return bytes.readBytes( destinationArray );
    }

    public void readBytes( byte[] destinationArray, int fromInc, int toExc ) {
        bytes.readBytes( destinationArray, fromInc, toExc );
    }

    public void readBytes( long destinationAddress, int numBytes ) {
        bytes.readBytes( destinationAddress, numBytes );
    }

    public void readUTF8Character( long index, DecodedCharacter output ) {
        bytes.readUTF8Character( index, output );
    }

    public int readUTF8String( long index, Appendable buf ) {
        return bytes.readUTF8String( index, buf );
    }

    public int readBytes( long index, byte[] array ) {
        return bytes.readBytes( index, array );
    }

    public void readBytes( long index, byte[] array, int fromInc, int toExc ) {
        bytes.readBytes( index, array, fromInc, toExc );
    }

    public void readBytes( long index, long toAddress, int numBytes ) {
        bytes.readBytes( index, toAddress, numBytes );
    }

    public String getName() {
        return bytes.getName();
    }

    public void setName( String name ) {
        bytes.setName( name );
    }

    public void release() {
        bytes.release();
    }

    public void writeBoolean( long index, boolean v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeByte( long index, byte v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeShort( long index, short v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeCharacter( long index, char v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeInt( long index, int v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeLong( long index, long v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeFloat( long index, float v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeDouble( long index, double v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeUnsignedByte( long index, short v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeUnsignedShort( long index, int v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeUnsignedInt( long index, long v ) {
        throw new UnsupportedOperationException(  );
    }

    public int writeUTF8Character( long destinationIndex, char v ) {
        throw new UnsupportedOperationException(  );
    }

    public int writeUTF8String( long destinationIndex, CharSequence sourceCharacters ) {
        throw new UnsupportedOperationException(  );
    }

    public int writeUTF8String( long destinationIndex, UTF8 sourceCharacters ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeBytes( long destinationIndex, byte[] sourceArray ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeBytes( long destinationIndex, byte[] sourceArray, int sourceFromInc, int sourceToExc ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeBytes( long destinationIndex, long sourceFromAddress, int numBytes ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeBytes( long destinationIndex, Bytes source, long sourceFromInc, long sourceToExc ) {
        throw new UnsupportedOperationException(  );
    }

    public long startIndex() {
        return bytes.startIndex();
    }

    public long getEndIndexExc() {
        return bytes.getEndIndexExc();
    }

    public long positionIndex() {
        return bytes.positionIndex();
    }

    public void positionIndex( long newIndex ) {
        bytes.positionIndex( newIndex );
    }

    public void rewindPositionIndex() {
        bytes.rewindPositionIndex();
    }

    public long bufferLength() {
        return bytes.bufferLength();
    }

    public void resize( long newLength ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeBoolean( boolean v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeByte( byte v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeShort( short v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeCharacter( char v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeInteger( int v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeLong( long v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeFloat( float v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeDouble( double v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeUnsignedByte( short v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeUnsignedShort( int v ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeUnsignedInteger( long v ) {
        throw new UnsupportedOperationException(  );
    }

    public int writeUTF8( char v ) {
        throw new UnsupportedOperationException(  );
    }

    public int writeUTF8String( CharSequence characters ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeBytes( byte[] array ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeBytes( byte[] array, int fromInc, int toExc ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeBytes( Bytes source ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeBytes( Bytes source, long sourceFromInc, long sourceToExc ) {
        throw new UnsupportedOperationException(  );
    }

    public void writeBytes( long fromAddress, int numBytes ) {
        throw new UnsupportedOperationException(  );
    }
}
