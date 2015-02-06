package com.mosaic.bytes2.impl;

import com.mosaic.bytes2.Bytes2;
import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8Tools;


import static com.mosaic.lang.system.SystemX.*;


/**
 *
 */
public class ArrayBytes2 extends BaseBytes2 {

    private byte[] array;


    public ArrayBytes2( String s ) {
        this( s.getBytes(SystemX.UTF8) );
    }

    public ArrayBytes2( long numBytes ) {
        this( new byte[(int) numBytes] );

        QA.argIsBetween( 0, numBytes, Integer.MAX_VALUE, "numBytes" );
    }

    public ArrayBytes2( byte[] array ) {
        this( array, 0, array.length );
    }

    public ArrayBytes2( byte[] array, int offset, int maxExc ) {
        super( offset, maxExc );

        QA.argNotNull( array, "array" );

        this.array  = array;
    }


    public void resize( long newLength ) {
        QA.isInt( newLength, "newLength" );
        QA.argIsGTEZero( newLength, "newLength" );


        byte[] newBackingArray = new byte[(int)newLength];

        long prevSize = sizeBytes();
        if ( prevSize > 0  ) {
            Backdoor.copyBytes( this.array, base, newBackingArray, 0, Math.min(prevSize, newLength) );
        }

        this.array  = newBackingArray;
        this.base   = 0;
        this.maxExc = newBackingArray.length;
    }

    public void fill( long from, long toExc, byte v ) {
        long numBytes = toExc-from;
        long i        = index(from, this.maxExc, numBytes);

        Backdoor.fillArray( array, i, numBytes, v );
    }


    public byte readByte( long offset, long maxExc ) {
        long i = index( offset, maxExc, SIZEOF_BYTE );

        return Backdoor.getByteFrom( array, i );
    }

    public void writeByte( long offset, long maxExc, byte v ) {
        long i = index(offset, maxExc, SIZEOF_BYTE);

        Backdoor.setByteIn( array, i, v );
    }

    public short readShort( long offset, long maxExc ) {
        long i = index( offset, maxExc, SIZEOF_SHORT );

        return Backdoor.getShortFrom( array, i );
    }

    public void writeShort( long offset, long maxExc, short v ) {
        long i = index(offset, maxExc, SIZEOF_SHORT);

        Backdoor.setShortIn( array, i, v );
    }

    public char readCharacter( long offset, long maxExc ) {
        long i = index( offset, maxExc, SIZEOF_CHAR );

        return Backdoor.getCharacterFrom( array, i );
    }

    public void writeCharacter( long offset, long maxExc, char v ) {
        long i = index( offset, maxExc, SIZEOF_CHAR );

        Backdoor.setCharacterIn( array, i, v );
    }

    public int readInt( long offset, long maxExc ) {
        long i = index( offset, maxExc, SIZEOF_INT );

        return Backdoor.getIntegerFrom( array, i );
    }

    public void writeInt( long offset, long maxExc, int v ) {
        long i = index( offset, maxExc, SIZEOF_INT );

        Backdoor.setIntegerIn( array, i, v );
    }

    public long readLong( long offset, long maxExc ) {
        long i = index( offset, maxExc, SIZEOF_LONG );

        return Backdoor.getLongFrom( array, i );
    }

    public void writeLong( long offset, long maxExc, long v ) {
        long i = index( offset, maxExc, SIZEOF_LONG );

        Backdoor.setLongIn( array, i, v );
    }

    public float readFloat( long offset, long maxExc ) {
        long i = index( offset, maxExc, SIZEOF_FLOAT );

        return Backdoor.getFloatFrom( array, i );
    }

    public void writeFloat( long offset, long maxExc, float v ) {
        long i = index( offset, maxExc, SIZEOF_FLOAT );

        Backdoor.setFloatIn( array, i, v );
    }

    public double readDouble( long offset, long maxExc ) {
        long i = index( offset, maxExc, SIZEOF_DOUBLE );

        return Backdoor.getDoubleFrom( array, i );
    }

    public void writeDouble( long offset, long maxExc, double v ) {
        long i = index( offset, maxExc, SIZEOF_DOUBLE );

        Backdoor.setDoubleIn( array, i, v );
    }

    public short readUnsignedByte( long offset, long maxExc ) {
        long i = index( offset, maxExc, SIZEOF_BYTE );

        return Backdoor.getUnsignedByteFrom( array, i );
    }

    public void writeUnsignedByte( long offset, long maxExc, short v ) {
        long i = index( offset, maxExc, SIZEOF_BYTE );

        Backdoor.setUnsignedByteIn( array, i, v );
    }

    public int readUnsignedShort( long offset, long maxExc ) {
        long i = index( offset, maxExc, SIZEOF_SHORT );

        return Backdoor.getUnsignedShortFrom( array, i );
    }

    public void writeUnsignedShort( long offset, long maxExc, int v ) {
        long i = index( offset, maxExc, SIZEOF_SHORT );

        Backdoor.setUnsignedShortIn( array, i, v );
    }

    public long readUnsignedInt( long offset, long maxExc ) {
        long i = index( offset, maxExc, SIZEOF_INT );

        return Backdoor.getUnsignedIntegerFrom( array, i );
    }
    //96 Lynn road (up the chase)
    public void writeUnsignedInt( long offset, long maxExc, long v ) {
        long i = index( offset, maxExc, SIZEOF_INT );

        Backdoor.setUnsignedIntegerIn( array, i, v );
    }

    public void readUTF8Character( long offset, long maxExc, DecodedCharacter output ) {
        long i = index( offset, maxExc, SIZEOF_BYTE );  // we don't know the size yet, so under estimate and recheck later

        UTF8Tools.decode( array, (int) i, output );

        if ( SystemX.isDebugRun() ) {
            index( offset, maxExc, output.numBytesConsumed );
        }
    }

    public int writeUTF8Character( long offset, long maxExc, char c ) {
        long i = index( offset, maxExc, SIZEOF_BYTE );

        int numBytesUsed = UTF8Tools.write( array, (int) i, c );

        if ( SystemX.isDebugRun() ) {
            index( offset, maxExc, numBytesUsed );
        }

        return numBytesUsed;
    }

    public int readBytes( long offset, long maxExc, Bytes2 destination, long destinationInc, long destinationExc ) {
        return destination.writeBytes( destinationInc, destinationExc, array, offset, maxExc );
    }

    public int writeBytes( long offset, long maxExc, Bytes2 sourceBytes, long sourceInc, long sourceExc ) {
        return sourceBytes.readBytes( sourceInc, sourceExc, array, offset, maxExc );
    }

    public int readBytes( long offset, long maxExc, byte[] destinationArray, long destinationArrayInc, long destinationArrayExc ) {
        int  i        = Backdoor.toInt( base + offset );
        long max      = Math.min( maxExc, this.maxExc );
        int  numBytes = Backdoor.toInt( Math.min(max, destinationArrayExc - destinationArrayInc) );

        throwIfInvalidIndex( i, maxExc, numBytes );

        Backdoor.copyBytes( array, i, destinationArray, destinationArrayInc, numBytes );

        return numBytes;
    }

    public int writeBytes( long offset, long maxExc, byte[] sourceArray, long sourceArrayInc, long sourceArrayExc ) {
        int destinationIndex = Backdoor.toInt( base + offset );
        int numBytes         = Backdoor.toInt( sourceArrayExc - sourceArrayInc );

        throwIfInvalidIndex( destinationIndex, maxExc, numBytes );

        Backdoor.copyBytes( sourceArray, sourceArrayInc, array, destinationIndex, numBytes );

        return numBytes;
    }

    public int readBytes( long offset, long maxExc, long toAddressBase, long toAddressInc, long toAddressExc ) {
        int  i        = Backdoor.toInt( base + offset );
        long max      = Math.min( maxExc, this.maxExc );
        int  numBytes = Backdoor.toInt( Math.min(max, toAddressExc - toAddressInc) );

        throwIfInvalidIndex( i, maxExc, numBytes );
        throwIfInvalidIndex( toAddressBase+toAddressInc, toAddressExc, numBytes );

        Backdoor.copyBytes( array, i, toAddressBase+toAddressInc, numBytes );

        return numBytes;
    }

    public int writeBytes( long offset, long maxExc, long fromAddressBase, long fromAddressInc, long fromAddressExc ) {
        int  destinationIndex = Backdoor.toInt( base + offset );
        int  numBytes         = Backdoor.toInt( fromAddressExc - fromAddressInc );
        long fromAddress      = fromAddressBase+fromAddressInc;

        throwIfInvalidIndex( destinationIndex, maxExc, numBytes );

        Backdoor.copyBytes( fromAddress, array, destinationIndex, numBytes );

        return numBytes;
    }

    public boolean compareBytes( long offset, long maxExc, byte[] targetBytes ) {
        int fromIndex = Backdoor.toInt( base + offset );

        throwIfInvalidIndex( fromIndex, Math.min(this.maxExc,maxExc), targetBytes.length );

        return Backdoor.compareBytes( array, fromIndex, targetBytes );
    }

}
