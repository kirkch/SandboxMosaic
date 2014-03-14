package com.mosaic.io.bytes;

import com.mosaic.lang.QA;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8Tools;

import java.io.IOException;

import static com.mosaic.lang.system.SystemX.*;


/**
 *
 */
public abstract class NativeBytes extends BaseBytes {


    private long   baseAddress;
    private long   cacheAlignedBaseAddress;
    private long   maxAddressExc;




    protected NativeBytes( long baseAddress, long alignedAddress, long maxAddressExc ) {
        resized( baseAddress, alignedAddress, maxAddressExc );
    }

    protected void resized( long baseAddress, long alignedAddress, long maxAddressExc ) {
        this.baseAddress             = baseAddress;
        this.cacheAlignedBaseAddress = alignedAddress;
        this.maxAddressExc           = maxAddressExc;
    }


    public long getBaseAddress() {
        return cacheAlignedBaseAddress;
    }


    public void release() {
        QA.isNotZero( baseAddress, "The memory has already been freed" );

        super.release();

        this.baseAddress             = 0;
        this.cacheAlignedBaseAddress = 0;
        this.maxAddressExc           = 0;
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

    public int writeUTF8( long index, char v ) {
        long toAddress = cacheAlignedBaseAddress+index;

        return UTF8Tools.write( toAddress, maxAddressExc, v );
    }

    public void writeBytes( long index, byte[] sourceArray, int fromInc, int toExc ) {
        long toAddress = cacheAlignedBaseAddress+index;
        int  numBytes  = toExc-fromInc;

        throwIfInvalidAddress( toAddress, numBytes );

        Backdoor.copyBytes( sourceArray, fromInc, toAddress, numBytes );
    }

    public void writeBytes( long index, long fromAddress, int numBytes ) {
        long toAddress = cacheAlignedBaseAddress+index;

        throwIfInvalidAddress( toAddress, numBytes );

        Backdoor.copyBytes( fromAddress, toAddress, numBytes );
    }


    public long startIndex() {
        return 0;
    }

    public long getEndIndexExc() {
        return maxAddressExc - cacheAlignedBaseAddress;
    }


    public boolean readBoolean() {
        return readByte() != 0;
    }

    public byte readByte() {
        byte v = readByte( positionIndex() );

        incrementPosition(  BYTE_SIZE );

        return v;
    }

    public short readShort() {
        short v = readShort( positionIndex() );

        incrementPosition(  SHORT_SIZE );

        return v;
    }

    public char readCharacter() {
        char v = readCharacter( positionIndex() );

        incrementPosition(  CHAR_SIZE );

        return v;
    }

    public int readInteger() {
        int v = readInteger( positionIndex() );

        incrementPosition(  INT_SIZE );

        return v;
    }

    public long readLong() {
        long v = readLong( positionIndex() );

        incrementPosition(  LONG_SIZE );

        return v;
    }

    public float readFloat() {
        float v = readFloat( positionIndex() );

        incrementPosition(  FLOAT_SIZE );

        return v;
    }

    public double readDouble() {
        double v = readDouble( positionIndex() );

        incrementPosition(  DOUBLE_SIZE );

        return v;
    }

    public short readUnsignedByte() {
        short v = readUnsignedByte( positionIndex() );

        incrementPosition(  BYTE_SIZE );

        return v;
    }

    public int readUnsignedShort() {
        int v = readUnsignedShort( positionIndex() );

        incrementPosition(  SHORT_SIZE );

        return v;
    }

    public long readUnsignedInteger() {
        long v = readUnsignedByte( positionIndex() );

        incrementPosition(  INT_SIZE );

        return v;
    }

    public void writeBoolean( boolean v ) {
        writeBoolean( positionIndex(), v );

        incrementPosition(  BYTE_SIZE );
    }

    public void writeByte( byte v ) {
        writeByte( positionIndex(), v );

        incrementPosition(  BYTE_SIZE );
    }

    public void writeShort( short v ) {
        writeShort( positionIndex(), v );

        incrementPosition(  SHORT_SIZE );
    }

    public void writeCharacter( char v ) {
        writeCharacter( positionIndex(), v );

        incrementPosition(  CHAR_SIZE );
    }

    public void writeInteger( int v ) {
        writeInteger( positionIndex(), v );

        incrementPosition(  INT_SIZE );
    }

    public void writeLong( long v ) {
        writeLong( positionIndex(), v );

        incrementPosition(  LONG_SIZE );
    }

    public void writeFloat( float v ) {
        writeFloat( positionIndex(), v );

        incrementPosition(  FLOAT_SIZE );
    }

    public void writeDouble( double v ) {
        writeDouble( positionIndex(), v );

        incrementPosition(  DOUBLE_SIZE );
    }

    public void writeUnsignedByte( short v ) {
        writeUnsignedByte( positionIndex(), v );

        incrementPosition(  BYTE_SIZE );
    }

    public void writeUnsignedShort( int v ) {
        writeUnsignedShort( positionIndex(), v );

        incrementPosition(  SHORT_SIZE );
    }

    public void writeUnsignedInteger( long v ) {
        writeUnsignedInt( positionIndex(), v );

        incrementPosition(  LONG_SIZE );
    }

    public void writeBytes( long fromAddress, int numBytes ) {
        long toAddress = cacheAlignedBaseAddress + positionIndex();

        throwIfInvalidAddress( toAddress, numBytes );

        Backdoor.copyBytes( fromAddress, toAddress, numBytes );

        incrementPosition( numBytes );
    }


    public void fill( long from, long toExc, byte v ) {
        long a        = cacheAlignedBaseAddress+from;
        long b        = cacheAlignedBaseAddress+toExc-1;
        long numBytes = toExc-from;

        throwIfInvalidAddress( a, BYTE_SIZE );
        throwIfInvalidAddress( b, BYTE_SIZE );

        Backdoor.fill( a, numBytes, v );
    }


    public void readSingleUTF8Character( long index, DecodedCharacter output ) {
        UTF8Tools.decode( cacheAlignedBaseAddress+index, maxAddressExc, output );
    }

    public int readUTF8String( long index, Appendable out ) {
        long lengthAddress = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( lengthAddress, 2 );

        int numUTF8Bytes = Backdoor.getUnsignedByte( lengthAddress );

        long utf8BytesAddress = cacheAlignedBaseAddress + index + 2;

        throwIfInvalidAddress( utf8BytesAddress, numUTF8Bytes );


        DecodedCharacter buf = myDecodedCharacterBuffer();

        int count = 0;
        long maxAddressExc = utf8BytesAddress + numUTF8Bytes;
        long nextPtr = utf8BytesAddress;
        while ( nextPtr < maxAddressExc ) {
            UTF8Tools.decode( nextPtr, maxAddressExc, buf );

            nextPtr += buf.numBytesConsumed;

            try {
                out.append( buf.c );
            } catch ( IOException ex ) {
                Backdoor.throwException( ex );
            }
        }

        assert nextPtr == maxAddressExc;

        return count;
    }

    public int readBytes( long index, byte[] destinationArray ) {
        int numBytes = (int) Math.min( remaining(), destinationArray.length );

        long ptr = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( ptr, numBytes );

        Backdoor.copyBytes( ptr, destinationArray, 0, numBytes );

        return numBytes;
    }

    public void readBytes( long index, byte[] destinationArray, int fromInc, int toExc ) {
        QA.argIsGT( toExc, fromInc, "toExc", "fromInc" );
        QA.argIsBetweenInc( 0, fromInc, destinationArray.length, "fromInc" );
        QA.argIsBetweenInc( 0, toExc, destinationArray.length, "toExc" );


        long ptr = cacheAlignedBaseAddress + index;
        int numBytes = toExc - fromInc;

        throwIfInvalidAddress( ptr, numBytes );

        Backdoor.copyBytes( ptr, destinationArray, fromInc, numBytes );
    }

    public void readBytes( long index, long toAddress, int numBytes ) {
        long ptr = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( ptr, numBytes );

        Backdoor.copyBytes( ptr, toAddress, numBytes );
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
