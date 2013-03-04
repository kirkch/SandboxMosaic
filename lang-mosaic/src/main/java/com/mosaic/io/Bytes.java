package com.mosaic.io;

import com.mosaic.lang.Validate;

import java.io.CharConversionException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.List;

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
    public DecodedBytesResult toCharacters( String characterSet ) throws CharConversionException {
        Charset charset = Charset.forName(characterSet);

        return toCharacters( charset );
    }

    /**
     * Converts this instance of bytes into an immutable instance of Characters. If the bytes end part way through a
     * multi-byte character then conversion will stop at the end of the last complete character.
     */
    public DecodedBytesResult toCharacters( Charset charset ) throws CharConversionException {
        if ( this.length() == 0 ) {
            return new DecodedBytesResult(this,Characters.EMPTY);
        }

        CharsetDecoder decoder        = charset.newDecoder();
        CharBuffer     destBuffer     = CharBuffer.allocate( (int) (this.length()*decoder.averageCharsPerByte()+1) );
        Bytes          bytesRemaining = decodeBytesToBufferAndReturningUndecodedBytes( decoder, destBuffer );

        destBuffer.flip();

        Characters decodedCharacters = Characters.wrapCharBuffer( destBuffer );
        return new DecodedBytesResult( bytesRemaining, decodedCharacters );
    }

    /**
     * @return bytes remaining
     */
    protected abstract Bytes decodeBytesToBufferAndReturningUndecodedBytes( CharsetDecoder decoder, CharBuffer destBuffer ) throws CharConversionException;


    /**
     * Join two instances of Bytes together. Does so by creating a wrapper around both instances of Bytes, making them
     * appear as one.
     */
    public abstract Bytes appendBytes( Bytes bytes );

    /**
     * Jump over the specified number of bytes. If this instance of Bytes is a wrapper around multiple instances
     * of Bytes and all of the bytes of the first instance have now been 'consumed' then the reference to that
     * instance will be dropped. Making it possible to GC it.
     */
    public abstract Bytes skipBytes( int numBytes );

    public abstract Bytes subset( int fromInc, int toExc );

    /**
     * Write the contents of this instance of Bytes into the target ByteBuffer. If the ByteBuffer is too small to take
     * all of the bytes then the write will stop at the buffers limit.
     */
    public void writeTo( ByteBuffer targetBuffer ) {
        writeTo( targetBuffer, this.length() );
    }

    /**
     * Write the contents of this instance of Bytes into the target ByteBuffer. If the ByteBuffer is too small to take
     * all of the bytes then the write will stop at the buffers limit.
     */
    public abstract void writeTo( ByteBuffer targetBuffer, int numBytes );


    /**
     * An internal setter for ensuring that the stream offset is accurate, even when multiple instances of Bytes are
     * being appended together.
     */
    abstract Bytes setStreamOffset( long newStreamOffset );
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

    protected Bytes decodeBytesToBufferAndReturningUndecodedBytes( CharsetDecoder decoder, CharBuffer destBuffer ) throws CharConversionException {
        ByteBuffer shallowClonedBytes = this.buf.duplicate();
        shallowClonedBytes.position( this.positionOffset );

        CoderResult v = decoder.decode( shallowClonedBytes, destBuffer, false );
        if ( v.isMalformed() ) {
            throw new CharConversionException( "Malformed bytes after offset " + streamOffset() );
        }
        int numBytesConsumed = shallowClonedBytes.position() - this.positionOffset;

        return new BytesNIOWrapper( this.buf, shallowClonedBytes.position(), streamOffset+numBytesConsumed );
    }

    public Bytes appendBytes( Bytes other ) {
        Validate.notNull( other, "other" );

        if ( this.length() == 0 ) {
            return other;
        } else if ( other.length() == 0 ) {
            return this;
        }

        List<Bytes> buckets = new ArrayList(2);
        buckets.add( this );
        buckets.add( other.setStreamOffset(this.streamOffset+this.length()) );

        return new BytesMultiBucketWrapper( buckets, this.streamOffset );
    }

    public Bytes skipBytes( int numBytes ) {
        Validate.isLTE( numBytes, this.length(), "numBytes" );

        if ( numBytes == 0 ) {
            return this;
        }

        return new BytesNIOWrapper( this.buf, this.positionOffset+numBytes, this.streamOffset+numBytes );
    }

    public Bytes subset( int fromInc, int toExc ) {
        int length = this.length();

        if ( fromInc == 0 && toExc == 0 && length == 0 ) {
            return this;
        }

        Validate.indexBounds( 0, fromInc, length, "fromInc" );
        Validate.indexBounds( 0, toExc, length+1, "toExc" );


        ByteBuffer clonedBuffer = this.buf.duplicate();
        clonedBuffer.limit( toExc );

        return new BytesNIOWrapper( clonedBuffer, this.positionOffset+fromInc, this.streamOffset+fromInc );
    }

    public void writeTo( ByteBuffer targetBuffer, int numBytes ) {
        Validate.isLTE( numBytes, this.length(), "numBytes" );

        ByteBuffer buf   = this.buf;
        int        limit = numBytes+this.positionOffset;

        for ( int i=buf.position()+this.positionOffset; i<limit; i++ ) {
            targetBuffer.put( buf.get(i) );
        }
    }

    Bytes setStreamOffset( long newStreamOffset ) {
        return new BytesNIOWrapper( this.buf, this.positionOffset, newStreamOffset );
    }
}


/**
 * Wraps other instances of Bytes. Allows appending instances together without copying all of the bytes around. Also
 * allows for the deallocation of consumed bytes on the fly, again without copying bytes around.
 */
class BytesMultiBucketWrapper extends Bytes {
    private final List<Bytes> buckets;
    private final long        streamOffset;

    BytesMultiBucketWrapper( List<Bytes> buckets, long streamOffset ) {
        this.buckets        = buckets;
        this.streamOffset   = streamOffset;
    }

    public long streamOffset() {
        return streamOffset;
    }

    public int length() {
        int remaining = 0;

        for ( Bytes b : buckets ) {
            remaining += b.length();
        }

        return remaining;
    }

    public byte getByte( final int index ) {
        int i = index;
        for ( Bytes b : buckets ) {
            int length = b.length();

            if ( i < length ) {
                return b.getByte( i );
            } else {
                i -= length;
            }
        }

        throw new IndexOutOfBoundsException( );
    }

    protected Bytes decodeBytesToBufferAndReturningUndecodedBytes( CharsetDecoder decoder, CharBuffer destBuffer ) throws CharConversionException {

//        int numBytesConsumed = 0;
//        for ( Bytes bucket : this.buckets ) {
//            Bytes remainingBytes = bucket.decodeBytesToBufferAndReturningUndecodedBytes( decoder, destBuffer );
//
//            numBytesConsumed += remainingBytes.length() - bucket.length();
//        }
//
//        return subset( 0, numBytesConsumed );

        ByteBuffer allBytes = ByteBuffer.allocate( this.length() );
        this.writeTo( allBytes );
        allBytes.flip();

        Bytes b = Bytes.wrapBytesBufferNoCopy( allBytes );
        return b.decodeBytesToBufferAndReturningUndecodedBytes( decoder, destBuffer );
    }

    public Bytes appendBytes( Bytes other ) {
        Validate.notNull( other, "other" );

        if ( this.length() == 0 ) {
            return other;
        } else if ( other.length() == 0 ) {
            return this;
        }

        List<Bytes> newBytes = new ArrayList(this.buckets.size()+1);  // todo replace with an immutable variant
        newBytes.addAll(this.buckets);
        newBytes.add(other.setStreamOffset(this.streamOffset+this.length()));

        return new BytesMultiBucketWrapper( newBytes, streamOffset );
    }

    public Bytes skipBytes( int numBytes ) {
        Validate.isLTE( numBytes, this.length(), "numBytes" );

        if ( numBytes == 0 ) {
            return this;
        }

        int         bytesLeftToConsume = numBytes;
        List<Bytes> newBuckets         = new ArrayList(this.buckets.size());

        for ( Bytes bucket : buckets ) {
            if ( bytesLeftToConsume == 0 ) {
                newBuckets.add( bucket );
            } else {
                int numBytesInBucket = bucket.length();
                if ( bytesLeftToConsume < numBytesInBucket ) {
                    newBuckets.add( bucket.skipBytes(bytesLeftToConsume) );

                    bytesLeftToConsume = 0;
                } else {
                    bytesLeftToConsume -= numBytesInBucket;
                }
            }
        }

        return new BytesMultiBucketWrapper( newBuckets, this.streamOffset+numBytes );
    }

    public Bytes subset( int fromInc, int toExc ) {

throw new UnsupportedOperationException( "todo" );
//        return this; // todo
    }

    public void writeTo( ByteBuffer targetBuffer, final int numBytes ) {
        Validate.isLTE( numBytes, this.length(), "numBytes" );

        int numBytesLeftToWrite = numBytes;

        for ( Bytes bucket : buckets ) {
            int numBytesInBucket = bucket.length();

            if ( numBytesInBucket < numBytesLeftToWrite ) {
                bucket.writeTo( targetBuffer );

                numBytesLeftToWrite -= numBytesInBucket;
            } else {
                bucket.writeTo( targetBuffer, numBytesInBucket );

                break;
            }
        }
    }

    Bytes setStreamOffset( long newStreamOffset ) {
        return new BytesMultiBucketWrapper( this.buckets, newStreamOffset );
    }
}