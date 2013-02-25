package com.mosaic.io;

import com.mosaic.lang.Validate;

import java.nio.ByteBuffer;

/**
 * An immutable collection of bytes. Modification of these bytes returns a new immutable instance of Bytes.
 */
public abstract class Bytes {

    /**
     * Create an instance of Bytes containing the remaining contents of buffer, that is the bytes between position
     * and limit. Defensively makes a copy of the src buffer. The position of the buffer will be returned back to its
     * original position before completion of this method.
     */
    public static Bytes wrapBytesBuffer( ByteBuffer src ) {
        Validate.notNull( src, "src" );

        int originalPosition = src.position();
        ByteBuffer copy = ByteBuffer.allocate( src.remaining() );
        copy.put( src );
        copy.flip();

        src.position( originalPosition );


        return new BytesNIOWrapper(copy,0,0L);
    }

    /**
     * Create an instance of Bytes containing the remaining contents of buffer, that is the bytes between position
     * and limit. Ownership of the src buffer is transferred to the Bytes object, it is not defensively copied and
     * thus any background changes to it will damage the immutability of the Bytes instance.
     */
    public static Bytes wrapBytesBufferNoCopy( ByteBuffer src ) {
        Validate.notNull( src, "src" );

        return new BytesNIOWrapper(src,src.position(),0L);
    }

    /**
     * The index of where these bytes start from in relation to the start of the original stream of bytes. Appending
     * and consuming bytes will maintain this value.
     */
    public abstract long streamOffset();

    /**
     * The number of bytes represented by this collection.
     */
    public abstract int length();

    /**
     * Fetch the byte at the specified index. The index starts from zero.
     */
    public abstract byte getByte( int index );

    /**
     * Converts this instance of bytes into an immutable instance of Characters. If the bytes end part way through a
     * multi-byte character then conversion will stop at the end of the last complete character.
     */
    public abstract Characters toCharacters( String characterSet );


    public abstract Bytes appendBytes( Bytes bytes );


    public abstract Bytes consume( int numBytes );


    public void writeTo( ByteBuffer targetBuffer ) {
        writeTo( targetBuffer, this.length() );
    }

    public abstract void writeTo( ByteBuffer targetBuffer, int numBytes );
}



class BytesNIOWrapper extends Bytes {
    private final ByteBuffer buf;
    private final int        positionOffset;
    private final long       streamOffset;

    BytesNIOWrapper( ByteBuffer src, int positionOffset, long streamOffset ) {
        this.buf            = src;
        this.positionOffset = positionOffset;
        this.streamOffset   = streamOffset;
    }

    public long streamOffset() {
        return streamOffset;
    }

    public int length() {
        return buf.remaining() - positionOffset;
    }

    public byte getByte( int index ) {
        return buf.get( index + positionOffset );
    }

    public Characters toCharacters( String characterSet ) {
        return null;
    }

    public Bytes appendBytes( Bytes other ) {
        Validate.notNull( other, "other" );

        if ( this.length() == 0 ) {
            return other;
        } else if ( other.length() == 0 ) {
            return this;
        }

        ByteBuffer buf = ByteBuffer.allocate( this.buf.remaining() + other.length() );   // todo remove need to copy
        this.writeTo( buf );
        other.writeTo( buf );
        buf.flip();

        return new BytesNIOWrapper( buf, 0, this.streamOffset ); // todo 0L is wrong
    }

    public Bytes consume( int numBytes ) {
        Validate.isLTE( numBytes, this.length(), "numBytes" );

        if ( numBytes == 0 ) {               // todo drop unused ByteBuffers
            return this;
        }

        return new BytesNIOWrapper( this.buf, this.positionOffset+numBytes, this.streamOffset+numBytes );
    }

    public void writeTo( ByteBuffer targetBuffer, int numBytes ) {
        Validate.isLTE( numBytes, this.length(), "numBytes" );

        ByteBuffer buf   = this.buf;
        int        limit = numBytes+this.positionOffset;

        for ( int i=buf.position()+this.positionOffset; i<limit; i++ ) {
            targetBuffer.put( buf.get(i) );
        }
    }
}