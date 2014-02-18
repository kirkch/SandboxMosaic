package com.mosaic.io.bytes;

import com.mosaic.lang.NotThreadSafe;


/**
 *
 *
 */
@NotThreadSafe
public interface OutputBytes {

    public void release();

    public boolean readBoolean( long index );
    public byte readByte( long index );
    public short readShort( long index );
    public char readCharacter( long index );
    public int readInteger( long index );
    public long readLong( long index );
    public float readFloat( long index );
    public double readDouble( long index );

    public short readUnsignedByte( long index );
    public int readUnsignedShort( long index );
    public long readUnsignedInteger( long index );


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
    public long endIndexExc();

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

    public long size();


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

}
