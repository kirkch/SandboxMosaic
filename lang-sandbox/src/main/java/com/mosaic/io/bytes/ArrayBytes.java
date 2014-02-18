package com.mosaic.io.bytes;

import com.mosaic.lang.Backdoor;
import com.mosaic.lang.SystemX;
import com.mosaic.lang.Validate;

import static com.mosaic.lang.SystemX.*;
import static com.mosaic.lang.SystemX.BYTE_SIZE;
import static com.mosaic.lang.SystemX.LONG_SIZE;


/**
 *
 */
public class ArrayBytes  implements Bytes {

    private byte[] array;

    private int min;
    private int maxExc;

    private int positionIndex;


    public ArrayBytes( long numBytes ) {
        this( new byte[(int) numBytes] );

        Validate.argIsBetween( 0, numBytes, Integer.MAX_VALUE, "numBytes" );
    }

    public ArrayBytes( byte[] array ) {
        this( array, 0, array.length );
    }

    public ArrayBytes( byte[] array, int min, int maxExc ) {
        this.array  = array;
        this.min    = min;
        this.maxExc = maxExc;
    }


    public void release() {}

    public boolean readBoolean( long index ) {
        byte v = readByte( index );

        return v != 0;
    }

    public byte readByte( long index ) {
        long i = min + index;

        throwIfInvalidIndex( index, BYTE_SIZE );

        return Backdoor.getByteFrom( array, i );
    }

    public short readShort( long index ) {
        long i = min + index;

        throwIfInvalidIndex( index, SHORT_SIZE );

        return Backdoor.getShortFrom( array, i );
    }

    public char readCharacter( long index ) {
        long i = min + index;

        throwIfInvalidIndex( index, CHAR_SIZE );

        return Backdoor.getCharacterFrom( array, i );
    }

    public int readInteger( long index ) {
        long i = min + index;

        throwIfInvalidIndex( index, INT_SIZE );

        return Backdoor.getIntegerFrom( array, i );
    }

    public long readLong( long index ) {
        long i = min + index;

        throwIfInvalidIndex( index, LONG_SIZE );

        return Backdoor.getLongFrom( array, i );
    }

    public float readFloat( long index ) {
        long i = min + index;

        throwIfInvalidIndex( index, FLOAT_SIZE );

        return Backdoor.getFloatFrom( array, i );
    }

    public double readDouble( long index ) {
        long i = min + index;

        throwIfInvalidIndex( index, DOUBLE_SIZE );

        return Backdoor.getDoubleFrom( array, i );
    }

    public short readUnsignedByte( long index ) {
        long i = min + index;

        throwIfInvalidIndex( i, BYTE_SIZE );

        return (short) (Backdoor.getByteFrom(array,i) & UNSIGNED_BYTE_MASK);
    }

    public int readUnsignedShort( long index ) {
        long i = min + index;

        throwIfInvalidIndex( i, SHORT_SIZE );

        return Backdoor.getShortFrom(array,i) & UNSIGNED_SHORT_MASK;
    }

    public long readUnsignedInteger( long index ) {
        long i = min + index;

        throwIfInvalidIndex( i, INT_SIZE );

        long v = Backdoor.getIntegerFrom(array,i);

        return v & UNSIGNED_INT_MASK;
    }

    public void writeBoolean( long index, boolean v ) {
        writeByte( index, v ? (byte) 1 : (byte) 0 );
    }

    public void writeByte( long index, byte v ) {
        long i = min + index;

        throwIfInvalidIndex( i, BYTE_SIZE );

        Backdoor.setByteIn( array, i, v );
    }

    public void writeShort( long index, short v ) {
        long i = min + index;

        throwIfInvalidIndex( i, SHORT_SIZE );

        Backdoor.setShortIn( array, i, v );
    }

    public void writeCharacter( long index, char v ) {
        long i = min + index;

        throwIfInvalidIndex( i, CHAR_SIZE );

        Backdoor.setCharacterIn( array, i, v );
    }

    public void writeInteger( long index, int v ) {
        long i = min + index;

        throwIfInvalidIndex( i, INT_SIZE );

        Backdoor.setIntegerIn( array, i, v );
    }

    public void writeLong( long index, long v ) {
        long i = min + index;

        throwIfInvalidIndex( i, LONG_SIZE );

        Backdoor.setLongIn( array, i, v );
    }

    public void writeFloat( long index, float v ) {
        long i = min + index;

        throwIfInvalidIndex( i, FLOAT_SIZE );

        Backdoor.setFloatIn( array, i, v );
    }

    public void writeDouble( long index, double v ) {
        long i = min + index;

        throwIfInvalidIndex( i, DOUBLE_SIZE );

        Backdoor.setDoubleIn( array, i, v );
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
        return min;
    }

    public long endIndexExc() {
        return maxExc;
    }

    public long positionIndex() {
        return positionIndex;
    }

    public void positionIndex( long newIndex ) {
        Validate.argIsBetween( startIndex(), newIndex, endIndexExc(), "newIndex" );

        this.positionIndex = (int) newIndex;
    }

    public void rewindPositionIndex() {
        this.positionIndex = min;
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
        long a        = min+from;
        long b        = min+toExc-1;
        long numBytes = toExc-from;

        throwIfInvalidIndex( a, BYTE_SIZE );
        throwIfInvalidIndex( b, BYTE_SIZE );

        Backdoor.fillArray( array, a, numBytes, (byte) 0 );
    }



    private void throwIfInvalidIndex( long address, int numBytes ) {
        if ( SystemX.isDebugRun() ) {
            if ( address < min ) {
                throw new IllegalArgumentException( "Address has under shot the allocated region" );
            } else if ( address+numBytes > maxExc ) {
                throw new IllegalArgumentException( "Address has over shot the allocated region" );
            }
        }
    }
}
