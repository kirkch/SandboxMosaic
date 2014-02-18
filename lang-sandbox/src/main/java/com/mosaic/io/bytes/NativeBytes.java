package com.mosaic.io.bytes;

import com.mosaic.lang.Backdoor;
import com.mosaic.lang.SystemX;
import com.mosaic.lang.Validate;

import static com.mosaic.lang.SystemX.*;


/**
 *
 */
public class NativeBytes implements Bytes {

    private static final int  UNSIGNED_BYTE_MASK  = 0xFF;
    private static final int  UNSIGNED_SHORT_MASK = 0xFFFF;
    private static final long UNSIGNED_INT_MASK   = 0xFFFFFFFF;


    /**
     * Reserves n bytes of memory.  The bytes are not guaranteed to be zero'd out,
     * so they may hold junk in them.
     */
    public static Bytes alloc( long numBytes ) {
        return alloc( numBytes, SystemX.getCacheLineLengthBytes() );
    }

    /**
     * Reserves n bytes of memory.  The bytes are not guaranteed to be zero'd out,
     * so they may hold junk in them.
     */
    public static Bytes alloc( long numBytes, int cpuCacheLineSizeBytes ) {
        Validate.argIsGTZero( numBytes, "numBytes" );

        long baseAddress    = Backdoor.alloc( numBytes + cpuCacheLineSizeBytes );
        long alignedAddress = Backdoor.alignAddress( baseAddress, cpuCacheLineSizeBytes );

        return new NativeBytes( baseAddress, alignedAddress, alignedAddress+numBytes );
    }



    private long baseAddress;
    private long cacheAlignedBaseAddress;
    private long maxAddressExc;

    private long positionIndex;
//    private long watermarkIndexExc;


    private NativeBytes( long baseAddress, long alignedAddress, long maxAddressExc ) {
        this.baseAddress             = baseAddress;
        this.cacheAlignedBaseAddress = alignedAddress;
        this.maxAddressExc           = maxAddressExc;
    }


    public long getBaseAddress() {
        return cacheAlignedBaseAddress;
    }


    public void release() {
        Validate.isNotZero( baseAddress, "The memory has already been freed" );

        Backdoor.free( baseAddress );

        this.baseAddress             = 0;
        this.cacheAlignedBaseAddress = 0;
        this.maxAddressExc           = 0;

        this.positionIndex           = 0;
    }

    public boolean readBoolean( long index ) {
        byte v = readByte( index );

        return v != 0;
    }

    public byte readByte( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, BYTE_SIZE );

        return Backdoor.getByte( address );
    }

    public short readShort( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SHORT_SIZE );

        return Backdoor.getShort( address );
    }

    public char readCharacter( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, CHAR_SIZE );

        return Backdoor.getCharacter( address );
    }

    public int readInteger( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, INT_SIZE );

        return Backdoor.getInteger( address );
    }

    public long readLong( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, LONG_SIZE );

        return Backdoor.getLong( address );
    }

    public float readFloat( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, FLOAT_SIZE );

        return Backdoor.getFloat( address );
    }

    public double readDouble( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, DOUBLE_SIZE );

        return Backdoor.getDouble( address );
    }

    public short readUnsignedByte( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, BYTE_SIZE );

        return (short) (Backdoor.getByte( address ) & UNSIGNED_BYTE_MASK);
    }

    public int readUnsignedShort( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SHORT_SIZE );

        return Backdoor.getShort( address ) & UNSIGNED_SHORT_MASK;
    }

    public long readUnsignedInteger( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, INT_SIZE );

        long v = Backdoor.getInteger( address );

        return v & UNSIGNED_INT_MASK;
    }

    public void writeBoolean( long index, boolean v ) {
        writeByte( index, v ? (byte) 1 : (byte) 0 );
    }

    public void writeByte( long index, byte v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, BYTE_SIZE );

        Backdoor.setByte( address, v );
    }

    public void writeShort( long index, short v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SHORT_SIZE );

        Backdoor.setShort( address, v );
    }

    public void writeCharacter( long index, char v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, CHAR_SIZE );

        Backdoor.setCharacter( address, v );
    }

    public void writeInteger( long index, int v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, INT_SIZE );

        Backdoor.setInteger( address, v );
    }

    public void writeLong( long index, long v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, LONG_SIZE );

        Backdoor.setLong( address, v );
    }

    public void writeFloat( long index, float v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, FLOAT_SIZE );

        Backdoor.setFloat( address, v );
    }

    public void writeDouble( long index, double v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, DOUBLE_SIZE );

        Backdoor.setDouble( address, v );
    }

    public void writeUnsignedByte( long index, short v ) {
        writeByte( index, (byte) (v & UNSIGNED_BYTE_MASK) );
    }

    public void writeUnsignedShort( long index, int v ) {
        writeShort( index, (short) (v & UNSIGNED_SHORT_MASK) );
    }

    public void writeUnsignedInt( long index, long v ) {
        writeInteger( index, (int) (v & UNSIGNED_INT_MASK) );
    }


    public long startIndex() {
        return 0;
    }

    public long endIndexExc() {
        return maxAddressExc - cacheAlignedBaseAddress;
    }

    public long positionIndex() {
        return positionIndex;
    }

    public void positionIndex( long newIndex ) {
        Validate.argIsBetween( startIndex(), newIndex, endIndexExc(), "newIndex" );

        this.positionIndex = newIndex;
    }

    public void rewindPositionIndex() {
        this.positionIndex = startIndex();
    }

    public long remaining() {
        return endIndexExc() - positionIndex();
    }

    public long size() {
        return endIndexExc() - startIndex();
    }

    public boolean readBoolean() {
        return readByte() != 0;
    }

    public byte readByte() {
        byte v = readByte( positionIndex );

        this.positionIndex += BYTE_SIZE;

        return v;
    }

    public short readShort() {
        short v = readShort( positionIndex );

        this.positionIndex += SHORT_SIZE;

        return v;
    }

    public char readCharacter() {
        char v = readCharacter( positionIndex );

        this.positionIndex += CHAR_SIZE;

        return v;
    }

    public int readInteger() {
        int v = readInteger( positionIndex );

        this.positionIndex += INT_SIZE;

        return v;
    }

    public long readLong() {
        long v = readLong( positionIndex );

        this.positionIndex += LONG_SIZE;

        return v;
    }

    public float readFloat() {
        float v = readFloat( positionIndex );

        this.positionIndex += FLOAT_SIZE;

        return v;
    }

    public double readDouble() {
        double v = readDouble( positionIndex );

        this.positionIndex += DOUBLE_SIZE;

        return v;
    }

    public short readUnsignedByte() {
        short v = readUnsignedByte( positionIndex );

        this.positionIndex += BYTE_SIZE;

        return v;
    }

    public int readUnsignedShort() {
        int v = readUnsignedShort( positionIndex );

        this.positionIndex += SHORT_SIZE;

        return v;
    }

    public long readUnsignedInteger() {
        long v = readUnsignedByte( positionIndex );

        this.positionIndex += INT_SIZE;

        return v;
    }

    public void writeBoolean( boolean v ) {
        writeBoolean( positionIndex, v );

        this.positionIndex += BYTE_SIZE;
    }

    public void writeByte( byte v ) {
        writeByte( positionIndex, v );

        this.positionIndex += BYTE_SIZE;
    }

    public void writeShort( short v ) {
        writeShort( positionIndex, v );

        this.positionIndex += SHORT_SIZE;
    }

    public void writeCharacter( char v ) {
        writeCharacter( positionIndex, v );

        this.positionIndex += CHAR_SIZE;
    }

    public void writeInteger( int v ) {
        writeInteger( positionIndex, v );

        this.positionIndex += INT_SIZE;
    }

    public void writeLong( long v ) {
        writeLong( positionIndex, v );

        this.positionIndex += LONG_SIZE;
    }

    public void writeFloat( float v ) {
        writeFloat( positionIndex, v );

        this.positionIndex += FLOAT_SIZE;
    }

    public void writeDouble( double v ) {
        writeDouble( positionIndex, v );

        this.positionIndex += DOUBLE_SIZE;
    }

    public void writeUnsignedByte( short v ) {
        writeUnsignedByte( positionIndex, v );

        this.positionIndex += BYTE_SIZE;
    }

    public void writeUnsignedShort( int v ) {
        writeUnsignedShort( positionIndex, v );

        this.positionIndex += SHORT_SIZE;
    }

    public void writeUnsignedInteger( long v ) {
        writeUnsignedInt( positionIndex, v );

        this.positionIndex += LONG_SIZE;
    }


    public void fill( long from, long toExc, byte v ) {
        long a        = cacheAlignedBaseAddress+from;
        long b        = cacheAlignedBaseAddress+toExc-1;
        long numBytes = toExc-from;

        throwIfInvalidAddress( a, BYTE_SIZE );
        throwIfInvalidAddress( b, BYTE_SIZE );

        Backdoor.fill( a, numBytes, (byte) 0 );
    }



    private void throwIfInvalidAddress( long address, int numBytes ) {
        if ( SystemX.isDebugRun() ) {
            if ( address < cacheAlignedBaseAddress ) {
                throw new IllegalArgumentException( "Address has under shot the allocated region" );
            } else if ( address+numBytes > maxAddressExc ) {
                throw new IllegalArgumentException( "Address has over shot the allocated region" );
            }
        }
    }
}
