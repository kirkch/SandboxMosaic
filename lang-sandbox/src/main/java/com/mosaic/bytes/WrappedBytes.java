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
    private final long base;
    private       long maxExc;


    public WrappedBytes( Bytes delegate ) {
        this( delegate, 0, delegate.sizeBytes() );
    }

    public WrappedBytes( Bytes delegate, long offset, long maxExc ) {
        this.base = offset;
        this.maxExc = maxExc;
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
        return maxExc - base;
    }

    public void resize( long newLength ) {
        if ( base != 0 || maxExc != delegate.sizeBytes() ) {
            throw new UnsupportedOperationException( "These bytes have been narrowed, and cannot be resized" );
        }

        delegate.resize(newLength);

        this.maxExc = newLength;
    }

    public void sync() {
        delegate.sync();
    }

    public void fill( long from, long toExc, byte v ) {
        touchRW( from, toExc, toExc-from );

        long f = from + base;
        long t = Math.min( this.maxExc, toExc );


        delegate.fill( f, t, v );
    }

    public boolean readBoolean( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_BOOLEAN );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );

        return delegate.readBoolean( f, t );
    }

    public void writeBoolean( long offset, long maxExc, boolean v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_BOOLEAN );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );

        delegate.writeBoolean( f, t, v );
    }

    public byte readByte( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_BYTE );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );

        return delegate.readByte( f, t );
    }

    public void writeByte( long offset, long maxExc, byte v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_BYTE );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );

        delegate.writeByte( f, t, v );
    }

    public short readShort( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_SHORT );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );

        return delegate.readShort( f, t );
    }

    public void writeShort( long offset, long maxExc, short v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_SHORT );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );

        delegate.writeShort( f, t, v );
    }

    public char readCharacter( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_CHAR );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );

        return delegate.readCharacter( f, t );
    }

    public void writeCharacter( long offset, long maxExc, char v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_CHAR );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );

        delegate.writeCharacter( f, t, v );
    }

    public int readInt( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_INT );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );

        return delegate.readInt( f, t );
    }

    public void writeInt( long offset, long maxExc, int v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_INT );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        delegate.writeInt( f, t, v );
    }

    public long readLong( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_LONG );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.readLong( f, t );
    }

    public void writeLong( long offset, long maxExc, long v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_LONG );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        delegate.writeLong( f, t, v );
    }

    public float readFloat( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_FLOAT );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.readFloat( f, t );
    }

    public void writeFloat( long offset, long maxExc, float v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_FLOAT );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        delegate.writeFloat( f, t, v );
    }

    public double readDouble( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_DOUBLE );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.readDouble( f, t );
    }

    public void writeDouble( long offset, long maxExc, double v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_DOUBLE );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        delegate.writeDouble( f, t, v );
    }

    public short readUnsignedByte( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_UNSIGNED_BYTE );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.readUnsignedByte( f, t );
    }

    public void writeUnsignedByte( long offset, long maxExc, short v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_UNSIGNED_BYTE );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        delegate.writeUnsignedByte( f, t, v );
    }

    public int readUnsignedShort( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_UNSIGNED_SHORT );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.readUnsignedShort( f, t );
    }

    public void writeUnsignedShort( long offset, long maxExc, int v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_UNSIGNED_SHORT );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        delegate.writeUnsignedShort( f, t, v );
    }

    public long readUnsignedInt( long offset, long maxExc ) {
        touchRO( offset, maxExc, SystemX.SIZEOF_UNSIGNED_INT );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.readUnsignedInt( f, t );
    }

    public void writeUnsignedInt( long offset, long maxExc, long v ) {
        touchRW( offset, maxExc, SystemX.SIZEOF_UNSIGNED_INT );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        delegate.writeUnsignedInt( f, t, v );
    }

    public void readUTF8Character( long offset, long maxExc, DecodedCharacter output ) {
        touchRO( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        delegate.readUTF8Character( f, t, output );
    }

    public int writeUTF8Character( long offset, long maxExc, char c ) {
        touchRW( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.writeUTF8Character( f, t, c );
    }

    public int readUTF8String( long offset, long maxExc, Appendable output ) {
        touchRO( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.readUTF8String( f, t, output );
    }

    public int writeUTF8String( long offset, long maxExc, CharSequence txt ) {
        touchRW( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.writeUTF8String( f, t, txt );
    }

    public int writeUTF8String( long offset, long maxExc, UTF8 txt ) {
        touchRW( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.writeUTF8String( f, t, txt );
    }

    public int writeNullTerminatedUTF8String( long offset, long maxExc, CharSequence txt ) {
        touchRW( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.writeNullTerminatedUTF8String( f, t, txt );
    }

    public int writeUTF8StringUndemarcated( long offset, long maxExc, CharSequence txt ) {
        touchRW( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.writeUTF8StringUndemarcated( f, t, txt );
    }

    public int readBytes( long offset, long maxExc, byte[] destinationArray ) {
        touchRO( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.readBytes( f, t, destinationArray );
    }

    public int writeBytes( long offset, long maxExc, byte[] sourceBytes ) {
        touchRW( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.writeBytes( f, t, sourceBytes );
    }

    public int readBytes( long offset, long maxExc, Bytes destination ) {
        touchRO( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.readBytes( f, t, destination );
    }

    public int writeBytes( long offset, long maxExc, Bytes sourceBytes ) {
        touchRW( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.writeBytes( f, t, sourceBytes );
    }

    public int readBytes( long offset, long maxExc, Bytes destination, long destinationInc, long destinationExc ) {
        touchRO( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.readBytes( f, t, destination, destinationInc, destinationExc );
    }

    public int writeBytes( long offset, long maxExc, Bytes sourceBytes, long sourceInc, long sourceExc ) {
        touchRW( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.writeBytes( f, t, sourceBytes, sourceInc, sourceExc );
    }

    public int readBytes( long offset, long maxExc, byte[] destinationArray, long destinationArrayInc, long destinationArrayExc ) {
        touchRO( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.readBytes( f, t, destinationArray, destinationArrayInc, destinationArrayExc );
    }

    public int writeBytes( long offset, long maxExc, byte[] sourceArray, long sourceArrayInc, long sourceArrayExc ) {
        touchRW( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.writeBytes( f, t, sourceArray, sourceArrayInc, sourceArrayExc );
    }

    public int readBytes( long offset, long maxExc, long toAddressBase, long toAddressInc, long toAddressExc ) {
        touchRO( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.readBytes( f, t, toAddressBase, toAddressInc, toAddressExc );
    }

    public int writeBytes( long offset, long maxExc, long fromAddressBase, long fromAddressInc, long fromAddressExc ) {
        touchRW( offset, maxExc, maxExc-offset );

        long f = base+offset;
        long t = Math.min( this.maxExc, maxExc );


        return delegate.writeBytes( f, t, fromAddressBase, fromAddressInc, fromAddressExc );
    }

    public byte[] toArray() {
        touchRO( base, sizeBytes(), sizeBytes() );

        return delegate.toArray();
    }

    public InputStream toInputStream() {
        return delegate.toInputStream();
    }

    public String toString() {
        return delegate.toString();
    }
}
