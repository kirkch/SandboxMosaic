package com.mosaic.io.bytes;

import com.mosaic.lang.NotThreadSafe;
import com.mosaic.lang.text.DecodedCharacter;


/**
 *
 *
 */
@NotThreadSafe
public interface InputBytes {

    /**
     * Helps to identify where the bytes came from/what they are used for.
     */
    public String getName();
    public void setName( String name );


    public void release();

    public boolean readBoolean( long index );
    public byte readByte( long index );
    public short readShort( long index );
    public char readCharacter( long index );
    public int readInt( long index );
    public long readLong( long index );
    public float readFloat( long index );
    public double readDouble( long index );

    public short readUnsignedByte( long index );
    public int readUnsignedShort( long index );
    public long readUnsignedInt( long index );


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
     * The index that will be used by the next relative read method.
     */
    public long positionIndex();

    /**
     * Moves the position of the next relative read to the specified index.
     */
    public void positionIndex( long newIndex );

    /**
     * Moves the position of the next relative read to the first valid index.
     */
    public void rewindPositionIndex();

    /**
     * Marks the index of where the valid data that has been previously written
     * comes to an end.  By convention this index is returned as being
     * exclusive.  This means that the last index is actually watermarkIndexExc()-1.
     */
//    public long watermarkIndexExc();

    /**
     * Marks a new position within the index as to where valid bytes have been
     * written up to.
     */
//    public void watermarkIndexExc( long newIndex );

    /**
     * The number of bytes between position and the end of the buffer.
     */
    public long remaining();

    public long bufferLength();


    public boolean readBoolean();
    public byte readByte();
    public short readShort();
    public char readCharacter();
    public int readInteger();
    public long readLong();
    public float readFloat();
    public double readDouble();

    public short readUnsignedByte();
    public int readUnsignedShort();
    public long readUnsignedInteger();



    public void fill( long from, long toExc, byte v );


    /**
     * Reads a single UTF-8 character. Will advance the position by 1-3 bytes.
     */
    public char readSingleUTF8Character();

    /**
     * Reads n characters in to the supplied StringBuffer.  Will advance the
     * position on by the number of bytes read.  The bytes must start an unsigned
     * short that supplies how many bytes make up the string.  The following
     * bytes are then UTF-8 encoded.
     *
     * @param buf the buffer to write the newly read characters into
     */
    public void readUTF8String( Appendable buf );

    /**
     * Copies as many bytes as are available, or will fit into the supplied
     * array.   Starts reading from the current position, and will increment
     * the current position by the number of bytes read.
     *
     * @return the number of bytes read
     */
    public long readBytes( byte[] destinationArray );

    /**
     * Copies as many bytes specified into the supplied array.  Starts reading
     * from the current position, and will increment
     * the current position by the number of bytes read.
     *
     * It is an error to request for more bytes than are available.
     *
     * @param fromInc the index to start writing to (inclusive)
     * @param toExc   the index to stop writing to (exclusive)
     */
    public void readBytes( byte[] destinationArray, int fromInc, int toExc );

    /**
     * Copies as many bytes as are available, or will fit into the supplied
     * memory location.  Starts reading from the current position, and will increment
     * the current position by the number of bytes read.
     *
     * It is an error to request more bytes than are available.
     *
     * @param numBytes the number of bytes to read
     */
    public void readBytes( long destinationAddress, int numBytes );


    /**
     * Reads a single UTF-8 encoded character from the specified index.  Uses
     * DecodedCharacter as an OUT param so that the character can be returned
     * with a count of how many bytes were used to hold the character without
     * having to allocateNewRecord any extra objects.
     */
    public void readUTF8Character( long index, DecodedCharacter output );

    /**
     * Reads the specified string encoded in UTF-8 into the supplied StringBuilder.
     * UTF-8 is a variable width encoding, for most latin characters it uses
     * only one byte.  For the rest it will use two or three.
     *
     * @return the number of bytes read
     */
    public int readUTF8String( long index, Appendable buf );

    /**
     * Copies as many bytes as are available, or will fit into the supplied
     * array.   Starts reading from the specified position, and will not modify
     * the current position.
     *
     * @return the number of bytes read
     */
    public int readBytes( long index, byte[] array );

    /**
     * Copies as many bytes that are specified into the supplied
     * array.  Starts reading from the current position, and will not
     * modify the current position.
     *
     * It is an error to request more bytes than are available.
     *
     * @param fromInc the index to start writing to (inclusive)
     * @param toExc   the index to stop writing to (exclusive)
     */
    public void readBytes( long index, byte[] array, int fromInc, int toExc );

    /**
     * Copies numBytes from this buffer[index] to the specified memory address.
     */
    public void readBytes( long index, long toAddress, int numBytes );

}
