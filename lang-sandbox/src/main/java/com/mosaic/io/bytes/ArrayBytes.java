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
public class ArrayBytes extends BaseBytes {

    private String name;
    private byte[] array;

    private int min;
    private int maxExc;


    public ArrayBytes( long numBytes ) {
        this( new byte[(int) numBytes] );

        QA.argIsBetween( 0, numBytes, Integer.MAX_VALUE, "numBytes" );
    }

    public ArrayBytes( byte[] array ) {
        this( array, 0, array.length );
    }

    public ArrayBytes( byte[] array, int min, int maxExc ) {
        this.array  = array;
        this.min    = min;
        this.maxExc = maxExc;
    }


    public String name() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }


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

    public int writeUTF8( long index, char v ) {
        QA.argIsLTE( index, Integer.MAX_VALUE, "index" );

        return UTF8Tools.write( array, (int) index, v );
    }


    public void writeBytes( long index, byte[] sourceArray, int fromInc, int toExc ) {
        QA.argIsBetween( 0, index, Integer.MAX_VALUE, "index" );

        Backdoor.copyBytes( sourceArray, fromInc, array, (int) index, toExc-fromInc );
    }

    public void writeBytes( long index, long fromAddress, int numBytes ) {
        QA.argIsBetween( 0, index, Integer.MAX_VALUE, "index" );

        Backdoor.copyBytes( fromAddress, array, (int) index, numBytes );
    }

    public void writeBytes( long fromAddress, int numBytes ) {
        QA.argIsBetween( 0, positionIndex(), Integer.MAX_VALUE, "positionIndex()" );

        Backdoor.copyBytes( fromAddress, array, (int) positionIndex(), numBytes );

        incrementPosition( numBytes );
    }


    public long startIndex() {
        return min;
    }

    public long getEndIndexExc() {
        return maxExc;
    }

    public boolean readBoolean() {
        return readByte() != 0;
    }

    public byte readByte() {
        byte v = readByte( positionIndex() );

        incrementPosition( BYTE_SIZE );

        return v;
    }

    public short readShort() {
        short v = readShort( positionIndex() );

        incrementPosition( SHORT_SIZE );

        return v;
    }

    public char readCharacter() {
        char v = readCharacter( positionIndex() );

        incrementPosition( CHAR_SIZE );

        return v;
    }

    public int readInteger() {
        int v = readInteger( positionIndex() );

        incrementPosition( INT_SIZE );

        return v;
    }

    public long readLong() {
        long v = readLong( positionIndex() );

        incrementPosition( LONG_SIZE );

        return v;
    }

    public float readFloat() {
        float v = readFloat( positionIndex() );

        incrementPosition( FLOAT_SIZE );

        return v;
    }

    public double readDouble() {
        double v = readDouble( positionIndex() );

        incrementPosition( DOUBLE_SIZE );

        return v;
    }

    public short readUnsignedByte() {
        short v = readUnsignedByte( positionIndex() );

        incrementPosition( BYTE_SIZE );

        return v;
    }

    public int readUnsignedShort() {
        int v = readUnsignedShort( positionIndex() );

        incrementPosition( SHORT_SIZE );

        return v;
    }

    public long readUnsignedInteger() {
        long v = readUnsignedByte( positionIndex() );

        incrementPosition( INT_SIZE );

        return v;
    }

    public void writeBoolean( boolean v ) {
        writeBoolean( positionIndex(), v );

        incrementPosition( BYTE_SIZE );
    }

    public void writeByte( byte v ) {
        writeByte( positionIndex(), v );

        incrementPosition( BYTE_SIZE );
    }

    public void writeShort( short v ) {
        writeShort( positionIndex(), v );

        incrementPosition( SHORT_SIZE );
    }

    public void writeCharacter( char v ) {
        writeCharacter( positionIndex(), v );

        incrementPosition( CHAR_SIZE );
    }

    public void writeInteger( int v ) {
        writeInteger( positionIndex(), v );

        incrementPosition( INT_SIZE );
    }

    public void writeLong( long v ) {
        writeLong( positionIndex(), v );

        incrementPosition( LONG_SIZE );
    }

    public void writeFloat( float v ) {
        writeFloat( positionIndex(), v );

        incrementPosition( FLOAT_SIZE );
    }

    public void writeDouble( double v ) {
        writeDouble( positionIndex(), v );

        incrementPosition( DOUBLE_SIZE );
    }

    public void writeUnsignedByte( short v ) {
        writeUnsignedByte( positionIndex(), v );

        incrementPosition( BYTE_SIZE );
    }

    public void writeUnsignedShort( int v ) {
        writeUnsignedShort( positionIndex(), v );

        incrementPosition( SHORT_SIZE );
    }

    public void writeUnsignedInteger( long v ) {
        writeUnsignedInt( positionIndex(), v );

        incrementPosition( LONG_SIZE );
    }


    public void fill( long from, long toExc, byte v ) {
        long a        = min+from;
        long b        = min+toExc-1;
        long numBytes = toExc-from;

        throwIfInvalidIndex( a, BYTE_SIZE );
        throwIfInvalidIndex( b, BYTE_SIZE );

        Backdoor.fillArray( array, a, numBytes, (byte) 0 );
    }


    public void readSingleUTF8Character( long index, DecodedCharacter output ) {
        throwIfInvalidIndex( index, 1 );

        UTF8Tools.decode( array, (int) index, output );
    }

    public int readUTF8String( long index, Appendable out ) {
        throwIfInvalidIndex( index, 2 );

        long from         = index+2;
        int  numUTF8Bytes = readUnsignedShort( index );
        long toExc        = from+numUTF8Bytes;

        DecodedCharacter buf = myDecodedCharacterBuffer();

        throwIfInvalidIndex( from, numUTF8Bytes );

        while( from < toExc ) {
            readSingleUTF8Character( from, buf );

            from += buf.numBytesConsumed;

            try {
                out.append( buf.c );
            } catch ( IOException ex ) {
                Backdoor.throwException( ex );
            }
        }

        assert from == toExc;

        return numUTF8Bytes+2;
    }

    public int readBytes( long index, byte[] destinationArray ) {
        int numBytes = (int) Math.min( remaining(), destinationArray.length );

        throwIfInvalidIndex( index, numBytes );

        Backdoor.copyBytes( array, (int) index, destinationArray, 0, numBytes );

        return numBytes;
    }

    public void readBytes( long index, byte[] destinationArray, int fromInc, int toExc ) {
        QA.argIsGT( toExc, fromInc, "toExc", "fromInc" );
        QA.argIsBetweenInc( 0, fromInc, destinationArray.length, "fromInc" );
        QA.argIsBetweenInc( 0, toExc, destinationArray.length, "toExc" );


        int numBytes = toExc - fromInc;

        throwIfInvalidIndex( index, numBytes );

        Backdoor.copyBytes( array, (int) index, destinationArray, fromInc, numBytes );
    }

    public void readBytes( long index, long toAddress, int numBytes ) {
        throwIfInvalidIndex( index, numBytes );

        Backdoor.copyBytes( array, (int) index, toAddress, numBytes );
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
