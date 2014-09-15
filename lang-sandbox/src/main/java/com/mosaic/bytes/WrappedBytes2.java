package com.mosaic.bytes;

import com.mosaic.bytes.Bytes2;
import com.mosaic.lang.QA;
import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8;

import java.io.InputStream;


/**
 *
 */
public class WrappedBytes2 implements Bytes2 {
    private final Bytes2 delegate;

    public WrappedBytes2( Bytes2 delegate ) {
        QA.notNull( delegate, "delegate" );

        this.delegate = delegate;
    }

    public void release() {
        delegate.release();
    }

    public long sizeBytes() {
        return delegate.sizeBytes();
    }

    public void resize( long newLength ) {
        delegate.resize( newLength );
    }

    public void fill( long from, long toExc, byte v ) {
        delegate.fill( from, toExc, v );
    }

    public boolean readBoolean( long offset, long maxExc ) {
        return delegate.readBoolean( offset, maxExc );
    }

    public void writeBoolean( long offset, long maxExc, boolean v ) {
        delegate.writeBoolean( offset, maxExc, v );
    }

    public byte readByte( long offset, long maxExc ) {
        return delegate.readByte( offset, maxExc );
    }

    public void writeByte( long offset, long maxExc, byte v ) {
        delegate.writeByte( offset, maxExc, v );
    }

    public short readShort( long offset, long maxExc ) {
        return delegate.readShort( offset, maxExc );
    }

    public void writeShort( long offset, long maxExc, short v ) {
        delegate.writeShort( offset, maxExc, v );
    }

    public char readCharacter( long offset, long maxExc ) {
        return delegate.readCharacter( offset, maxExc );
    }

    public void writeCharacter( long offset, long maxExc, char v ) {
        delegate.writeCharacter( offset, maxExc, v );
    }

    public int readInt( long offset, long maxExc ) {
        return delegate.readInt( offset, maxExc );
    }

    public void writeInt( long offset, long maxExc, int v ) {
        delegate.writeInt( offset, maxExc, v );
    }

    public long readLong( long offset, long maxExc ) {
        return delegate.readLong( offset, maxExc );
    }

    public void writeLong( long offset, long maxExc, long v ) {
        delegate.writeLong( offset, maxExc, v );
    }

    public float readFloat( long offset, long maxExc ) {
        return delegate.readFloat( offset, maxExc );
    }

    public void writeFloat( long offset, long maxExc, float v ) {
        delegate.writeFloat( offset, maxExc, v );
    }

    public double readDouble( long offset, long maxExc ) {
        return delegate.readDouble( offset, maxExc );
    }

    public void writeDouble( long offset, long maxExc, double v ) {
        delegate.writeDouble( offset, maxExc, v );
    }

    public short readUnsignedByte( long offset, long maxExc ) {
        return delegate.readUnsignedByte( offset, maxExc );
    }

    public void writeUnsignedByte( long offset, long maxExc, short v ) {
        delegate.writeUnsignedByte( offset, maxExc, v );
    }

    public int readUnsignedShort( long offset, long maxExc ) {
        return delegate.readUnsignedShort( offset, maxExc );
    }

    public void writeUnsignedShort( long offset, long maxExc, int v ) {
        delegate.writeUnsignedShort( offset, maxExc, v );
    }

    public long readUnsignedInt( long offset, long maxExc ) {
        return delegate.readUnsignedInt( offset, maxExc );
    }

    public void writeUnsignedInt( long offset, long maxExc, long v ) {
        delegate.writeUnsignedInt( offset, maxExc, v );
    }

    public void readUTF8Character( long offset, long maxExc, DecodedCharacter output ) {
        delegate.readUTF8Character( offset, maxExc, output );
    }

    public int writeUTF8Character( long offset, long maxExc, char c ) {
        return delegate.writeUTF8Character( offset, maxExc, c );
    }

    public int readUTF8String( long offset, long maxExc, Appendable output ) {
        return delegate.readUTF8String( offset, maxExc, output );
    }

    public int writeUTF8String( long offset, long maxExc, CharSequence txt ) {
        return delegate.writeUTF8String( offset, maxExc, txt );
    }

    public int writeUTF8String( long offset, long maxExc, UTF8 txt ) {
        return delegate.writeUTF8String( offset, maxExc, txt );
    }

    public int writeNullTerminatedUTF8String( long offset, long maxExc, CharSequence txt ) {
        return delegate.writeNullTerminatedUTF8String( offset, maxExc, txt );
    }

    public int writeUTF8StringUndemarcated( long offset, long maxExc, CharSequence txt ) {
        return delegate.writeUTF8StringUndemarcated( offset, maxExc, txt );
    }

    public int readBytes( long offset, long maxExc, byte[] destinationArray ) {
        return delegate.readBytes( offset, maxExc, destinationArray );
    }

    public int writeBytes( long offset, long maxExc, byte[] sourceBytes ) {
        return delegate.writeBytes( offset, maxExc, sourceBytes );
    }

    public int readBytes( long offset, long maxExc, Bytes2 destination ) {
        return delegate.readBytes( offset, maxExc, destination );
    }

    public int writeBytes( long offset, long maxExc, Bytes2 sourceBytes ) {
        return delegate.writeBytes( offset, maxExc, sourceBytes );
    }

    public int readBytes( long offset, long maxExc, Bytes2 destination, long destinationInc, long destinationExc ) {
        return delegate.readBytes( offset, maxExc, destination, destinationInc, destinationExc );
    }

    public int writeBytes( long offset, long maxExc, Bytes2 sourceBytes, long sourceInc, long sourceExc ) {
        return delegate.writeBytes( offset, maxExc, sourceBytes, sourceInc, sourceExc );
    }

    public int readBytes( long offset, long maxExc, byte[] destinationArray, long destinationArrayInc, long destinationArrayExc ) {
        return delegate.readBytes( offset, maxExc, destinationArray, destinationArrayInc, destinationArrayExc );
    }

    public int writeBytes( long offset, long maxExc, byte[] sourceArray, long sourceArrayInc, long sourceArrayExc ) {
        return delegate.writeBytes( offset, maxExc, sourceArray, sourceArrayInc, sourceArrayExc );
    }

    public int readBytes( long offset, long maxExc, long toAddressBase, long toAddressInc, long toAddressExc ) {
        return delegate.readBytes( offset, maxExc, toAddressBase, toAddressInc, toAddressExc );
    }

    public int writeBytes( long offset, long maxExc, long fromAddressBase, long fromAddressInc, long fromAddressExc ) {
        return delegate.writeBytes( offset, maxExc, fromAddressBase, fromAddressInc, fromAddressExc );
    }


    public InputStream toInputStream() {
        return delegate.toInputStream();
    }

    public String toString() {
        return delegate.toString();
    }
}
