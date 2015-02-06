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
abstract class NativeBytes2 extends BaseBytes2 {

    protected NativeBytes2( long base, long maxExc ) {
        super( base, maxExc );
    }

    public void release() {
        QA.isNotZero( base, "The memory has already been freed" );

        super.release();

        this.base   = 0;
        this.maxExc = 0;
    }


    public void fill( long from, long toExc, byte v ) {
        long a        = base+from;
        long numBytes = toExc-from;

        throwIfInvalidAddress( a, numBytes );

        Backdoor.fill( a, numBytes, v );
    }

    public byte readByte( long offset, long maxExc ) {
        long address = index( offset, maxExc, SIZEOF_BYTE );

        return Backdoor.getByte( address );
    }

    public void writeByte( long offset, long maxExc, byte v ) {
        long address = index( offset, maxExc, SIZEOF_BYTE );

        Backdoor.setByte( address, v );
    }

    public short readShort( long offset, long maxExc ) {
        long address = index( offset, maxExc, SIZEOF_SHORT );

        return Backdoor.getShort( address );
    }

    public void writeShort( long offset, long maxExc, short v ) {
        long address = index( offset, maxExc, SIZEOF_SHORT );

        Backdoor.setShort( address, v );
    }

    public char readCharacter( long offset, long maxExc ) {
        long address = index( offset, maxExc, SIZEOF_CHAR );

        return Backdoor.getCharacter( address );
    }

    public void writeCharacter( long offset, long maxExc, char v ) {
        long address = index( offset, maxExc, SIZEOF_CHAR );

        Backdoor.setCharacter( address, v );
    }

    public int readInt( long offset, long maxExc ) {
        long address = index( offset, maxExc, SIZEOF_INT );

        return Backdoor.getInteger( address );
    }

    public void writeInt( long offset, long maxExc, int v ) {
        long address = index( offset, maxExc, SIZEOF_INT );

        Backdoor.setInteger( address, v );
    }

    public long readLong( long offset, long maxExc ) {
        long address = index( offset, maxExc, SIZEOF_LONG );

        return Backdoor.getLong( address );
    }

    public void writeLong( long offset, long maxExc, long v ) {
        long address = index( offset, maxExc, SIZEOF_LONG );

        Backdoor.setLong( address, v );
    }

    public float readFloat( long offset, long maxExc ) {
        long address = index( offset, maxExc, SIZEOF_FLOAT );

        return Backdoor.getFloat( address );
    }

    public void writeFloat( long offset, long maxExc, float v ) {
        long address = index( offset, maxExc, SIZEOF_FLOAT );

        Backdoor.setFloat( address, v );
    }

    public double readDouble( long offset, long maxExc ) {
        long address = index( offset, maxExc, SIZEOF_DOUBLE );

        return Backdoor.getDouble( address );
    }

    public void writeDouble( long offset, long maxExc, double v ) {
        long address = index( offset, maxExc, SIZEOF_DOUBLE );

        Backdoor.setDouble( address, v );
    }

    public short readUnsignedByte( long offset, long maxExc ) {
        long address = index( offset, maxExc, SIZEOF_UNSIGNED_BYTE );

        return Backdoor.getUnsignedByte( address );
    }

    public void writeUnsignedByte( long offset, long maxExc, short v ) {
        long address = index( offset, maxExc, SIZEOF_UNSIGNED_BYTE );

        Backdoor.setUnsignedByte( address, v );
    }

    public int readUnsignedShort( long offset, long maxExc ) {
        long address = index( offset, maxExc, SIZEOF_UNSIGNED_SHORT );

        return Backdoor.getUnsignedShort( address );
    }

    public void writeUnsignedShort( long offset, long maxExc, int v ) {
        long address = index( offset, maxExc, SIZEOF_UNSIGNED_SHORT );

        Backdoor.setUnsignedShort( address, v );
    }

    public long readUnsignedInt( long offset, long maxExc ) {
        long address = index( offset, maxExc, SIZEOF_UNSIGNED_INT );

        return Backdoor.getUnsignedInt( address );
    }

    public void writeUnsignedInt( long offset, long maxExc, long v ) {
        long address = index( offset, maxExc, SIZEOF_UNSIGNED_INT );

        Backdoor.setUnsignedInt( address, v );
    }

    public void readUTF8Character( long offset, long maxExc, DecodedCharacter output ) {
        UTF8Tools.decode( base+offset, base+maxExc, output );
    }

    public int writeUTF8Character( long offset, long maxExc, char c ) {
        long toAddress = base + offset;

        return UTF8Tools.write( toAddress, base+maxExc, c );
    }

    public int readBytes( long offset, long maxExc, Bytes2 destination, long destinationInc, long destinationExc ) {
        return destination.writeBytes( destinationInc, destinationExc, base, offset, maxExc );
    }

    public int writeBytes( long offset, long maxExc, Bytes2 sourceBytes, long sourceInc, long sourceExc ) {
        return sourceBytes.readBytes( sourceInc, sourceExc, base, offset, maxExc );
    }

    public int readBytes( long offset, long maxExc, byte[] destinationArray, long destinationArrayInc, long destinationArrayExc ) {
        long i        = base + offset;
        long max      = Math.min( maxExc, this.maxExc );
        int  numBytes = Backdoor.toInt( Math.min(max, destinationArrayExc - destinationArrayInc) );

        throwIfInvalidIndex( i, base+maxExc, numBytes );

        Backdoor.copyBytes( i, destinationArray, Backdoor.toInt(destinationArrayInc), numBytes );

        return numBytes;
    }

    public int writeBytes( long offset, long maxExc, byte[] sourceArray, long sourceArrayInc, long sourceArrayExc ) {
        long destinationIndex = base + offset;
        int  numBytes         = Backdoor.toInt( sourceArrayExc - sourceArrayInc );

        throwIfInvalidIndex( destinationIndex, base+maxExc, numBytes );

        Backdoor.copyBytes( sourceArray, Backdoor.toInt(sourceArrayInc), destinationIndex, numBytes );

        return numBytes;
    }

    public int readBytes( long offset, long maxExc, long toAddressBase, long toAddressInc, long toAddressExc ) {
        long i        = base + offset;
        long max      = Math.min( maxExc, this.maxExc );
        int  numBytes = Backdoor.toInt( Math.min(max, toAddressExc - toAddressInc) );

        throwIfInvalidIndex( i, base+maxExc, numBytes );
        QA.argIsWithinRange( toAddressBase, toAddressBase+toAddressInc, toAddressBase+toAddressInc+numBytes, toAddressBase+toAddressExc, "toAddressInc", "toAddressExc" );

        Backdoor.copyBytes( i, toAddressBase+toAddressInc, numBytes );

        return numBytes;
    }

    public int writeBytes( long offset, long maxExc, long fromAddressBase, long fromAddressInc, long fromAddressExc ) {
        long destinationIndex = base + offset;
        int  numBytes         = Backdoor.toInt( fromAddressExc - fromAddressInc );
        long fromAddress      = fromAddressBase+fromAddressInc;

        throwIfInvalidIndex( destinationIndex, maxExc, numBytes );

        Backdoor.copyBytes( fromAddress, destinationIndex, numBytes );

        return numBytes;
    }

    public boolean compareBytes( long offset, long maxExc, byte[] targetBytes ) {
        throwIfInvalidIndex( offset, maxExc, targetBytes.length );

        return Backdoor.compareBytes( base+offset, targetBytes );
    }

    private void throwIfInvalidAddress( long address, long numBytes ) {
        if ( SystemX.isDebugRun() ) {
            if ( address < base ) {
                throw new IllegalArgumentException( "Address has under shot the allocated region" );
            } else if ( address+numBytes > maxExc ) {
                throw new IllegalArgumentException( "Address has over shot the allocated region" );
            }
        }
    }

}
