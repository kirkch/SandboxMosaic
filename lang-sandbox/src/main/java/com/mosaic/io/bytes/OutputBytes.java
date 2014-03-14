package com.mosaic.io.bytes;

import com.mosaic.lang.NotThreadSafe;


/**
 *
 *
 */
@NotThreadSafe
public interface OutputBytes {

    /**
     * Helps to identify where the bytes came from/what they are used for.
     */
    public String name();
    public void setName( String name );


    public void release();

    public void writeBoolean( long index, boolean v );
    public void writeByte( long index, byte v );
    public void writeShort( long index, short v );
    public void writeCharacter( long index, char v );
    public void writeInteger( long index, int v );
    public void writeLong( long index, long v );
    public void writeFloat( long index, float v );
    public void writeDouble( long index, double v );

    public void writeUnsignedByte( long index, short v );
    public void writeUnsignedShort( long index, int v );
    public void writeUnsignedInt( long index, long v );

    /**
     * Writes the specified character encoded in UTF-8.  UTF-8 is a variable
     * width encoding, for most latin characters it uses only one byte.  For the
     * rest it will use two or three.
     *
     * @return the number of bytes used
     */
    public int writeUTF8( long index, char v );

    /**
     * Writes the specified string encoded in UTF-8.  UTF-8 is a variable
     * width encoding, for most latin characters it uses only one byte.  For the
     * rest it will use two or three.
     *
     * @return the number of bytes used
     */
    public int writeUTF8String( long index, CharSequence characters );

    /**
     * Writes all of the bytes within the array.
     */
    public void writeBytes( long index, byte[] array );

    public void writeBytes( long index, byte[] array, int fromInc, int toExc );

    public void writeBytes( long index, long fromAddress, int numBytes );


    /**
     * The starting point for these bytes.  The minimum index that may be used.
     * Usually zero, but when slicing and dicing different buffers then it is best
     * to not assume.
     */
    public long startIndex();

    /**
     * Marks the end of the bytes.  By convention this index is returned as being
     * exclusive.  This means that the max valid index is actually endIndexExc()-1.
     */
    public long getEndIndexExc();

    /**
     * The index that will be used by the next relative write method.
     */
    public long positionIndex();

    /**
     * Moves the position of the next relative write to the specified index.
     */
    public void positionIndex( long newIndex );

    /**
     * Moves the position of the next relative write to the first valid index.
     */
    public void rewindPositionIndex();

    // NB hummed and ahhed over the notion of adding a watermark.  As I do
    // not want to add the overhead of maintaining the watermark for every
    // write call, I have decided not to add it.  The position can be used
    // for the same effect.

    public long bufferLength();

    public void resize( long newLength );


    public void writeBoolean( boolean v );
    public void writeByte( byte v );
    public void writeShort( short v );
    public void writeCharacter( char v );
    public void writeInteger( int v );
    public void writeLong( long v );
    public void writeFloat( float v );
    public void writeDouble( double v );

    public void writeUnsignedByte( short v );
    public void writeUnsignedShort( int v );
    public void writeUnsignedInteger( long v );


    /**
     * Writes the specified character encoded in UTF-8.  UTF-8 is a variable
     * width encoding, for most latin characters it uses only one byte.  For the
     * rest it will use two or three.
     */
    public int writeUTF8( char v );

    /**
     * Returns the number of bytes written.
     */
    public int writeUTF8String( CharSequence characters );

    /**
     * Write all of the bytes within the array.
     */
    public void writeBytes( byte[] array );
    public void writeBytes( byte[] array, int fromInc, int toExc );

    public void writeBytes( long fromAddress, int numBytes );


}
