package com.mosaic.io.bytes;

import com.mosaic.lang.QA;
import com.mosaic.lang.reflect.ReflectionUtils;
import com.mosaic.lang.system.Backdoor;
import com.mosaic.lang.system.SystemX;
import com.mosaic.lang.text.DecodedCharacter;
import com.mosaic.lang.text.UTF8Tools;

import java.io.IOException;

import static com.mosaic.lang.system.SystemX.*;


/**
 *
 */
public abstract class NativeBytes extends BaseBytes implements Cloneable {


    private long   baseAddress;
    private long   cacheAlignedBaseAddress;
    private long   maxAddressExc;

    private boolean isOwner = true;



    protected NativeBytes( long baseAddress, long alignedAddress, long maxAddressExc ) {
        resized( baseAddress, alignedAddress, maxAddressExc );
    }

    public Bytes narrow( long fromInc, long toExc ) {
        NativeBytes clone = ReflectionUtils.clone( this );

        clone.isOwner                  = false;
        clone.cacheAlignedBaseAddress += fromInc;
        clone.maxAddressExc            = this.cacheAlignedBaseAddress + toExc;

        return clone;
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
        QA.isTrue( isOwner, "This object does not own the allocated memory" );

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

        throwIfInvalidAddress( address, SIZEOF_BYTE );

        return Backdoor.getByte( address );
    }

    public short readShort( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_SHORT );

        return Backdoor.getShort( address );
    }

    public char readCharacter( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_CHAR );

        return Backdoor.getCharacter( address );
    }

    public int readInt( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_INT );

        return Backdoor.getInteger( address );
    }

    public long readLong( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_LONG );

        return Backdoor.getLong( address );
    }

    public float readFloat( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_FLOAT );

        return Backdoor.getFloat( address );
    }

    public double readDouble( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_DOUBLE );

        return Backdoor.getDouble( address );
    }

    public short readUnsignedByte( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_BYTE );

        return (short) (Backdoor.getByte( address ) & UNSIGNED_BYTE_MASK);
    }

    public int readUnsignedShort( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_SHORT );

        return Backdoor.getShort( address ) & UNSIGNED_SHORT_MASK;
    }

    public long readUnsignedInt( long index ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_INT );

        long v = Backdoor.getInteger( address );

        return v & UNSIGNED_INT_MASK;
    }

    public void writeBoolean( long index, boolean v ) {
        writeByte( index, v ? (byte) 1 : (byte) 0 );
    }

    public void writeByte( long index, byte v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_BYTE );

        Backdoor.setByte( address, v );
    }

    public void writeShort( long index, short v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_SHORT );

        Backdoor.setShort( address, v );
    }

    public void writeCharacter( long index, char v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_CHAR );

        Backdoor.setCharacter( address, v );
    }

    public void writeInt( long index, int v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_INT );

        Backdoor.setInteger( address, v );
    }

    public void writeLong( long index, long v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_LONG );

        Backdoor.setLong( address, v );
    }

    public void writeFloat( long index, float v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_FLOAT );

        Backdoor.setFloat( address, v );
    }

    public void writeDouble( long index, double v ) {
        long address = cacheAlignedBaseAddress + index;

        throwIfInvalidAddress( address, SIZEOF_DOUBLE );

        Backdoor.setDouble( address, v );
    }

    public void writeUnsignedByte( long index, short v ) {
        writeByte( index, (byte) (v & UNSIGNED_BYTE_MASK) );
    }

    public void writeUnsignedShort( long index, int v ) {
        writeShort( index, (short) (v & UNSIGNED_SHORT_MASK) );
    }

    public void writeUnsignedInt( long index, long v ) {
        writeInt( index, (int) (v & UNSIGNED_INT_MASK) );
    }

    public int writeUTF8Character( long destinationIndex, char v ) {
        long toAddress = cacheAlignedBaseAddress+ destinationIndex;

        return UTF8Tools.write( toAddress, maxAddressExc, v );
    }

    public void writeBytes( long destinationIndex, byte[] sourceArray, int sourceFromInc, int sourceToExc ) {
        long toAddress = cacheAlignedBaseAddress+ destinationIndex;
        int  numBytes  = sourceToExc - sourceFromInc;

        throwIfInvalidAddress( toAddress, numBytes );

        Backdoor.copyBytes( sourceArray, sourceFromInc, toAddress, numBytes );
    }

    public void writeBytes( long destinationIndex, long sourceFromAddress, int numBytes ) {
        long toAddress = cacheAlignedBaseAddress+ destinationIndex;

        throwIfInvalidAddress( toAddress, numBytes );

        Backdoor.copyBytes( sourceFromAddress, toAddress, numBytes );
    }

    public void writeBytes( long destinationIndex, Bytes source, long sourceFromInc, long sourceToExc ) {
        QA.argIsBetween( 0, destinationIndex, Integer.MAX_VALUE, "index" );

        long toAddress = cacheAlignedBaseAddress+ destinationIndex;
        source.readBytes( sourceFromInc, toAddress, (int) (destinationIndex + sourceToExc - sourceFromInc) );
    }

    public void writeBytes( Bytes source ) {
        QA.argIsBetween( 0, positionIndex(), Integer.MAX_VALUE, "index" );

        long toAddress = cacheAlignedBaseAddress + positionIndex();
        source.readBytes( 0, toAddress, (int) (positionIndex() + source.bufferLength()) );
    }

    public void writeBytes( Bytes source, long sourceFromInc, long sourceToExc ) {
        QA.argIsBetween( 0, positionIndex(), Integer.MAX_VALUE, "index" );

        long toAddress = cacheAlignedBaseAddress + positionIndex();
        source.readBytes( sourceFromInc, toAddress, (int) (positionIndex() + sourceToExc - sourceFromInc) );
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

        incrementPosition( SIZEOF_BYTE );

        return v;
    }

    public short readShort() {
        short v = readShort( positionIndex() );

        incrementPosition( SIZEOF_SHORT );

        return v;
    }

    public char readCharacter() {
        char v = readCharacter( positionIndex() );

        incrementPosition( SIZEOF_CHAR );

        return v;
    }

    public int readInteger() {
        int v = readInt( positionIndex() );

        incrementPosition( SIZEOF_INT );

        return v;
    }

    public long readLong() {
        long v = readLong( positionIndex() );

        incrementPosition( SIZEOF_LONG );

        return v;
    }

    public float readFloat() {
        float v = readFloat( positionIndex() );

        incrementPosition( SIZEOF_FLOAT );

        return v;
    }

    public double readDouble() {
        double v = readDouble( positionIndex() );

        incrementPosition( SIZEOF_DOUBLE );

        return v;
    }

    public short readUnsignedByte() {
        short v = readUnsignedByte( positionIndex() );

        incrementPosition( SIZEOF_BYTE );

        return v;
    }

    public int readUnsignedShort() {
        int v = readUnsignedShort( positionIndex() );

        incrementPosition( SIZEOF_SHORT );

        return v;
    }

    public long readUnsignedInteger() {
        long v = readUnsignedByte( positionIndex() );

        incrementPosition( SIZEOF_INT );

        return v;
    }

    public void writeBoolean( boolean v ) {
        writeBoolean( positionIndex(), v );

        incrementPosition( SIZEOF_BYTE );
    }

    public void writeByte( byte v ) {
        writeByte( positionIndex(), v );

        incrementPosition( SIZEOF_BYTE );
    }

    public void writeShort( short v ) {
        writeShort( positionIndex(), v );

        incrementPosition( SIZEOF_SHORT );
    }

    public void writeCharacter( char v ) {
        writeCharacter( positionIndex(), v );

        incrementPosition( SIZEOF_CHAR );
    }

    public void writeInteger( int v ) {
        writeInt( positionIndex(), v );

        incrementPosition( SIZEOF_INT );
    }

    public void writeLong( long v ) {
        writeLong( positionIndex(), v );

        incrementPosition( SIZEOF_LONG );
    }

    public void writeFloat( float v ) {
        writeFloat( positionIndex(), v );

        incrementPosition( SIZEOF_FLOAT );
    }

    public void writeDouble( double v ) {
        writeDouble( positionIndex(), v );

        incrementPosition( SIZEOF_DOUBLE );
    }

    public void writeUnsignedByte( short v ) {
        writeUnsignedByte( positionIndex(), v );

        incrementPosition( SIZEOF_BYTE );
    }

    public void writeUnsignedShort( int v ) {
        writeUnsignedShort( positionIndex(), v );

        incrementPosition( SIZEOF_SHORT );
    }

    public void writeUnsignedInteger( long v ) {
        writeUnsignedInt( positionIndex(), v );

        incrementPosition( SIZEOF_LONG );
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

        throwIfInvalidAddress( a, SIZEOF_BYTE );
        throwIfInvalidAddress( b, SIZEOF_BYTE );

        Backdoor.fill( a, numBytes, v );
    }


    public void readUTF8Character( long index, DecodedCharacter output ) {
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
