package com.mosaic.io.memory;


import com.mosaic.io.bytes.Bytes;
import com.mosaic.lang.text.DecodedCharacter;


/**
 * Malloc/Free implementation for Bytes.  The reason for implementing this is to support
 * off-heap data structures, especially combined with Memory Mapped files for persistence.
 *
 * Use FlyWeightBytes where every record is of the same size, and MemoryRegion when they are
 * variable width.
 */
public interface MemoryRegion {

    /**
     * Allocates a new region of memory.  Sets the addresses ref count to one.
     *
     * @return a pointer that is only recognised by this class (it is not a main memory address)
     */
    public int malloc( int numBytes );

    /**
     * Increments the addresses ref count.
     */
    public void retain( int address );

    /**
     * Decrements the addresses ref count.  When the ref count reaches zero, the address will
     * become invalid and open to being reallocated.
     */
    public void release( int address );

    public short getCurrentRetainCountFor( int address );





    public void writeBoolean( int baseAddress, int offset, boolean newValue );
    public boolean readBoolean( int baseAddress, int offset );

    public void writeByte( int baseAddress, int offset, byte newValue );
    public byte readByte( int baseAddress, int offset );
    
    public void writeCharacter( int baseAddress, int offset, char newValue );
    public char readCharacter( int baseAddress, int offset );

    public void writeUTF8Character( int baseAddress, int offset, char newValue );
    public void readUTF8Character( int baseAddress, int offset, DecodedCharacter buf );

    public void writeShort( int baseAddress, int offset, short newValue );
    public short readShort( int baseAddress, int offset );

    public void writeInt( int baseAddress, int offset, int newValue );
    public int readInt( int baseAddress, int offset );

    public void writeLong( int baseAddress, int offset, long newValue );
    public long readLong( int baseAddress, int offset );

    public void writeFloat( int baseAddress, int offset, float newValue );
    public float readFloat( int baseAddress, int offset );

    public void writeDouble( int baseAddress, int offset, double newValue );
    public double readDouble( int baseAddress, int offset );

    public void writeUnsignedByte( int baseAddress, int offset, short newValue );
    public short readUnsignedByte( int baseAddress, int offset );

    public void writeUnsignedShort( int baseAddress, int offset, int newValue );
    public int readUnsignedShort( int baseAddress, int offset );

    public void writeUnsignedInt( int baseAddress, int offset, long newValue );
    public long readUnsignedInt( int baseAddress, int offset );

    public int writeUTF8String( int baseAddress, int offset, CharSequence newValue );
    public int readUTF8String( int baseAddress, int offset, Appendable out );

    public Bytes asBytes( int baseAddress );
}
