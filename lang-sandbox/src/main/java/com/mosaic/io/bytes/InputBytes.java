package com.mosaic.io.bytes;

import com.mosaic.lang.NotThreadSafe;


/**
 *
 *
 */
@NotThreadSafe
public interface InputBytes {

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

}
