package com.mosaic.bytes;

import com.mosaic.lang.QA;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8;

import java.io.InputStream;


/**
 *
 */
public class WrappedBytes implements Bytes {
    private final Bytes delegate;

    public WrappedBytes( Bytes delegate ) {
        QA.notNull( delegate, "delegate" );

        this.delegate = delegate;
    }

    /**
     * Extension point.  The public access method will be modifying the specified range.  Most of
     * the time this method will be blank, and it will get removed by Hotspot, thus no overhead.
     */
    protected void touchRW( long offset, long maxExc, long size ) {}

    /**
     * Extension point.  The public access method will be reading the specified range. Most of
     * the time this method will be blank, and it will get removed by Hotspot, thus no overhead.
     */
    protected void touchRO( long offset, long maxExc, long size ) {}


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
        touchRW( from, toExc, toExc - from );

        delegate.fill( from, toExc, v );
    }

    public boolean readBoolean( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_BOOLEAN );

        return delegate.readBoolean( offset, maxExc );
    }

    public void writeBoolean( long offset, long maxExc, boolean v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_BOOLEAN );

        delegate.writeBoolean( offset, maxExc, v );
    }

    public byte readByte( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_BYTE );

        return delegate.readByte( offset, maxExc );
    }

    public void writeByte( long offset, long maxExc, byte v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_BYTE );

        delegate.writeByte( offset, maxExc, v );
    }

    public short readShort( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_SHORT );

        return delegate.readShort( offset, maxExc );
    }

    public void writeShort( long offset, long maxExc, short v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_SHORT );

        delegate.writeShort( offset, maxExc, v );
    }

    public char readCharacter( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_CHAR );

        return delegate.readCharacter( offset, maxExc );
    }

    public void writeCharacter( long offset, long maxExc, char v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_CHAR );

        delegate.writeCharacter( offset, maxExc, v );
    }

    public int readInt( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_INT );

        return delegate.readInt( offset, maxExc );
    }

    public void writeInt( long offset, long maxExc, int v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_INT );

        delegate.writeInt( offset, maxExc, v );
    }

    public long readLong( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_LONG );

        return delegate.readLong( offset, maxExc );
    }

    public void writeLong( long offset, long maxExc, long v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_LONG );

        delegate.writeLong( offset, maxExc, v );
    }

    public float readFloat( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_FLOAT );

        return delegate.readFloat( offset, maxExc );
    }

    public void writeFloat( long offset, long maxExc, float v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_FLOAT );

        delegate.writeFloat( offset, maxExc, v );
    }

    public double readDouble( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_DOUBLE );

        return delegate.readDouble( offset, maxExc );
    }

    public void writeDouble( long offset, long maxExc, double v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_DOUBLE );

        delegate.writeDouble( offset, maxExc, v );
    }

    public short readUnsignedByte( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_UNSIGNED_BYTE );

        return delegate.readUnsignedByte( offset, maxExc );
    }

    public void writeUnsignedByte( long offset, long maxExc, short v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_UNSIGNED_BYTE );

        delegate.writeUnsignedByte( offset, maxExc, v );
    }

    public int readUnsignedShort( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_UNSIGNED_SHORT );

        return delegate.readUnsignedShort( offset, maxExc );
    }

    public void writeUnsignedShort( long offset, long maxExc, int v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_UNSIGNED_SHORT );

        delegate.writeUnsignedShort( offset, maxExc, v );
    }

    public long readUnsignedInt( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_UNSIGNED_INT );

        return delegate.readUnsignedInt( offset, maxExc );
    }

    public void writeUnsignedInt( long offset, long maxExc, long v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_UNSIGNED_INT );

        delegate.writeUnsignedInt( offset, maxExc, v );
    }

    public void readUTF8Character( long offset, long maxExc, DecodedCharacter output ) {
        touchRO( offset, maxExc, maxExc - offset );

        delegate.readUTF8Character( offset, maxExc, output );
    }

    public int writeUTF8Character( long offset, long maxExc, char c ) {
        touchRW( offset, maxExc, maxExc - offset );

        return delegate.writeUTF8Character( offset, maxExc, c );
    }

    public int readUTF8String( long offset, long maxExc, Appendable output ) {
        touchRO( offset, maxExc, maxExc-offset );

        return delegate.readUTF8String( offset, maxExc, output );
    }

    public int writeUTF8String( long offset, long maxExc, CharSequence txt ) {
        touchRW( offset, maxExc, maxExc - offset );

        return delegate.writeUTF8String( offset, maxExc, txt );
    }

    public int writeUTF8String( long offset, long maxExc, UTF8 txt ) {
        touchRW( offset, maxExc, maxExc-offset );

        return delegate.writeUTF8String( offset, maxExc, txt );
    }

    public int writeNullTerminatedUTF8String( long offset, long maxExc, CharSequence txt ) {
        touchRW( offset, maxExc, maxExc-offset );

        return delegate.writeNullTerminatedUTF8String( offset, maxExc, txt );
    }

    public int writeUTF8StringUndemarcated( long offset, long maxExc, CharSequence txt ) {
        touchRW( offset, maxExc, maxExc-offset );

        return delegate.writeUTF8StringUndemarcated( offset, maxExc, txt );
    }

    public int readBytes( long offset, long maxExc, byte[] destinationArray ) {
        touchRO( offset, maxExc, maxExc - offset );

        return delegate.readBytes( offset, maxExc, destinationArray );
    }

    public int writeBytes( long offset, long maxExc, byte[] sourceBytes ) {
        touchRW( offset, maxExc, maxExc-offset );

        return delegate.writeBytes( offset, maxExc, sourceBytes );
    }

    public int readBytes( long offset, long maxExc, Bytes destination ) {
        touchRO( offset, maxExc, maxExc - offset );

        return delegate.readBytes( offset, maxExc, destination );
    }

    public int writeBytes( long offset, long maxExc, Bytes sourceBytes ) {
        touchRW( offset, maxExc, maxExc-offset );

        return delegate.writeBytes( offset, maxExc, sourceBytes );
    }

    public int readBytes( long offset, long maxExc, Bytes destination, long destinationInc, long destinationExc ) {
        touchRO( offset, maxExc, maxExc - offset );

        return delegate.readBytes( offset, maxExc, destination, destinationInc, destinationExc );
    }

    public int writeBytes( long offset, long maxExc, Bytes sourceBytes, long sourceInc, long sourceExc ) {
        touchRW( offset, maxExc, maxExc - offset );

        return delegate.writeBytes( offset, maxExc, sourceBytes, sourceInc, sourceExc );
    }

    public int readBytes( long offset, long maxExc, byte[] destinationArray, long destinationArrayInc, long destinationArrayExc ) {
        touchRO( offset, maxExc, maxExc - offset );

        return delegate.readBytes( offset, maxExc, destinationArray, destinationArrayInc, destinationArrayExc );
    }

    public int writeBytes( long offset, long maxExc, byte[] sourceArray, long sourceArrayInc, long sourceArrayExc ) {
        touchRW( offset, maxExc, maxExc-offset );

        return delegate.writeBytes( offset, maxExc, sourceArray, sourceArrayInc, sourceArrayExc );
    }

    public int readBytes( long offset, long maxExc, long toAddressBase, long toAddressInc, long toAddressExc ) {
        touchRO( offset, maxExc, maxExc - offset );

        return delegate.readBytes( offset, maxExc, toAddressBase, toAddressInc, toAddressExc );
    }

    public int writeBytes( long offset, long maxExc, long fromAddressBase, long fromAddressInc, long fromAddressExc ) {
        touchRW( offset, maxExc, maxExc-offset );

        return delegate.writeBytes( offset, maxExc, fromAddressBase, fromAddressInc, fromAddressExc );
    }

    public byte[] toArray() {
        touchRO( 0, delegate.sizeBytes(), delegate.sizeBytes() );

        return delegate.toArray();
    }

    public InputStream toInputStream() {
        return delegate.toInputStream();
    }

    public String toString() {
        return delegate.toString();
    }
}
